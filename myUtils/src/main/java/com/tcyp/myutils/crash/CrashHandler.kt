package com.tcyp.myutils.crash

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Process
import android.util.Log
import com.tcyp.myutils.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.logging.Logger
import kotlin.system.exitProcess

/**
 * 全局异常捕获处理器，用于捕获未被处理的异常并记录日志。
 *
 * 实现了 [Thread.UncaughtExceptionHandler] 接口，在应用发生未捕获异常时，
 * 将异常信息保存至文件，并可选地执行自定义回调（如上传日志）。
 */
object CrashHandler: Thread.UncaughtExceptionHandler {
    private const val TAG = "CrashHandler"
    private const val CRASH_DIR_NAME = "crash_logs"
    //最大保留奔溃文件数，超过最大数量时，将最旧的崩溃日志删除
    private var MAX_CRASH_LOG_FILES = 10
    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    // 防止内存泄露，使用弱引用持有 Context
    private var contextRef: WeakReference<Context>? = null

    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    // 崩溃后的回调接口，可用于上传日志等操作，默认为空
    private var onCrashListener: ((File) -> Unit)? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * 初始化崩溃处理器。（必须在 Application.onCreate() 中调用）
     *
     * @param context 应用上下文，建议传入 Application Context。
     * @param onCrash 可选的崩溃监听器，当崩溃发生且日志成功保存后调用该回调。
     */
    fun init(context: Context, maxFiles: Int = MAX_CRASH_LOG_FILES,onCrash: ((File) -> Unit)? = null) {
        MAX_CRASH_LOG_FILES = maxFiles.coerceIn(0, 100)
        if (MAX_CRASH_LOG_FILES <= 0) return

        this.onCrashListener = onCrash
        contextRef = WeakReference( context.applicationContext)

        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        //初始化的时候清理一次旧日志
        cleanOldLogs( context)
        Log.i(TAG, "init")
    }

    /**
     * 当线程抛出未被捕获的异常时触发此方法。
     *
     * 此方法会尝试将异常堆栈信息写入本地日志文件，并在完成后终止当前进程。
     *
     * @param t 发生异常的线程。
     * @param e 抛出的异常对象。
     */
    override fun uncaughtException(t: Thread, e: Throwable) {
       val ctx = contextRef?.get()?: run {
           defaultHandler?.uncaughtException(t, e)
           return
       }
        Log.e(TAG, "未捕获到异常")
        // 保存日志 + 自动清理旧日志
        val crashFile = saveCrashLog(ctx, e)
        crashFile?.let { onCrashListener?.invoke(it) }

        // 交给系统默认处理器（避免某些极端情况 ANR）
        defaultHandler?.takeIf { it !== this }?.uncaughtException(t, e)

        // 延迟退出，确保日志写完
        try {
            Thread.sleep(1500)
        } catch (ignored: InterruptedException) { }
        //退出程序
        Process.killProcess(Process.myPid())
        exitProcess(1)
    }

    /**
     * 保存崩溃日志到指定目录下的文件中。
     *
     * 日志包括设备信息、应用版本号、时间戳以及完整的异常堆栈跟踪。
     *
     * @param context 上下文环境。
     * @param throwable 异常对象。
     * @return 成功则返回日志文件对象；否则返回 null。
     */
    @SuppressLint("SimpleDateFormat")
    private fun saveCrashLog(context: Context, throwable: Throwable): File? {
        //清理旧日志
        cleanOldLogs(context)
        val crashTime = DATE_FORMAT.format(Date())
        val appUtils = AppUtils.getInstance(context)
        val log = buildString {
            append("=============== Crash Report ===============\n")
            append("Time       : $crashTime\n")
            append("Device     : ${Build.MANUFACTURER} ${Build.MODEL}\n")
            append("Android    : ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
            append("App Version: ${appUtils.localVersionName} (${appUtils.localVersionCode})\n")
            append("Thread     : ${Thread.currentThread().name}\n\n")
            append("StackTrace :\n")
            append(getStackTrace(throwable))
            append("\n===========================================\n")
        }

        return try {
            val dir = File(context.getExternalFilesDir(null) ?: context.filesDir, CRASH_DIR_NAME)
            dir.mkdirs()
            val file = File(dir, "crash_$crashTime.log")
            file.writeText(log)
            Log.i(TAG, "崩溃日志已保存: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "保存崩溃日志失败", e)
            null
        }
    }

    /**
     * 获取指定异常及其所有嵌套原因的完整堆栈追踪字符串。
     *
     * @param t 要获取堆栈的异常对象。
     * @return 包含完整堆栈信息的字符串。
     */
    private fun getStackTrace(t: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        t.printStackTrace(pw)
        var cause = t.cause
        while (cause != null) {
            cause.printStackTrace(pw)
            cause = cause.cause
        }
        pw.close()
        return sw.toString()
    }

    /**
     * 自动清理旧崩溃日志。
     *
     * 删除超过指定数量的崩溃日志，保持最新的指定数量的崩溃日志。
     *
     * @param context 应用上下文。
     */
    private fun cleanOldLogs(context: Context) {
        val dir = File(context.getExternalFilesDir(null) ?: context.filesDir, CRASH_DIR_NAME)
        if (!dir.exists() || !dir.isDirectory) return
        val files = dir.listFiles()
            ?.filter{ it.isFile && it.name.startsWith("crash_") && it.name.endsWith(".log")}
            ?.sortedBy { it.lastModified() } // 按修改时间升序，删除最旧的崩溃日志,最旧的在最前面
            ?.takeIf { files -> files.size > MAX_CRASH_LOG_FILES}?:return

        val fileToDelete = files.take(files.size - MAX_CRASH_LOG_FILES)
        fileToDelete.forEach { oldFile ->
            oldFile.delete()
            Log.i(TAG, "已删除旧崩溃日志: ${oldFile.name}")
        }
    }

    /**
     * 主动抛出一个测试用的运行时异常，用于验证崩溃日志功能是否正常工作。
     */
    fun testCrash() {
        throw RuntimeException("CrashKeeper 测试崩溃")
    }
}

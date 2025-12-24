package com.tcyp.myutils

import android.util.Log

object LogUtils {
    private const val DEFAULT_TAG = "LogUtils"
    private const val MAX_LOG_LENGTH = 3500
    //是否是Debug模式
    private var isDebug = AppUtils.getInstance().isDebug
    var logEnable = true

    fun setLogEnable(enable: Boolean) {
        logEnable = enable
    }

    val isLogEnable: Boolean
        get() = isDebug && logEnable

    fun v(tag: String = DEFAULT_TAG, msg: String) {
        if (isLogEnable) {
            logChunk(Log.VERBOSE, tag, msg)
        }
    }

    fun d(tag: String = DEFAULT_TAG, msg: String) {
        if (isLogEnable) {
            logChunk(Log.DEBUG, tag, msg)
        }
    }

    fun i(tag: String = DEFAULT_TAG, msg: String) {
        if (isLogEnable) {
            logChunk(Log.INFO, tag, msg)
        }
    }


    fun e(tag: String = DEFAULT_TAG, msg: String) {
        if (isLogEnable) {
            logChunk(Log.ERROR, tag, msg)
        }
    }

    fun w(tag: String = DEFAULT_TAG, msg: String) {
        if (isLogEnable) {
            logChunk(Log.WARN, tag, msg)
        }
    }

    /**
    * 分块记录日志消息
    * 当消息长度超过最大日志长度限制时，将消息分割成多个块进行记录
    *
    * @param priority 日志优先级级别（如Log.DEBUG, Log.INFO等）
    * @param tag 用于标识日志来源的标签
    * @param msg 需要记录的消息内容
     */
    private fun logChunk(priority: Int, tag: String, msg: String) {
        if (msg.length <= MAX_LOG_LENGTH) {
            Log.println(priority, tag, msg)
            return
        }
        var i = 0
        val length = msg.length
        // 循环分割消息并逐块记录日志
        while (i < length) {
            val end = (i + MAX_LOG_LENGTH).coerceAtMost(length)
            Log.println(priority, tag, msg.substring(i, end))
            i = end
        }
    }

}
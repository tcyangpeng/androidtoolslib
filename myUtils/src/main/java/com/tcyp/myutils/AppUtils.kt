package com.tcyp.myutils

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

/**
 * <app 工具类>
 * 提供APP常见的信息获取
 *
 * @author yangpeng
 * @version [版本号, 2025-12-09]
 * @since [V1]
 */
class AppUtils private constructor(context: Context){
    companion object {
        @Volatile
        private var instance: AppUtils? = null

        fun getInstance(context: Context): AppUtils {
            return instance ?: synchronized(this) {
                instance ?: AppUtils(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * 包信息实例
     */
    private var info: PackageInfo? = null

    /**
     * 包管理器实例
     */
    private var pm: PackageManager? = null

    init {
        pm = context.packageManager
        try {
            info = pm!!.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * 获取应用版本CODE
     *
     * @return
     */
    val localVersionCode: Long
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info?.longVersionCode ?: 0
        } else {
            @Suppress("DEPRECATION")
            info?.versionCode?.toLong() ?: 0
        }

    /**
     * 获取应用版本名称
     *
     * @return
     */
    val localVersionName: String
        get() = info?.versionName ?: "defaultVName"

    /**
     * 获取应用名称
     *
     * @return
     */
    val appName: String
        get() = info?.applicationInfo?.let { pm?.getApplicationLabel(it) as? String }
            ?: "defaultAppName"

    /**
     * 获取包名
     *
     * @return
     */
    val packageName: String
        get() = info?.packageName ?: "defaultPackageName"
}
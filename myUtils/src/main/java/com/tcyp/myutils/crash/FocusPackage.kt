package com.tcyp.myutils.crash

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import java.io.Serializable

/**
 * @version :
 * @描述 ：
 * @user ：yanguozhu
 * @date 创建时间 ： 2016/3/22
 */
class FocusPackage : Serializable {
    /**
     * 包信息实例
     */
    private var info: PackageInfo? = null

    /**
     * 包管理器实例
     */
    private var pm: PackageManager? = null

    fun init(context: Context) {
        pm = context.getPackageManager()
        try {
            info = pm!!.getPackageInfo(context.getPackageName(), 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    val localVersionCode: Int
        /**
         * 获取应用版本CODE
         *
         * @return
         */
        get() = if (info != null) info!!.versionCode else 0

    val localVersionName: String?
        /**
         * 获取应用版本名称
         *
         * @return
         */
        get() = if (info != null) info!!.versionName else "defaultVName"

    val appName: String
        /**
         * 获取应用名称
         *
         * @return
         */
        get() = if (info != null) info!!.applicationInfo!!.loadLabel(pm!!) as String else "defaultAppName"

    val packageName: String
        /**
         * 获取包名
         *
         * @return
         */
        get() = if (info != null) info!!.packageName else "defaultPackageName"

    companion object {
        /**
         * Package单例
         */
        @JvmStatic
        var instance: FocusPackage? = null
            /**
             * 获取Package单例
             *
             * @return
             */
            get() {
                if (field == null) {
                    field = FocusPackage()
                }
                return field
            }
            private set
    }
}

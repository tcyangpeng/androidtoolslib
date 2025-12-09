package com.tcyp.myutils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * <权限申请管理工具类>
 * 用于处理Android 6.0及以上版本的运行时权限申请
 *
 * @author focustech
 * @version [版本号, 2024-11-20]
 * @since [V1]
</权限申请管理工具类> */
object PermissionManager {
    /**
     * 检查是否有指定权限
     *
     * @param context    上下文
     * @param permission 权限名称
     * @return true表示有权限，false表示无权限
     */
    fun hasPermission(context: Context?, permission: String?): Boolean {
        if (context == null || permission.isNullOrEmpty()) {
            return false
        }

        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 检查是否有指定的所有权限
     *
     * @param context     上下文
     * @param permissions 权限数组
     * @return true表示全部有权限，false表示有权限未授予
     */
    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context == null || permissions.isEmpty()) {
            return false
        }
        for (permission in permissions) {
            if (!hasPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    /**
     * 获取未授予的权限列表
     *
     * @param context     上下文
     * @param permissions 权限数组
     * @return 未授予的权限列表
     */
    fun getDeniedPermissions(context: Context?, vararg permissions: String?): MutableList<String?> {
        val deniedPermissions: MutableList<String?> = ArrayList<String?>()
        if (context == null || permissions.isEmpty()) {
            return deniedPermissions
        }
        for (permission in permissions) {
            if (!hasPermission(context, permission)) {
                deniedPermissions.add(permission)
            }
        }
        return deniedPermissions
    }

    /**
     * 请求权限
     *
     * @param activity    Activity对象
     * @param permissions 要请求的权限数组
     * @param requestCode 请求码
     */
    fun requestPermissions(activity: Activity?, permissions: Array<String?>?, requestCode: Int) {
        if (activity == null || permissions == null || permissions.isEmpty()) {
            return
        }
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    /**
     * 请求单个权限
     *
     * @param activity    Activity对象
     * @param permission  要请求的权限
     * @param requestCode 请求码
     */
    fun requestPermission(activity: Activity?, permission: String?, requestCode: Int) {
        requestPermissions(activity, arrayOf<String?>(permission), requestCode)
    }

    /**
     * 检查是否应该显示权限申请理由
     *
     * @param activity   Activity对象
     * @param permission 权限名称
     * @return true表示应该显示理由，false表示不需要
     */
    fun shouldShowRequestPermissionRationale(activity: Activity?, permission: String?): Boolean {
        if (activity == null || GeneralUtils.isNullOrEmpty(permission) || permission.isNullOrEmpty()) {
            return false
        }
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /**
     * 检查哪些权限需要显示申请理由
     *
     * @param activity    Activity对象
     * @param permissions 权限数组
     * @return 需要显示理由的权限列表
     */
    fun getPermissionsNeedRationale(
        activity: Activity?,
        vararg permissions: String?
    ): MutableList<String?> {
        val rationalePermissions: MutableList<String?> = ArrayList<String?>()
        if (activity == null || permissions.isEmpty()) {
            return rationalePermissions
        }
        for (permission in permissions) {
            if (shouldShowRequestPermissionRationale(activity, permission)) {
                rationalePermissions.add(permission)
            }
        }
        return rationalePermissions
    }

    /**
     * 处理权限请求结果
     *
     * @param activity     Activity对象
     * @param permissions  请求的权限数组
     * @param grantResults 授权结果数组
     * @param callback     回调接口
     */
    fun handlePermissionResult(
        activity: Activity?, permissions: Array<String?>?,
        grantResults: IntArray?, callback: PermissionCallback?
    ) {
        if (permissions == null || grantResults == null || callback == null) {
            return
        }

        val deniedPermissions: MutableList<String?> = ArrayList<String?>()
        val permanentlyDeniedPermissions: MutableList<String?> = ArrayList<String?>()

        for (i in permissions.indices) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i])
                // 检查是否被永久拒绝
                if (activity != null && !shouldShowRequestPermissionRationale(
                        activity,
                        permissions[i]
                    )
                ) {
                    permanentlyDeniedPermissions.add(permissions[i])
                }
            }
        }

        if (deniedPermissions.isEmpty()) {
            // 所有权限都已授予
            callback.onPermissionGranted()
        } else if (!permanentlyDeniedPermissions.isEmpty()) {
            // 有权限被永久拒绝
            callback.onPermissionPermanentlyDenied(permanentlyDeniedPermissions)
        } else {
            // 权限被拒绝
            callback.onPermissionDenied(deniedPermissions)
        }
    }

    /**
     * 检查并请求权限（如果需要）
     *
     * @param activity    Activity对象
     * @param permissions 权限数组
     * @param requestCode 请求码
     * @return true表示所有权限已授予，false表示需要请求权限
     */
    fun checkAndRequestPermissions(
        activity: Activity?,
        permissions: Array<String?>?,
        requestCode: Int
    ): Boolean {
        if (activity == null || permissions.isNullOrEmpty()) {
            return false
        }

        val deniedPermissions = getDeniedPermissions(activity, *permissions)
        if (deniedPermissions.isEmpty()) {
            // 所有权限都已授予
            return true
        }

        // 请求未授予的权限
        requestPermissions(activity, deniedPermissions.toTypedArray<String?>(), requestCode)
        return false
    }

    /**
     * 获取权限的友好名称（用于向用户解释）
     * 注意：这里只提供常用权限的映射，可根据实际需求扩展
     *
     * @param permission 权限名称
     * @return 友好名称
     */
    fun getPermissionName(permission: String): String {
        if (GeneralUtils.isNullOrEmpty(permission)) {
            return "未知权限"
        }
        // 这里可以根据需要添加更多权限的映射
        if (permission.contains("CAMERA")) {
            return "相机"
        } else if (permission.contains("LOCATION")) {
            return "位置信息"
        } else if (permission.contains("RECORD_AUDIO")) {
            return "麦克风"
        } else if (permission.contains("READ_EXTERNAL_STORAGE") || permission.contains("WRITE_EXTERNAL_STORAGE")) {
            return "存储"
        } else if (permission.contains("READ_PHONE_STATE")) {
            return "电话状态"
        } else if (permission.contains("READ_CONTACTS")) {
            return "通讯录"
        } else if (permission.contains("SEND_SMS") || permission.contains("READ_SMS")) {
            return "短信"
        } else if (permission.contains("BLUETOOTH")) {
            return "蓝牙"
        } else if (permission.contains("CALENDAR")) {
            return "日历"
        }
        return permission
    }

    /**
     * 权限请求回调接口
     */
    interface PermissionCallback {
        /**
         * 权限授予
         */
        fun onPermissionGranted()

        /**
         * 权限拒绝
         *
         * @param deniedPermissions 被拒绝的权限列表
         */
        fun onPermissionDenied(deniedPermissions: MutableList<String?>?)

        /**
         * 权限被永久拒绝（用户选择了"不再询问"）
         *
         * @param permanentlyDeniedPermissions 被永久拒绝的权限列表
         */
        fun onPermissionPermanentlyDenied(permanentlyDeniedPermissions: MutableList<String?>?)
    }
}

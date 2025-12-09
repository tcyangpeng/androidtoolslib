package com.tcyp.myutils


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import okhttp3.Response

/**
 * <判断当前网络状态>
 *
 * @author yanguozhu
 * @version [版本号, 2016/6/24]
 * @since [V1]
</判断当前网络状态> */
object NetworkUtil {
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            // Android 5.0 ~ 8.1 旧版写法
            @Suppress("DEPRECATION")
            val info = cm.activeNetworkInfo ?: return false
            info.isConnected && info.isAvailable
        }
    }


    /**
     * 判断当前网络是否是wifi
     *
     * @param context
     * @return
     */
    fun isWifiConnected(context: Context): Boolean {
        val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info: NetworkInfo? = cm.activeNetworkInfo
        if (null == info) return false
        return info.isConnected && info.type == ConnectivityManager.TYPE_WIFI
    }

    /**
     * 判断手机数据是否连接
     *
     * @return
     */

    fun isMobileConnected(context: Context): Boolean {
        val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info: NetworkInfo? = cm.getActiveNetworkInfo()
        if (null == info) return false
        if (info.isConnected()) return info.getType() == ConnectivityManager.TYPE_MOBILE
        return false
    }


}

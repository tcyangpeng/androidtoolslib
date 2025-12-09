package com.tcyp.myutils.device

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import java.io.Serializable
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.Locale
import java.util.UUID

/**
 * <SDK Service 能力类>
 *
 * @author yanguozhu
 * @version [版本号, 2016-5-26]
 * @since [V1]
</SDK> */
class Device : Serializable {
    /**
     * 手机厂商
     */
    enum class MOBILE_BRAND {
        samsung,
        huawei,
        xiaomi,
        meizu,
        oppo,
        vivo,
        htc,
        other;

        companion object {
            fun getBrand(name: String): MOBILE_BRAND {
                for (item in MOBILE_BRAND.entries) {
                    if (name.lowercase(Locale.getDefault()) == item.name) {
                        return item
                    }
                }
                return MOBILE_BRAND.other
            }
        }
    }

    var myUUID: String? = null

    /**
     * 设备厂商，带型号
     */
    private var deviceModel: String? = null

    /**
     * 设备OS版本号
     */
    var deviceOsVersion: String? = null

    /**
     * User-Agent
     */
    private var userAgent: String? = null

    var brand: MOBILE_BRAND? = null

    /**
     * 应用版本信息
     */
    var appVersion: String? = null

    /**
     * 渠道
     */
    var channel: String? = null

    /**
     * 设备除了厂商之外的 具体信息  比如：hm2(红米2)
     */
    var deviceModelDetail: String? = null

    /**
     * 初始化获取设备信息
     *
     *
     * 注意：<uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
     *
     * @param mContext
     */
    fun initDevice(mContext: Context, appVersion: String?, channel: String?) {
        this.appVersion = appVersion
        val mTm = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val tmDevice: String?
        val tmSerial: String?
        var tmPhone: String?
        val androidId: String?

        tmDevice = "" + mTm.getDeviceId()

        tmSerial = "" + mTm.getSimSerialNumber()

        androidId = "" + Settings.Secure.getString(
            mContext.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )

        val deviceUuid = UUID(
            androidId.hashCode().toLong(),
            (tmDevice.hashCode().toLong() shl 32) or tmSerial.hashCode().toLong()
        )

        myUUID = deviceUuid.toString()
        // 设备型号
//        deviceModel = android.os.Build.MODEL;
        //为了兼容魅族系统推送，将model，改为厂商brand
        deviceModel = Build.BRAND
        deviceModelDetail = Build.DEVICE
        brand = MOBILE_BRAND.Companion.getBrand(deviceModel!!)
        // 设备OS版本号
        deviceOsVersion = Build.VERSION.RELEASE
        //UA
        try {
            val temp = "ANDROID_MOBILE_PARENT_" + deviceModel + "_" + appVersion
            userAgent = URLEncoder.encode(temp, "utf-8")
        } catch (e: UnsupportedEncodingException) {
            userAgent = "ANDROID_MOBILE_PARENT_" + "UNKNOW" + "_" + appVersion
        }
        this.channel = channel
    }

    fun getDeviceModel(): String {
        return deviceModel!!
    }

    fun getUserAgent(): String {
        return (if (userAgent == null) "ANDROID_MOBILE" else userAgent)!!
    }

    fun setDeviceModel(deviceModel: String) {
        this.deviceModel = deviceModel
    }

    fun setUserAgent(userAgent: String?) {
        this.userAgent = userAgent
    }

    companion object {
        /**
         * Device单例
         */
        var instance: Device? = null
            /**
             * 获取Device单例
             *
             * @return
             */
            get() {
                if (field == null) {
                    field = Device()
                }
                return field
            }
            private set
    }
}

package com.tcyp.myutils

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import java.net.URLEncoder
import java.util.Locale
import java.util.UUID

/**
 * 2025 年最新版设备信息管理类
 * 完全合规（不读取 IMEI、序列号等永久标识符）
 * 支持 Advertising ID（可重置，符合 Google Play 政策）
 */
class DeviceUtils private constructor(private val appContext: Context){

    companion object {
        @Volatile private var INSTANCE: DeviceUtils? = null

        private val BRAND_MAPPING = mapOf(
            "samsung" to MobileBrand.SAMSUNG,
            "huawei" to MobileBrand.HUAWEI,
            "honor" to MobileBrand.HONOR,
            "xiaomi" to MobileBrand.XIAOMI,
            "redmi" to MobileBrand.XIAOMI,
            "oppo" to MobileBrand.OPPO,
            "realme" to MobileBrand.REALME,
            "vivo" to MobileBrand.VIVO,
            "iqoo" to MobileBrand.IQOO,
            "meizu" to MobileBrand.MEIZU,
            "google" to MobileBrand.GOOGLE,
            "oneplus" to MobileBrand.ONEPLUS
        )

        fun getInstance(context: Context): DeviceUtils {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DeviceUtils(context.applicationContext).also { INSTANCE = it }
            }
        }
    }


    enum class MobileBrand {
        SAMSUNG, HUAWEI, HONOR, XIAOMI, OPPO, VIVO, IQOO, MEIZU, GOOGLE, ONEPLUS, REALME, OTHER
    }

    val deviceId: String by lazy { generateDeviceId() }
    val model: String = "${Build.MANUFACTURER} ${Build.MODEL}".trim()
    val osVersion: String = Build.VERSION.RELEASE
    val brand: MobileBrand = detectBrand(Build.MANUFACTURER)
    private val userAgent: String

    init {
        this.userAgent = buildUserAgent(getAppVersion())
    }

    /**
     * 获取推荐的设备唯一标识（可重置，合规）
     * 优先级：Advertising ID > AndroidId + RandomUUID 持久化
     */
    private fun generateDeviceId(): String {
        return try {
            val info = AdvertisingIdClient.getAdvertisingIdInfo(appContext)
            info.id?.takeIf { !info.isLimitAdTrackingEnabled } ?: fallbackId()
        } catch (e: Exception) {
            fallbackId()
        }
    }

    private fun fallbackId(): String {
        val androidId = Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)
        return UUID.nameUUIDFromBytes("$androidId:${appContext.packageName}".toByteArray()).toString()
    }

    private fun getAppVersion(): String {
        return try {
            val pInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            pInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun buildUserAgent(version: String): String =
        "ANDROID_MOBILE_${Build.MANUFACTURER.uppercase()}_$version".let {
            try { URLEncoder.encode(it, "UTF-8") } catch (e: Exception) { it }
        }

    fun getUserAgent(): String = userAgent
    fun getSummary(): String = "Device: $model | OS: Android $osVersion | ID: $deviceId"

    /** 检测主流品牌 */
    private fun detectBrand(manufacturer: String): MobileBrand {
//        return when (manufacturer.lowercase(Locale.ROOT)) {
//            "samsung" -> MobileBrand.SAMSUNG
//            "huawei", "honor" -> MobileBrand.HUAWEI
//            "xiaomi", "redmi", "poco" -> MobileBrand.XIAOMI
//            "oppo", "realme" -> MobileBrand.OPPO
//            "vivo" -> MobileBrand.VIVO
//            "meizu" -> MobileBrand.MEIZU
//            "google" -> MobileBrand.GOOGLE
//            "oneplus" -> MobileBrand.ONEPLUS
//            else -> MobileBrand.OTHER
//        }
        return BRAND_MAPPING[manufacturer.lowercase(Locale.ROOT)] ?: MobileBrand.OTHER
    }


    /** 一行代码获取常用设备信息（上报用） */
    fun getDeviceSummary(appVersion: String): String = buildString {
        append("DeviceID: $deviceId, ")
        append("Model: $model, ")
        append("Brand: $brand, ")
        append("OS: Android ${Build.VERSION.RELEASE}, ")
        append("App: v$appVersion")
    }
}
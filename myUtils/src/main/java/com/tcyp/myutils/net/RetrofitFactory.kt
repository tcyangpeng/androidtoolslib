package com.tcyp.myutils.net

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.Timeout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit工厂类，用于创建和配置Retrofit实例
 */
object RetrofitFactory {
    private var BASE_URL = ""
    private var timeout = 10L

    /**
     * 初始化基础URL
     *
     * @param url 基础URL地址
     */
    fun init(url: String, timeout: Long = 10L) {
        BASE_URL = url
    }

    /**
     * 创建并配置OkHttpClient实例
     * 该实例使用懒加载方式初始化，包含超时设置和日志拦截器
     *
     * @return 配置好的OkHttpClient实例
     */
    private val okHttpClient by lazy {
        // 创建HTTP日志拦截器并设置日志级别为BODY
        val logInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 构建OkHttpClient，配置连接、读取和写入超时时间，并添加日志拦截器
        OkHttpClient.Builder()
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .build()
    }



    /**
     * 创建并配置Retrofit实例
     * 该实例使用懒加载模式，仅在首次访问时创建
     * 配置了基础URL、HTTP客户端和Gson转换器
     */
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    /**
     * 创建指定类型的网络服务接口实例
     *
     * @param service 网络服务接口的Class对象
     * @return T 网络服务接口的实现实例
     */
    fun <T> create(service: Class<T>): T = retrofit.create(service)
}

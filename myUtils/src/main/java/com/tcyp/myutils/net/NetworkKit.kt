//package com.tcyp.myutils.net
//
//import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.Serializable
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import okio.IOException
//import retrofit2.Call
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.scalars.ScalarsConverterFactory
//import retrofit2.http.Body
//import retrofit2.http.DELETE
//import retrofit2.http.GET
//import retrofit2.http.POST
//import retrofit2.http.PUT
//import retrofit2.http.QueryMap
//import retrofit2.http.Url
//import java.util.concurrent.TimeUnit
//import kotlin.Result
//import kotlinx.coroutines.suspendCancellableCoroutine
//import okhttp3.Call as OkHttpCall
//import okhttp3.Callback as OkHttpCallback
//import retrofit2.HttpException
//import kotlin.jvm.JvmOverloads
//import kotlin.jvm.JvmSuppressWildcards
//
//object NetworkKit {
//    private const val TAG = "NetworkKit"
//    private const val TIMEOUT_SECONDS = 30
//    private const val RETRY_COUNT = 3
//    private const val RETRY_DELAY_MILLIS = 500L
//
//    private const val BASE_URL = "https://api.example.com/" // 改成你的域名
//
//    private val okHttpClient by lazy {
//        OkHttpClient.Builder()
//            .connectTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
//            .readTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
//            .writeTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
//            .addInterceptor(HttpLoggingInterceptor().apply {
//                level = HttpLoggingInterceptor.Level.BODY
//            })
//            .addInterceptor { chain ->
//                var request = chain.request().newBuilder()
//                    .addHeader("Platform", "Android")
//                    .addHeader("Content-Type", "application/json")
//                    .build()
//                chain.proceed(request)
//            }
//            .build()
//    }
//
//
//    private val json = Json {
//        ignoreUnknownKeys = true
//        coerceInputValues = true
//    }
//
//    private val retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
//            .build()
//    }
//
//    // 动态创建 API 接口
//    private inline fun <reified T> create(): T = retrofit.create(T::class.java)
//
//    // ====================== 通用请求方法 ======================
//
//    // 请求方法
//    suspend inline fun <reified T> get(
//        url: String,
//        params: Map<String, Any>? = null
//    ): Result<T> = safeRequest {
//        val call = create<ApiService>().get<T>(url, params ?: emptyMap())
//        call.awaitResponse().body()!!
//    }
//
//    suspend inline fun <reified T> post(
//        url: String,
//        body: Any? = null
//    ): Result<T> = safeRequest {
//        create<ApiService>()
//            .post<T>(url, body)
//            .also { check(it.isSuccessful) }
//            .body()!!
//    }
//
//    // 修改 put 函数
//    suspend inline fun <reified T> put(
//        url: String,
//        body: Any
//    ): Result<T> = safeRequest {
//        create<ApiService>().put<T>(url, body).awaitResponse().body()!!
//    }
//
//
//
//    // ====================== 核心安全请求封装 ======================
//    suspend inline fun <reified T> safeRequest(
//        block: () -> T
//    ): Result<T> = try {
//        Result.success(block())
//    } catch (e: HttpException) {
//        Result.failure(NetworkException.Http(e.code(), e.message()))
//    } catch (e: IOException) {
//        Result.failure(NetworkException.NoNetwork("无网络连接"))
//    } catch (e: Exception) {
//        Result.failure(NetworkException.ParseError("数据解析失败: ${e.message}", e))
//    }
//
//    // 临时 API 接口（不需要每个接口都写一个）
//    @JvmSuppressWildcards
//    interface ApiService {
//        @GET
//        suspend fun <T> get(
//            @Url url: String,
//            @QueryMap params: Map<String, Any>
//        ): Response<T>
//
//        @POST
//        suspend fun <T> post(
//            @Url url: String,
//            @Body body: Any? = null
//        ): Response<T>
//
//        @PUT
//        suspend fun <T> put(
//            @Url url: String,
//            @Body body: Any
//        ): Response<T>
//    }
//
//    // 扩展：让 Call 变成 suspend 函数
//// 扩展：让 Call 变成 suspend 函数
//    suspend fun <T> Call<T>.awaitResponse(): Response<T> = suspendCancellableCoroutine { cont ->
//        cont.invokeOnCancellation {
//            cancel()
//        }
//
//        enqueue(object : Callback<T> {
//            private val isResumeCalled = java.util.concurrent.atomic.AtomicBoolean(false)
//
//            override fun onResponse(call: Call<T>, response: Response<T>) {
//                if (isResumeCalled.compareAndSet(false, true)) {
//                    cont.resume(response)
//                }
//            }
//
//            override fun onFailure(call: Call<T>, t: Throwable) {
//                if (isResumeCalled.compareAndSet(false, true)) {
//                    cont.resumeWithException(t)
//                }
//            }
//        })
//    }
//}
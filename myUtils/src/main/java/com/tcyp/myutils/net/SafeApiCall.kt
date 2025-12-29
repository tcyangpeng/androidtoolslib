package com.tcyp.myutils.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 安全的API调用包装函数，用于处理网络请求并返回Result包装的结果
 *
 * @param T 泛型类型，表示API响应数据的类型
 * @param block 挂起函数类型的回调，执行具体的API请求并返回BaseResponse<T>
 * @return Result<T?> 包含成功数据或异常信息的结果对象，成功时返回响应数据，失败时返回异常
 */
suspend fun <T> safeApiCall(
    block: suspend () -> BaseResponse<T>
): Result<T?> {
    return try {
        // 在IO调度器上下文中执行API请求
        val response = withContext(Dispatchers.IO) {
            block()
        }

        if (response.isSuccess()) {
            Result.success(response.data)
        } else {
            Result.failure(
                RuntimeException("code=${response.code}, msg=${response.message}")
            )
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

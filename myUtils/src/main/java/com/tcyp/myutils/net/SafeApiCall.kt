package com.tcyp.myutils.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 协程安全请求封装
 */
suspend fun <T> safeApiCall(
    block: suspend () -> BaseResponse<T>
): Result<T?> {
    return try {
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
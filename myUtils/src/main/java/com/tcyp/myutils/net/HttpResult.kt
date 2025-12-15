package com.tcyp.myutils.net

sealed class HttpResult<out T> {
    data class Success<T>(val data: T) : HttpResult<T>()

    data class Error(val exception: ApiException) : HttpResult<Nothing>()

    data class Loading(val isLoading: Boolean) : HttpResult<Nothing>()

    inline fun onSuccess(block: (T?) -> Unit): HttpResult<T> {
        if (this is Success) block(data)
        return this
    }

    inline fun onError(block: (ApiException) -> Unit): HttpResult<T> {
        if (this is Error) block(exception)
        return this
    }

    inline fun onLoading(block: (Boolean) -> Unit): HttpResult<T> {
        if (this is Loading) block(isLoading)
        return this
    }
}
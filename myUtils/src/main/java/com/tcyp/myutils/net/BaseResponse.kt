package com.tcyp.myutils.net

import com.google.gson.annotations.SerializedName;

/**
 * <基础响应类>
 *
 * @author yp
 * @version [版本号, 2025/11/27]
 * @see [相关类/方法]
 *
 * @since [V1]
</基础响应类> */
data class BaseResponse<T>(
    val code: Int,
    @SerializedName("message",  alternate = ["errmsg", "msg"])
    val message: String,
    val data: T?,
) {
    fun isSuccess(): Boolean {
        return code == 200 || code == 0
    }
}

package com.tcyp.myutils.net

import com.tcyp.myutils.net.bean.TestUserBean
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * 测试代码接口，需要用户自己进行实现
 */
interface TestApiService {
    @GET("users")
    suspend fun getUsers(): BaseResponse<List<TestUserBean>>
}
package com.tcyp.myutils.net

import com.tcyp.myutils.net.bean.TestUserBean
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("users")
    suspend fun getUsers(): BaseResponse<List<TestUserBean>>
}
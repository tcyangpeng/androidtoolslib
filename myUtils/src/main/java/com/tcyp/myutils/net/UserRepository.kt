package com.tcyp.myutils.net

import com.tcyp.myutils.net.bean.TestUserBean

class UserRepository {
    private val api = RetrofitFactory.create(TestApiService::class.java)

    suspend fun getUserInfo(): Result<List<TestUserBean>?> {
        return safeApiCall {
            api.getUsers()
        }
    }
}
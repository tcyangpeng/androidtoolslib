package com.tcyp.androidtoolslib

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tcyp.myutils.net.TestApiService
import com.tcyp.myutils.net.UserRepository
import com.tcyp.myutils.net.bean.TestUserBean
import com.tcyp.myutils.net.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TestViewModel : ViewModel() {
    private val repository = UserRepository()
    private val _list = MutableStateFlow<List<TestUserBean>>(arrayListOf())
    val list = _list.asStateFlow()

    fun getUsers() {
        viewModelScope.launch {
            repository.getUserInfo().onSuccess {
                if (it != null) {
                    _list.value = it
                } else {
                    _list.value = emptyList()
                }
            }.onFailure {
                println("error: ${it.message}")
            }
        }
    }
}
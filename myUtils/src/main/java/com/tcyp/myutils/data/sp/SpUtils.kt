package com.tcyp.myutils.data.sp

import android.content.Context
import android.content.SharedPreferences
import com.tcyp.myutils.AppHolder
import androidx.core.content.edit

object SpUtils {
    const val SP_NAME = "my_utils_sp"
    val myApp = AppHolder.app

    private val sp: SharedPreferences by lazy(LazyThreadSafetyMode.NONE) {
        myApp.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    private val lock = Any()

    fun saveString(key: String, value: String) {
        synchronized(lock) {
            sp.edit { putString(key, value) }
        }
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sp.getString(key, defaultValue) ?: defaultValue
    }

    fun saveInt(key: String, value: Int) {
        synchronized(lock) {
            sp.edit { putInt(key, value) }
        }
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        val sp = myApp.getSharedPreferences(SP_NAME, 0)
        return sp.getInt(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        synchronized(lock) {
            sp.edit { putBoolean(key, value) }
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        val sp = myApp.getSharedPreferences(SP_NAME, 0)
        return sp.getBoolean(key, defaultValue)
    }

    // 可选：提供清除某个 key 或全部
    fun remove(key: String) {
        synchronized(lock) {
            sp.edit { remove(key) }
        }
    }

    fun clearAll() {
        synchronized(lock) {
            sp.edit { clear() }
        }
    }
}
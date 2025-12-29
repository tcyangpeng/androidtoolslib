package com.tcyp.androidtoolslib

import android.app.Application
import com.tcyp.myutils.InitProvider
import com.tcyp.myutils.net.RetrofitFactory

class MyApp: Application () {
    override fun onCreate() {
        super.onCreate()
        RetrofitFactory.init("https://jsonplaceholder.typicode.com/")

        InitProvider()
    }
}
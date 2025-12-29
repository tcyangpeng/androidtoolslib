package com.tcyp.myutils

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.tcyp.myutils.image.ColiUtils

class InitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        AppHolder.app = context!!.applicationContext as Application
        ColiUtils.init(AppHolder.app)
        return true
    }
    override fun delete(
        p0: Uri,
        p1: String?,
        p2: Array<out String?>?
    ): Int  = 0

    override fun getType(p0: Uri): String?  = null

    override fun insert(p0: Uri, p1: ContentValues?): Uri?  = null

    override fun query(
        p0: Uri,
        p1: Array<out String?>?,
        p2: String?,
        p3: Array<out String?>?,
        p4: String?
    ): Cursor?  = null

    override fun update(
        p0: Uri,
        p1: ContentValues?,
        p2: String?,
        p3: Array<out String?>?
    ): Int  = 0
}

object AppHolder {
    lateinit var app: Application
}
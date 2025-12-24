package com.tcyp.myutils

import android.graphics.Color
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

object ToastUtils {
    private var currentToast : Toast? = null
    fun showToastShort(msg: String) {
        Toast.makeText(AppHolder.app, msg, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(msg: String) {
        Toast.makeText(AppHolder.app, msg, Toast.LENGTH_LONG).show()
    }

    fun showToastShort(@StringRes resId: Int) {
        val context = AppHolder.app
        Toast.makeText(context, context.getString( resId ), Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(@StringRes resId: Int) {
        val context = AppHolder.app
        Toast.makeText(context, context.getString( resId ), Toast.LENGTH_LONG).show()
    }

    fun showToast(msg: String, duration: Int) {
        val context = AppHolder.app
        Toast.makeText(context, msg, duration).show()
    }

    fun showImgToast(@StringRes resId: Int, @DrawableRes imgResId: Int, duration: Int = Toast.LENGTH_LONG, canCancel: Boolean = false) {
        val context = AppHolder.app
//        val inflate = LayoutInflater.from(context)

        //创建自定义布局
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(50, 30, 50, 50)
        }

        //添加图片
        val imageView = ImageView(context).apply {
            setImageResource(imgResId)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        //添加文字
        val text = TextView(context).apply {
            text = context.getString(resId)
            setTextColor(Color.BLACK)
            textSize = 16f
            setPadding(20, 0, 0, 0) //设置文字与图片的间距
        }
        layout.apply {
            addView(imageView)
            addView(text)
        }

        if (canCancel) {
            currentToast ?.cancel()
            currentToast = null
        }

        currentToast = Toast(context)
        currentToast?.duration = duration
        currentToast?.view = layout
        currentToast?.show()
    }

    fun showToast(msg: String, duration: Int, canCancel: Boolean) {
        if (!canCancel) {
            showToast(msg, duration)
            return
        }
        val context = AppHolder.app

        if (currentToast != null) {
            currentToast?.cancel()
            currentToast = null
        }
        currentToast = Toast.makeText(context, msg, duration)
        currentToast?.show()
    }

    private fun cancelToast() {
        currentToast?.cancel()
    }
}
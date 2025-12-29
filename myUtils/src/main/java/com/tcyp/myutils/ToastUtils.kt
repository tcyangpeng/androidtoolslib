package com.tcyp.myutils

import android.graphics.Color
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Toast工具类，提供多种方式显示Toast消息
 */
object ToastUtils {
    private var currentToast : Toast? = null

    /**
     * 显示短时Toast消息
     * @param msg 要显示的消息内容
     */
    fun showToastShort(msg: String) {
        Toast.makeText(AppHolder.app, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * 显示长时Toast消息
     * @param msg 要显示的消息内容
     */
    fun showToastLong(msg: String) {
        Toast.makeText(AppHolder.app, msg, Toast.LENGTH_LONG).show()
    }

    /**
     * 显示短时Toast消息（通过资源ID）
     * @param resId 字符串资源ID
     */
    fun showToastShort(@StringRes resId: Int) {
        val context = AppHolder.app
        Toast.makeText(context, context.getString( resId ), Toast.LENGTH_SHORT).show()
    }

    /**
     * 显示长时Toast消息（通过资源ID）
     * @param resId 字符串资源ID
     */
    fun showToastLong(@StringRes resId: Int) {
        val context = AppHolder.app
        Toast.makeText(context, context.getString( resId ), Toast.LENGTH_LONG).show()
    }

    /**
     * 显示指定时长的Toast消息
     * @param msg 要显示的消息内容
     * @param duration 显示时长
     */
    fun showToast(msg: String, duration: Int) {
        val context = AppHolder.app
        Toast.makeText(context, msg, duration).show()
    }

    /**
     * 显示带图片的自定义Toast
     * @param resId 文字内容的字符串资源ID
     * @param imgResId 图片资源ID
     * @param duration 显示时长，默认为Toast.LENGTH_LONG
     * @param canCancel 是否可以取消之前的Toast，默认为false
     */
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

    /**
     * 显示指定时长的Toast消息，支持取消之前的Toast
     * @param msg 要显示的消息内容
     * @param duration 显示时长
     * @param canCancel 是否可以取消之前的Toast
     */
    fun showToast(msg: String, duration: Int, canCancel: Boolean = false) {
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

    /**
     * 取消当前显示的Toast
     */
    private fun cancelToast() {
        currentToast?.cancel()
    }
}

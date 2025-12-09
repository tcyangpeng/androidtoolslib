package com.tcyp.myutils

import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.ListView
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

/**
 * <通用工具类>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 *
 * @since [V1]
</通用工具类> */
object GeneralUtils {
    /**
     * 判断对象是否为null , 为null返回true,否则返回false
     *
     * @param obj 被判断的对象
     * @return boolean
     */
    fun isNull(obj: Any?): Boolean {
        return null == obj
    }

    /**
     * 判断String为空则返回""
     *
     * @param obj
     * @return
     */
    fun isNullToEmpty(obj: String): String {
        if (GeneralUtils.isNull(obj)) return ""
        return obj
    }


    /**
     * 判断对象是否为null , 为null返回false,否则返回true
     *
     * @param obj 被判断的对象
     * @return boolean
     */
    fun isNotNull(obj: Any?): Boolean {
        return !GeneralUtils.isNull(obj)
    }

    /**
     * 判断集合对象是否为null  添加顺序依次遍历
     *
     * @param objs
     * @return
     */
    fun isNull(vararg objs: Any?): Boolean {
        for (obj in objs) {
            if (GeneralUtils.isNull(obj)) return true
        }
        return false
    }

    /**
     * 判断集合对象是否为null 按添加顺序依次遍历
     *
     * @param objs
     * @return
     */
    fun isNotNull(vararg objs: Any?): Boolean {
        for (obj in objs) {
            if (GeneralUtils.isNull(obj)) return false
        }
        return true
    }

    /**
     * 判断字符串是否为null或者0长度，字符串在判断长度时，先去除前后的空格,空或者0长度返回true,否则返回false
     *
     * @param str 被判断的字符串
     * @return boolean
     */
    @JvmStatic
    fun isNullOrEmpty(str: String?): Boolean {
        return (null == str || "" == str.trim { it <= ' ' })
    }

    /**
     * 判断字符串是否为null或者0长度，字符串在判断长度时，先去除前后的空格,空或者0长度返回false,否则返回true
     *
     * @param str 被判断的字符串
     * @return boolean
     */
    fun isNotNullOrEmpty(str: String?): Boolean {
        return !isNullOrEmpty(str)
    }

    /**
     * 判断集合对象是否为null或者0大小 , 为空或0大小返回true ,否则返回false
     *
     * @param c collection 集合接口
     * @return boolean 布尔值
     * @see [类、类.方法、类.成员]
     */
    fun isNullOrZeroSize(c: MutableCollection<out Any?>?): Boolean {
        return GeneralUtils.isNull(c) || c!!.isEmpty()
    }

    /**
     * 判断集合对象是否为null或者0大小 , 为空或0大小返回false, 否则返回true
     *
     * @param c collection 集合接口
     * @return boolean 布尔值
     * @see [类、类.方法、类.成员]
     */
    fun isNotNullOrZeroSize(c: MutableCollection<out Any?>?): Boolean {
        return !isNullOrZeroSize(c)
    }

    /**
     * 判断数字类型是否为null或者0，如果是返回true，否则返回false
     *
     * @param number 被判断的数字
     * @return boolean
     */
    fun isNullOrZero(number: Number?): Boolean {
        if (GeneralUtils.isNotNull(number)) {
            return if (number!!.toInt() != 0) false else true
        }
        return true
    }

    /**
     * 判断数字类型是否不为null或者0，如果是返回true，否则返回false
     *
     * @param number 被判断的数字
     * @return boolean
     */
    fun isNotNullOrZero(number: Number?): Boolean {
        return !isNullOrZero(number)
    }

    val rightNowDateString: String
        /**
         * <获取当前日期 格式 yyyyMMddHHmmss> <功能详细描述>
         *
         * @return String
         * @see [类、类.方法、类.成员]
        </功能详细描述></获取当前日期> */
        get() {
            val calendar =
                Calendar.getInstance(Locale.CHINA)
            val date = calendar.getTime()
            val dateFormat: DateFormat = SimpleDateFormat("yyyyMMddHHmmss")
            return dateFormat.format(date)
        }

    val rightNowDateTime: Date?
        /**
         * <获取当前时间 格式yyyyMMddHHmmss> <功能详细描述>
         *
         * @return String
         * @see [类、类.方法、类.成员]
        </功能详细描述></获取当前时间> */
        get() {
            val calendar =
                Calendar.getInstance(Locale.CHINA)
            val date = calendar.getTime()
            val dateFormat: DateFormat = SimpleDateFormat("yyyyMMddHHmmss")
            try {
                return dateFormat.parse(dateFormat.format(date))
            } catch (e: ParseException) {
                e.printStackTrace()
                return null
            }
        }

    /**
     * <邮箱判断>
     * <功能详细描述>
     *
     * @param email
     * @return
     * @see [类、类.方法、类.成员]
    </功能详细描述></邮箱判断> */
    fun isEmail(email: String): Boolean {
        val str =
            "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"
        val p = Pattern.compile(str)
        val m = p.matcher(email)
        return m.matches()
    }

    /**
     * <手机号码判断>
     *
     * @param tel
     * @return
     * @see [类、类.方法、类.成员]
    </手机号码判断> */
    fun isTel(tel: String): Boolean {
        val str = "^[0-9]{11}$"
        val p = Pattern.compile(str)
        val m = p.matcher(tel)
        return m.matches()
    }

    /**
     * <邮编判断>
     * <功能详细描述>
     *
     * @param post
     * @return
     * @see [类、类.方法、类.成员]
    </功能详细描述></邮编判断> */
    fun isPost(post: String): Boolean {
        val patrn = "^[1-9][0-9]{5}$"
        val p = Pattern.compile(patrn)
        val m = p.matcher(post)
        return m.matches()
    }


    /**
     * http://stackoverflow.com/questions/3495890/how-can-i-put-a-listview-into-a-scrollview-without-it-collapsing/3495908#3495908
     *
     * @param listView
     */
    fun setListViewHeightBasedOnChildrenExtend(listView: ListView) {
        val listAdapter = listView.getAdapter()
        if (listAdapter == null) {
            return
        }
        val desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST)
        var totalHeight = 0
        var view: View? = null
        for (i in 0..<listAdapter.getCount()) {
            view = listAdapter.getView(i, view, listView)
            if (i == 0) {
                view.setLayoutParams(
                    ViewGroup.LayoutParams(
                        desiredWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }
            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED)
            totalHeight += view.getMeasuredHeight()
        }
        val params = listView.getLayoutParams()
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))
        listView.setLayoutParams(params)
        listView.requestLayout()
    }

    // 去除textview的排版问题
    fun ToDBC(input: String): String {
        val c = input.toCharArray()
        for (i in c.indices) {
            if (c[i].code == 12288) {
                c[i] = 32.toChar()
                continue
            }
            if (c[i].code > 65280 && c[i].code < 65375) c[i] = (c[i].code - 65248).toChar()
        }
        return String(c)
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    fun getCurrentTime(format: String?): String {
        val currentTime = System.currentTimeMillis()
        val date = Date(currentTime)
        val sdf = SimpleDateFormat(format)
        return sdf.format(date)
    }
}

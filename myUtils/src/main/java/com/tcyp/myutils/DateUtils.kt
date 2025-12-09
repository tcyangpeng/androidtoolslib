package com.tcyp.myutils

import com.tcyp.myutils.GeneralUtils.isNullOrEmpty
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * <日期转换工具类>
 * 提供日期格式化、转换、计算等常用功能
 *
 * @author focustech
 * @version [版本号, 2024-11-20]
 * @since [V1]
</日期转换工具类> */
class DateUtils private constructor() {
    init {
        throw UnsupportedOperationException("DateUtils cannot be instantiated")
    }

    companion object {
        // 常用日期格式
        const val FORMAT_YYYY_MM_DD: String = "yyyy-MM-dd"
        const val FORMAT_YYYY_MM_DD_HH_MM_SS: String = "yyyy-MM-dd HH:mm:ss"
        const val FORMAT_YYYYMMDD: String = "yyyyMMdd"
        const val FORMAT_YYYYMMDDHHMMSS: String = "yyyyMMddHHmmss"
        const val FORMAT_HH_MM_SS: String = "HH:mm:ss"
        const val FORMAT_HH_MM: String = "HH:mm"
        const val FORMAT_YYYY_MM_DD_CN: String = "yyyy年MM月dd日"
        const val FORMAT_YYYY_MM_DD_HH_MM_SS_CN: String = "yyyy年MM月dd日 HH:mm:ss"

        /**
         * 将日期转换为指定格式的字符串
         *
         * @param date   日期对象
         * @param format 格式化字符串
         * @return 格式化后的日期字符串
         */
        /**
         * 将日期转换为默认格式的字符串 (yyyy-MM-dd HH:mm:ss)
         *
         * @param date 日期对象
         * @return 格式化后的日期字符串
         */
        @JvmOverloads
        fun formatDate(date: Date?, format: String? = FORMAT_YYYY_MM_DD_HH_MM_SS): String {
            if (date == null || isNullOrEmpty(format)) {
                return ""
            }
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            return sdf.format(date)
        }

        /**
         * 将时间戳转换为指定格式的字符串
         *
         * @param timestamp 时间戳（毫秒）
         * @param format    格式化字符串
         * @return 格式化后的日期字符串
         */
        /**
         * 将时间戳转换为默认格式的字符串 (yyyy-MM-dd HH:mm:ss)
         *
         * @param timestamp 时间戳（毫秒）
         * @return 格式化后的日期字符串
         */
        @JvmOverloads
        fun formatTimestamp(timestamp: Long, format: String? = FORMAT_YYYY_MM_DD_HH_MM_SS): String {
            return formatDate(Date(timestamp), format)
        }

        /**
         * 将字符串解析为日期对象
         *
         * @param dateString 日期字符串
         * @param format     格式化字符串
         * @return 日期对象，解析失败返回null
         */
        /**
         * 将字符串解析为日期对象，使用默认格式 (yyyy-MM-dd HH:mm:ss)
         *
         * @param dateString 日期字符串
         * @return 日期对象，解析失败返回null
         */
        @JvmOverloads
        fun parseDate(dateString: String, format: String? = FORMAT_YYYY_MM_DD_HH_MM_SS): Date? {
            if (isNullOrEmpty(dateString) || isNullOrEmpty(format)) {
                return null
            }
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            try {
                return sdf.parse(dateString)
            } catch (e: ParseException) {
                e.printStackTrace()
                return null
            }
        }

        val currentDate: Date
            /**
             * 获取当前时间
             *
             * @return 当前时间的Date对象
             */
            get() = Date()

        val currentTimestamp: Long
            /**
             * 获取当前时间戳（毫秒）
             *
             * @return 当前时间戳
             */
            get() = System.currentTimeMillis()

        /**
         * 获取当前时间的格式化字符串
         *
         * @param format 格式化字符串
         * @return 格式化后的当前时间字符串
         */
        fun getCurrentDateString(format: String?): String {
            return formatDate(currentDate, format)
        }

        val currentDateString: String
            /**
             * 获取当前时间的格式化字符串，使用默认格式 (yyyy-MM-dd HH:mm:ss)
             *
             * @return 格式化后的当前时间字符串
             */
            get() = getCurrentDateString(FORMAT_YYYY_MM_DD_HH_MM_SS)

        /**
         * 计算两个日期之间相差的天数
         *
         * @param startDate 开始日期
         * @param endDate   结束日期
         * @return 相差的天数
         */
        fun getDaysBetween(startDate: Date?, endDate: Date?): Long {
            if (startDate == null || endDate == null) {
                return 0
            }
            val diff = endDate.getTime() - startDate.getTime()
            return diff / (1000 * 60 * 60 * 24)
        }

        /**
         * 在指定日期上增加或减少天数
         *
         * @param date 原始日期
         * @param days 要增加的天数（负数表示减少）
         * @return 计算后的日期
         */
        fun addDays(date: Date?, days: Int): Date? {
            if (date == null) {
                return null
            }
            val calendar = Calendar.getInstance()
            calendar.setTime(date)
            calendar.add(Calendar.DAY_OF_MONTH, days)
            return calendar.getTime()
        }

        /**
         * 在指定日期上增加或减少月份
         *
         * @param date   原始日期
         * @param months 要增加的月份（负数表示减少）
         * @return 计算后的日期
         */
        fun addMonths(date: Date?, months: Int): Date? {
            if (date == null) {
                return null
            }
            val calendar = Calendar.getInstance()
            calendar.setTime(date)
            calendar.add(Calendar.MONTH, months)
            return calendar.getTime()
        }

        /**
         * 在指定日期上增加或减少年份
         *
         * @param date  原始日期
         * @param years 要增加的年份（负数表示减少）
         * @return 计算后的日期
         */
        fun addYears(date: Date?, years: Int): Date? {
            if (date == null) {
                return null
            }
            val calendar = Calendar.getInstance()
            calendar.setTime(date)
            calendar.add(Calendar.YEAR, years)
            return calendar.getTime()
        }

        /**
         * 获取指定日期的年份
         *
         * @param date 日期
         * @return 年份
         */
        fun getYear(date: Date?): Int {
            if (date == null) {
                return 0
            }
            val calendar = Calendar.getInstance()
            calendar.setTime(date)
            return calendar.get(Calendar.YEAR)
        }

        /**
         * 获取指定日期的月份（1-12）
         *
         * @param date 日期
         * @return 月份
         */
        fun getMonth(date: Date?): Int {
            if (date == null) {
                return 0
            }
            val calendar = Calendar.getInstance()
            calendar.setTime(date)
            return calendar.get(Calendar.MONTH) + 1
        }

        /**
         * 获取指定日期的天（1-31）
         *
         * @param date 日期
         * @return 天
         */
        fun getDay(date: Date?): Int {
            if (date == null) {
                return 0
            }
            val calendar = Calendar.getInstance()
            calendar.setTime(date)
            return calendar.get(Calendar.DAY_OF_MONTH)
        }

        /**
         * 获取指定日期是星期几（1-7，1表示星期日）
         *
         * @param date 日期
         * @return 星期几
         */
        fun getWeekDay(date: Date?): Int {
            if (date == null) {
                return 0
            }
            val calendar = Calendar.getInstance()
            calendar.setTime(date)
            return calendar.get(Calendar.DAY_OF_WEEK)
        }

        /**
         * 判断是否是闰年
         *
         * @param year 年份
         * @return true表示闰年，false表示平年
         */
        fun isLeapYear(year: Int): Boolean {
            return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
        }

        /**
         * 判断指定日期是否是今天
         *
         * @param date 日期
         * @return true表示是今天，false表示不是
         */
        fun isToday(date: Date?): Boolean {
            if (date == null) {
                return false
            }
            val today: String = formatDate(currentDate, FORMAT_YYYY_MM_DD)
            val target: String = formatDate(date, FORMAT_YYYY_MM_DD)
            return today == target
        }

        /**
         * 判断指定日期是否是昨天
         *
         * @param date 日期
         * @return true表示是昨天，false表示不是
         */
        fun isYesterday(date: Date?): Boolean {
            if (date == null) {
                return false
            }
            val yesterday: String = formatDate(
                addDays(
                    currentDate, -1
                ), FORMAT_YYYY_MM_DD
            )
            val target: String = formatDate(date, FORMAT_YYYY_MM_DD)
            return yesterday == target
        }

        /**
         * 获取指定日期所在月份的第一天
         *
         * @param date 日期
         * @return 月份第一天的日期
         */
        fun getFirstDayOfMonth(date: Date?): Date? {
            if (date == null) {
                return null
            }
            val calendar = Calendar.getInstance()
            calendar.setTime(date)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.getTime()
        }

        /**
         * 获取指定日期所在月份的最后一天
         *
         * @param date 日期
         * @return 月份最后一天的日期
         */
        fun getLastDayOfMonth(date: Date?): Date? {
            if (date == null) {
                return null
            }
            val calendar = Calendar.getInstance()
            calendar.setTime(date)
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            return calendar.getTime()
        }

        /**
         * 获取两个日期中较早的日期
         *
         * @param date1 日期1
         * @param date2 日期2
         * @return 较早的日期
         */
        fun min(date1: Date?, date2: Date?): Date? {
            if (date1 == null) {
                return date2
            }
            if (date2 == null) {
                return date1
            }
            return if (date1.before(date2)) date1 else date2
        }

        /**
         * 获取两个日期中较晚的日期
         *
         * @param date1 日期1
         * @param date2 日期2
         * @return 较晚的日期
         */
        fun max(date1: Date?, date2: Date?): Date? {
            if (date1 == null) {
                return date2
            }
            if (date2 == null) {
                return date1
            }
            return if (date1.after(date2)) date1 else date2
        }
    }
}

package com.tcyp.myutils

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Random
import kotlin.math.pow

/**
 * <数学工具类>
 * 提供数学计算、随机数生成等功能
 *
 * @author focustech
 * @version [版本号, 2024-11-20]
 * @since [V1]
</数学工具类> */
class MathUtils private constructor() {
    init {
        throw UnsupportedOperationException("MathUtils cannot be instantiated")
    }

    companion object {
        private val RANDOM = Random()

        /**
         * 生成指定范围内的随机整数（包含min，不包含max）
         *
         * @param min 最小值（包含）
         * @param max 最大值（不包含）
         * @return 随机整数
         */
        fun randomInt(min: Int, max: Int): Int {
            require(min < max) { "max must be greater than min" }
            return RANDOM.nextInt(max - min) + min
        }

        /**
         * 生成0到指定值之间的随机整数（不包含bound）
         *
         * @param bound 上界（不包含）
         * @return 随机整数
         */
        fun randomInt(bound: Int): Int {
            require(bound > 0) { "bound must be positive" }
            return RANDOM.nextInt(bound)
        }

        /**
         * 生成指定范围内的随机长整数（包含min，不包含max）
         *
         * @param min 最小值（包含）
         * @param max 最大值（不包含）
         * @return 随机长整数
         */
        fun randomLong(min: Long, max: Long): Long {
            require(min < max) { "max must be greater than min" }
            return (RANDOM.nextDouble() * (max - min)).toLong() + min
        }

        /**
         * 生成指定范围内的随机浮点数（包含min，不包含max）
         *
         * @param min 最小值（包含）
         * @param max 最大值（不包含）
         * @return 随机浮点数
         */
        fun randomFloat(min: Float, max: Float): Float {
            require(!(min >= max)) { "max must be greater than min" }
            return RANDOM.nextFloat() * (max - min) + min
        }

        /**
         * 生成指定范围内的随机双精度浮点数（包含min，不包含max）
         *
         * @param min 最小值（包含）
         * @param max 最大值（不包含）
         * @return 随机双精度浮点数
         */
        fun randomDouble(min: Double, max: Double): Double {
            require(!(min >= max)) { "max must be greater than min" }
            return RANDOM.nextDouble() * (max - min) + min
        }

        /**
         * 生成随机布尔值
         *
         * @return 随机布尔值
         */
        fun randomBoolean(): Boolean {
            return RANDOM.nextBoolean()
        }

        /**
         * 生成指定长度的随机字符串（包含数字和大小写字母）
         *
         * @param length 字符串长度
         * @return 随机字符串
         */
        fun randomString(length: Int): String {
            if (length <= 0) {
                return ""
            }
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            val sb = StringBuilder(length)
            for (i in 0..<length) {
                sb.append(chars.get(RANDOM.nextInt(chars.length)))
            }
            return sb.toString()
        }

        /**
         * 生成指定长度的随机数字字符串
         *
         * @param length 字符串长度
         * @return 随机数字字符串
         */
        fun randomNumericString(length: Int): String {
            if (length <= 0) {
                return ""
            }
            val sb = StringBuilder(length)
            for (i in 0..<length) {
                sb.append(RANDOM.nextInt(10))
            }
            return sb.toString()
        }

        /**
         * 获取两个数中的最大值
         *
         * @param a 第一个数
         * @param b 第二个数
         * @return 最大值
         */
        fun max(a: Int, b: Int): Int {
            return kotlin.math.max(a, b)
        }

        /**
         * 获取多个数中的最大值
         *
         * @param numbers 数字数组
         * @return 最大值
         */
        fun max(vararg numbers: Int): Int {
            require(!(numbers == null || numbers.size == 0)) { "numbers cannot be null or empty" }
            var max = numbers[0]
            for (i in 1..<numbers.size) {
                if (numbers[i] > max) {
                    max = numbers[i]
                }
            }
            return max
        }

        /**
         * 获取两个数中的最小值
         *
         * @param a 第一个数
         * @param b 第二个数
         * @return 最小值
         */
        fun min(a: Int, b: Int): Int {
            return kotlin.math.min(a, b)
        }

        /**
         * 获取多个数中的最小值
         *
         * @param numbers 数字数组
         * @return 最小值
         */
        fun min(vararg numbers: Int): Int {
            require(!(numbers == null || numbers.size == 0)) { "numbers cannot be null or empty" }
            var min = numbers[0]
            for (i in 1..<numbers.size) {
                if (numbers[i] < min) {
                    min = numbers[i]
                }
            }
            return min
        }

        /**
         * 计算绝对值
         *
         * @param value 数值
         * @return 绝对值
         */
        fun abs(value: Int): Int {
            return kotlin.math.abs(value)
        }

        /**
         * 计算绝对值
         *
         * @param value 数值
         * @return 绝对值
         */
        fun abs(value: Long): Long {
            return kotlin.math.abs(value)
        }

        /**
         * 计算绝对值
         *
         * @param value 数值
         * @return 绝对值
         */
        fun abs(value: Float): Float {
            return kotlin.math.abs(value)
        }

        /**
         * 计算绝对值
         *
         * @param value 数值
         * @return 绝对值
         */
        fun abs(value: Double): Double {
            return kotlin.math.abs(value)
        }

        /**
         * 四舍五入
         *
         * @param value 数值
         * @return 四舍五入后的整数
         */
        fun round(value: Float): Int {
            return Math.round(value)
        }

        /**
         * 四舍五入
         *
         * @param value 数值
         * @return 四舍五入后的长整数
         */
        fun round(value: Double): Long {
            return Math.round(value)
        }

        /**
         * 向上取整
         *
         * @param value 数值
         * @return 向上取整后的值
         */
        fun ceil(value: Double): Double {
            return kotlin.math.ceil(value)
        }

        /**
         * 向下取整
         *
         * @param value 数值
         * @return 向下取整后的值
         */
        fun floor(value: Double): Double {
            return kotlin.math.floor(value)
        }

        /**
         * 保留指定小数位数（四舍五入）
         *
         * @param value  数值
         * @param scale  小数位数
         * @return 保留指定小数位数后的值
         */
        fun round(value: Double, scale: Int): Double {
            require(scale >= 0) { "scale must be positive" }
            var bd = BigDecimal(value.toString())
            bd = bd.setScale(scale, RoundingMode.HALF_UP)
            return bd.toDouble()
        }

        /**
         * 精确加法运算
         *
         * @param v1 加数1
         * @param v2 加数2
         * @return 和
         */
        fun add(v1: Double, v2: Double): Double {
            val b1 = BigDecimal(v1.toString())
            val b2 = BigDecimal(v2.toString())
            return b1.add(b2).toDouble()
        }

        /**
         * 精确减法运算
         *
         * @param v1 被减数
         * @param v2 减数
         * @return 差
         */
        fun subtract(v1: Double, v2: Double): Double {
            val b1 = BigDecimal(v1.toString())
            val b2 = BigDecimal(v2.toString())
            return b1.subtract(b2).toDouble()
        }

        /**
         * 精确乘法运算
         *
         * @param v1 乘数1
         * @param v2 乘数2
         * @return 积
         */
        fun multiply(v1: Double, v2: Double): Double {
            val b1 = BigDecimal(v1.toString())
            val b2 = BigDecimal(v2.toString())
            return b1.multiply(b2).toDouble()
        }

        /**
         * 精确除法运算
         *
         * @param v1    被除数
         * @param v2    除数
         * @param scale 保留小数位数
         * @return 商
         */
        /**
         * 精确除法运算（默认保留10位小数）
         *
         * @param v1 被除数
         * @param v2 除数
         * @return 商
         */
        @JvmOverloads
        fun divide(v1: Double, v2: Double, scale: Int = 10): Double {
            require(scale >= 0) { "scale must be positive" }
            if (v2 == 0.0) {
                throw ArithmeticException("Division by zero")
            }
            val b1 = BigDecimal(v1.toString())
            val b2 = BigDecimal(v2.toString())
            return b1.divide(b2, scale, RoundingMode.HALF_UP).toDouble()
        }

        /**
         * 计算平方
         *
         * @param value 数值
         * @return 平方值
         */
        fun square(value: Double): Double {
            return value * value
        }

        /**
         * 计算平方根
         *
         * @param value 数值
         * @return 平方根
         */
        fun sqrt(value: Double): Double {
            require(!(value < 0)) { "value cannot be negative" }
            return kotlin.math.sqrt(value)
        }

        /**
         * 计算幂次方
         *
         * @param base     底数
         * @param exponent 指数
         * @return 幂次方值
         */
        fun pow(base: Double, exponent: Double): Double {
            return base.pow(exponent)
        }

        /**
         * 限制数值在指定范围内
         *
         * @param value 数值
         * @param min   最小值
         * @param max   最大值
         * @return 限制后的值
         */
        fun clamp(value: Int, min: Int, max: Int): Int {
            return kotlin.math.max(min, kotlin.math.min(max, value))
        }

        /**
         * 限制数值在指定范围内
         *
         * @param value 数值
         * @param min   最小值
         * @param max   最大值
         * @return 限制后的值
         */
        fun clamp(value: Float, min: Float, max: Float): Float {
            return kotlin.math.max(min, kotlin.math.min(max, value))
        }

        /**
         * 限制数值在指定范围内
         *
         * @param value 数值
         * @param min   最小值
         * @param max   最大值
         * @return 限制后的值
         */
        fun clamp(value: Double, min: Double, max: Double): Double {
            return kotlin.math.max(min, kotlin.math.min(max, value))
        }

        /**
         * 判断数值是否在指定范围内（包含边界）
         *
         * @param value 数值
         * @param min   最小值
         * @param max   最大值
         * @return true表示在范围内，false表示不在
         */
        fun inRange(value: Int, min: Int, max: Int): Boolean {
            return value >= min && value <= max
        }

        /**
         * 判断数值是否在指定范围内（包含边界）
         *
         * @param value 数值
         * @param min   最小值
         * @param max   最大值
         * @return true表示在范围内，false表示不在
         */
        fun inRange(value: Double, min: Double, max: Double): Boolean {
            return value >= min && value <= max
        }
    }
}

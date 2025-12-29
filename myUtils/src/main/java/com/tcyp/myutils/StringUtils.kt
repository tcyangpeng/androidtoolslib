package com.tcyp.myutils


/**
 * 字符串工具类，提供常用的字符串处理方法
 */
object StringUtils {

    /**
     * 判断字符串是否为空或null
     * @param str 待检查的字符串
     * @return 如果字符串为null或空字符串则返回true，否则返回false
     */
    fun isEmpty(str: String?): Boolean {
        return str == null || str.isEmpty()
    }

    /**
     * 判断字符串是否不为空
     * @param str 待检查的字符串
     * @return 如果字符串不为null且不为空字符串则返回true，否则返回false
     */
    fun isNotEmpty(str: String?): Boolean {
        return !isEmpty(str)
    }

    /**
     * 判断字符串是否为空白（null、空字符串或只包含空白字符）
     * @param str 待检查的字符串
     * @return 如果字符串为null、空字符串或只包含空白字符则返回true，否则返回false
     */
    fun isBlank(str: String?): Boolean {
        return str == null || str.trim().isEmpty()
    }

    /**
     * 判断字符串是否不为空白
     * @param str 待检查的字符串
     * @return 如果字符串不为null、不为空字符串且不只包含空白字符则返回true，否则返回false
     */
    fun isNotBlank(str: String?): Boolean {
        return !isBlank(str)
    }

    /**
     * 安全地截取字符串，防止越界
     * @param str 原字符串
     * @param start 起始位置
     * @param end 结束位置（不包含）
     * @return 截取后的字符串，如果原字符串为null则返回空字符串
     */
    fun safeSubstring(str: String?, start: Int, end: Int = -1): String {
        if (str == null) return ""
        val actualStart = start.coerceIn(0, str.length)
        val actualEnd = if (end == -1) str.length else end.coerceIn(0, str.length)
        return if (actualStart <= actualEnd) str.substring(actualStart, actualEnd) else ""
    }

    /**
     * 安全地获取字符串长度
     * @param str 待检查的字符串
     * @return 字符串长度，如果字符串为null则返回0
     */
    fun safeLength(str: String?): Int {
        return str?.length ?: 0
    }

    /**
     * 将字符串首字母大写
     * @param str 待处理的字符串
     * @return 首字母大写后的字符串
     */
    fun capitalize(str: String): String {
        return if (str.isEmpty()) str else str[0].uppercase() + str.substring(1)
    }

    /**
     * 将字符串首字母小写
     * @param str 待处理的字符串
     * @return 首字母小写后的字符串
     */
    fun uncapitalize(str: String): String {
        return if (str.isEmpty()) str else str[0].lowercase() + str.substring(1)
    }

    /**
     * 去除字符串两端空白字符
     * @param str 待处理的字符串
     * @return 去除两端空白后的字符串，如果原字符串为null则返回空字符串
     */
    fun trim(str: String?): String {
        return str?.trim() ?: ""
    }

    /**
     * 去除字符串中所有空白字符
     * @param str 待处理的字符串
     * @return 去除所有空白后的字符串，如果原字符串为null则返回空字符串
     */
    fun removeAllWhitespace(str: String?): String {
        return str?.replace(Regex("\\s+"), "") ?: ""
    }

    /**
     * 判断字符串是否为数字
     * @param str 待检查的字符串
     * @return 如果字符串是数字则返回true，否则返回false
     */
    fun isNumeric(str: String?): Boolean {
        return str?.isNotEmpty() == true && str.all { it.isDigit() }
    }

    /**
     * 判断字符串是否为有效的邮箱格式
     * @param email 待检查的邮箱字符串
     * @return 如果邮箱格式有效则返回true，否则返回false
     */
    fun isValidEmail(email: String?): Boolean {
        return email?.matches(Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) == true
    }

    /**
     * 判断字符串是否为有效的手机号格式（中国大陆）
     * @param phone 待检查的手机号字符串
     * @return 如果手机号格式有效则返回true，否则返回false
     */
    fun isValidPhone(phone: String?): Boolean {
        return phone?.matches(Regex("^1[3-9]\\d{9}$")) == true
    }

    /**
     * 替换字符串中的敏感词
     * @param str 原字符串
     * @param sensitiveWords 敏感词列表
     * @param replacement 替换字符
     * @return 替换后的字符串
     */
    fun replaceSensitiveWords(str: String, sensitiveWords: List<String>, replacement: String = "*"): String {
        var result = str
        sensitiveWords.forEach { word ->
            if (word.isNotEmpty()) {
                result = result.replace(word, replacement.repeat(word.length))
            }
        }
        return result
    }

    /**
     * 格式化字符串，将null值转换为默认值
     * @param str 待处理的字符串
     * @param default 默认值
     * @return 如果字符串为null则返回默认值，否则返回原字符串
     */
    fun formatWithDefault(str: String?, default: String = ""): String {
        return str ?: default
    }

    /**
     * 截断字符串并在末尾添加省略号
     * @param str 待处理的字符串
     * @param maxLength 最大长度
     * @param suffix 后缀，默认为"..."
     * @return 截断后的字符串
     */
    fun truncate(str: String?, maxLength: Int, suffix: String = "..."): String {
        if (str == null) return ""
        return if (str.length <= maxLength) str else str.substring(0, maxLength - suffix.length) + suffix
    }

    /**
     * 统计字符串中某个字符出现的次数
     * @param str 待检查的字符串
     * @param char 要统计的字符
     * @return 字符出现的次数
     */
    fun countChar(str: String?, char: Char): Int {
        return str?.count { it == char } ?: 0
    }

    /**
     * 将字符串按照指定长度分割
     * @param str 待分割的字符串
     * @param chunkSize 每段的长度
     * @return 分割后的字符串列表
     */
    fun chunk(str: String, chunkSize: Int): List<String> {
        if (chunkSize <= 0) return emptyList()
        return str.chunked(chunkSize)
    }

    /**
     * 比较两个字符串的相似度（0-1之间的值）
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 相似度，0表示完全不相似，1表示完全相同
     */
    fun similarity(str1: String, str2: String): Double {
        if (str1 == str2) return 1.0
        if (str1.isEmpty() && str2.isEmpty()) return 1.0
        if (str1.isEmpty() || str2.isEmpty()) return 0.0

        val maxLength = maxOf(str1.length, str2.length)
        val distance = levenshteinDistance(str1, str2)

        return (maxLength - distance).toDouble() / maxLength
    }

    /**
     * 计算两个字符串的编辑距离（Levenshtein距离）
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 编辑距离
     */
    private fun levenshteinDistance(str1: String, str2: String): Int {
        val m = str1.length
        val n = str2.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) {
            dp[i][0] = i
        }

        for (j in 0..n) {
            dp[0][j] = j
        }

        for (j in 1..n) {
            for (i in 1..m) {
                val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1, // 删除
                    dp[i][j - 1] + 1, // 插入
                    dp[i - 1][j - 1] + cost // 替换
                )
            }
        }

        return dp[m][n]
    }
}
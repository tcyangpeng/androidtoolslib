package com.tcyp.myutils.data.sp

import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * MMKV 封装工具类（线程安全、支持泛型、支持对象存取）
 * 使用前确保已初始化 MMKV：
 *   MMKV.initialize(context)   // 建议放在 Application.onCreate()
 *
 * 使用方法：在application中oncreate() 中初始化MMKV.initialize(this)
 *
 * Kotlin// 1. 基础用法
 * MMKVUtils.putString("token", "abc123")
 * val token = MMKVUtils.getString("token")
 *
 * MMKVUtils.putBoolean("isFirstLaunch", false)
 * val isFirst = MMKVUtils.getBoolean("isFirstLaunch", true)
 *
 * // 2. 存对象/列表
 * data class User(val id: Long, val name: String)
 * val user = User(1, "张三")
 * MMKVUtils.putObject("user", user)
 * val user2: User? = MMKVUtils.getObject("user")
 *
 * val list = listOf("A", "B", "C")
 * MMKVUtils.putObject("stringList", list)
 * val list2: List<String>? = MMKVUtils.getObject("stringList")
 *
 * // 3. 属性委托（最优雅）
 * class UserConfig {
 *     var token: String? by mmkv("token")
 *     var isLogin: Boolean by mmkv("is_login", false)
 *     var user: User? by mmkv("user")
 * }
 *
 * // 直接使用
 * UserConfig().token = "new_token"
 * println(UserConfig().token)
 */
object MMKVUtils {
    public val gson = Gson()

    // 默认的 MMKV 实例（多进程模式看需求）
    val defaultMMKV: MMKV by lazy(LazyThreadSafetyMode.NONE) {
        MMKV.defaultMMKV()
    }

    // 可选：支持多文件（分模块存储）
    fun mmkvWithId(mmapID: String, mode: Int = MMKV.MULTI_PROCESS_MODE): MMKV {
        return MMKV.mmkvWithID(mmapID, mode)
    }

    // ==================== 基础类型直接读写 ====================

    fun put(key: String, value: Any?) {
        when (value) {
            is String -> defaultMMKV.encode(key, value)
            is Int -> defaultMMKV.encode(key, value)
            is Long -> defaultMMKV.encode(key, value)
            is Float -> defaultMMKV.encode(key, value)
            is Double -> defaultMMKV.encode(key, value)
            is Boolean -> defaultMMKV.encode(key, value)
            is ByteArray -> defaultMMKV.encode(key, value)
            null -> remove(key)                                   // 支持存 null 即删除
            else -> defaultMMKV.encode(key, gson.toJson(value))   // 其他对象走 Gson 序列化
        }
    }

    inline fun <reified T> get(key: String, default: T? = null): T? {
        return when (T::class) {
            String::class -> defaultMMKV.decodeString(key, default as? String) as T?
            Int::class -> defaultMMKV.decodeInt(key, default as? Int ?: 0) as T?
            Long::class -> defaultMMKV.decodeLong(key, default as? Long ?: 0L) as T?
            Float::class -> defaultMMKV.decodeFloat(key, default as? Float ?: 0f) as T?
            Double::class -> defaultMMKV.decodeDouble(key, default as? Double ?: 0.0) as T?
            Boolean::class -> defaultMMKV.decodeBool(key, default as? Boolean ?: false) as T?
            ByteArray::class -> defaultMMKV.decodeBytes(key, default as? ByteArray) as T?
            else -> {
                val json = defaultMMKV.decodeString(key, null)
                if (json != null) {
                    gson.fromJson(json, T::class.java)
                } else {
                    default
                }
            }
        }
    }

    //===========常用便捷函数==========
    fun putString(key: String, value: String?) = put(key, value)
    fun putInt(key: String, value: Int) = defaultMMKV.encode(key, value)
    fun putLong(key: String, value: Long) = defaultMMKV.encode(key, value)
    fun putFloat(key: String, value: Float) = defaultMMKV.encode(key, value)
    fun putDouble(key: String, value: Double) = defaultMMKV.encode(key, value)
    fun putBoolean(key: String, value: Boolean) = defaultMMKV.encode(key, value)
    fun putBytes(key: String, value: ByteArray?) = defaultMMKV.encode(key, value)

    fun getString(key: String, def: String = "") = defaultMMKV.decodeString(key, def)!!
    fun getInt(key: String, def: Int = 0) = defaultMMKV.decodeInt(key, def)
    fun getLong(key: String, def: Long = 0L) = defaultMMKV.decodeLong(key, def)
    fun getFloat(key: String, def: Float = 0f) = defaultMMKV.decodeFloat(key, def)
    fun getDouble(key: String, def: Double = 0.0) = defaultMMKV.decodeDouble(key, def)
    fun getBoolean(key: String, def: Boolean = false) = defaultMMKV.decodeBool(key, def)
    fun getBytes(key: String) = defaultMMKV.decodeBytes(key)

    fun contains(key: String) = defaultMMKV.containsKey(key)
    fun remove(key: String) = defaultMMKV.removeValueForKey(key)
    fun clearAll() = defaultMMKV.clearAll()
    fun allKeys() = defaultMMKV.allKeys()?.toList() ?: emptyList()

    // ==================== 支持 Set 类型（StringSet）================
    fun putStringSet(key: String, value: Set<String>?) {
        defaultMMKV.encode(key, value)
    }

    fun getStringSet(key: String, def: Set<String> = emptySet()): Set<String> {
        return defaultMMKV.decodeStringSet(key, def) ?: def
    }

    // ==================== 对象/列表 自动序列化 ====================
    inline fun <reified T> putObject(key: String, obj: T?) {
        if (obj == null) {
            remove(key)
        } else {
            put(key, obj)   // 内部已走 Gson
        }
    }

    inline fun <reified T> getObject(key: String, default: T? = null): T? {
        return get(key, default)
    }

    // ==================== Kotlin 属性委托================
    class MMKVProperty<T>(
        val key: String,
        val default: T? = null
    ) : ReadWriteProperty<Any?, T?> {

        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadWriteProperty<Any?, T?> =
            MMKVProperty(key.ifEmpty { property.name }, default)

        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return get(key, default)
        }

        override fun setValue(
            thisRef: Any?,
            property: KProperty<*>,
            value: T?
        ) {
            put(key, value)
        }

    }

}



inline fun <reified T> mmkv(key: String, default: T? = null) = MMKVUtils.MMKVProperty<T>(key, default)
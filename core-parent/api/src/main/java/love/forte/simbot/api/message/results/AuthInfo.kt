/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     AuthInfo.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */
@file:JvmName("AuthInfos")
package love.forte.simbot.api.message.results

/**
 * bot的权限信息
 */
public interface AuthInfo : Result {

    /**
     * 得到cookies信息。
     * cookies不为null，但是不保证其内部存在信息。
     */
    val cookies: Cookies

    /**
     * 如果存在，则得到token信息。在qq中，token一般指代bkn值。
     * 其他应用中参考其文档说明。
     */
    val token: String?

    /**
     * Cookies信息封装接口
     * @see CookieValuesImpl [Cookies]的默认实现。
     */
    public interface Cookies {
        operator fun get(key: String): String?
        fun toMap(): MutableMap<String, String>
        override fun toString(): String
    }
}

private val COOKIE_SPLIT_REGEX = Regex("; *")

/**
 * 根据 cookies 字符串的实例。
 */
public data class CookieValuesImpl(private val cookieValue: String) : AuthInfo.Cookies {

    private val cookieMap: MutableMap<String, String> by lazy(LazyThreadSafetyMode.PUBLICATION) {
        cookieValue.split(COOKIE_SPLIT_REGEX).asSequence().filter {
            it.isNotBlank()
        }.map {
            val sp: List<String> = it.split("=", limit = 2)
            if(sp.size == 1){
                sp[0] to ""
            }else{
                sp[0] to sp[1]
            }
        }.toMap().toMutableMap()
    }

    override fun get(key: String): String? = cookieMap[key]
    override fun toMap(): MutableMap<String, String> = cookieMap

    override fun toString(): String = cookieMap.toString()
}
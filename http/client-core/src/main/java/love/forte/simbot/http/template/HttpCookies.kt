/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     HttpCookies.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */
@file:JvmName("HttpCookiesUtil")

package love.forte.simbot.http.template


/**
 * http简易的cookie数据模板
 */
public interface HttpCookie {
    val name: String
    val value: String
    val domain: String?
    val path: String?

    @JvmDefault
    val maxAge: Int
        get() = 0

}

@JvmName("getHttpCookie")
@JvmOverloads
public fun httpCookie(
    name: String,
    value: String,
    domain: String? = null,
    path: String? = null,
    maxAge: Int = 0
): HttpCookie = HttpCookieData(name, value, domain, path, maxAge)


private data class HttpCookieData(
    override val name: String,
    override val value: String,
    override val domain: String?,
    override val path: String?,
    override val maxAge: Int
) : HttpCookie {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HttpCookieData

        if (name != other.name) return false
        if (value != other.value) return false
        if (domain != other.domain) return false
        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + (domain?.hashCode() ?: 0)
        result = 31 * result + (path?.hashCode() ?: 0)
        return result
    }
}


/**
 * http 简易cookie列表信息。
 */
public interface HttpCookies : MutableSet<HttpCookie> {
    /**
     * 添加一条cookie信息。
     */
    fun add(name: String, value: String): Boolean
    fun add(name: String, value: String, domain: String?): Boolean
    fun add(name: String, value: String, domain: String?, path: String?): Boolean
    fun add(name: String, value: String, domain: String?, path: String?, maxAge: Int): Boolean
}


@JvmName("getHttpCookies")
public fun httpCookies(cookieValueMap: Map<String, String>): HttpCookies {
    return HttpCookiesImpl(mutableSetOf()).apply {
        cookieValueMap.forEach { (name, value) ->
            add(name, value)
        }
    }
}

@JvmName("getHttpCookies")
public fun httpCookies(cookies: Collection<HttpCookie>): HttpCookies {
    return HttpCookiesImpl(cookies.toMutableSet())
}



private class HttpCookiesImpl(private val set: MutableSet<HttpCookie>) : HttpCookies, MutableSet<HttpCookie> by set {
    /**
     * 添加一条cookie信息。
     */
    override fun add(name: String, value: String): Boolean {
        return add(httpCookie(name, value))
    }

    override fun add(name: String, value: String, domain: String?): Boolean {
        return add(httpCookie(name, value, domain))
    }

    override fun add(name: String, value: String, domain: String?, path: String?): Boolean {
        return add(httpCookie(name, value, domain, path))
    }

    override fun add(name: String, value: String, domain: String?, path: String?, maxAge: Int): Boolean {
        return add(httpCookie(name, value, domain, path, maxAge))
    }
}













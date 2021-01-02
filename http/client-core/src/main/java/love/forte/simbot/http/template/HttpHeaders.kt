/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("HttpHeadersTemplate")
package love.forte.simbot.http.template

public const val USER_AGENT_KEY_NAME = "User-Agent"
public const val USER_AGENT_WIN10_CHROME =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36"
public const val USER_AGENT_MAC_FIREFOX = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1"


public var HttpHeaders.userAgent: String?
    get() = this[USER_AGENT_KEY_NAME]?.firstOrNull()
    set(value) {
        value?.let {
            this.set(USER_AGENT_KEY_NAME, it)
        }
    }

public fun HttpHeaders.setUserAgentChrome() {
    this.userAgent = USER_AGENT_WIN10_CHROME
}
public fun HttpHeaders.setUserAgentFireFox() {
    this.userAgent = USER_AGENT_MAC_FIREFOX
}
public fun HttpHeaders.setUserAgentChromeIfAbsent() {
    if (this.userAgent == null){
        this.setUserAgentChrome()
    }
}
public fun HttpHeaders.setUserAgentFireFoxIfAbsent() {
    if (this.userAgent == null) {
        this.setUserAgentFireFox()
    }
}

/**
 *
 * http 请求头列表类型，可用于请求或者接受相应。
 *
 * 请求头类似于键值对格式，但是key对应的value是可以存在多个的，
 * 因此其为一个 `Map<String, List<String>>` 。
 *
 *
 */
public interface HttpHeaders : MutableMap<String, MutableList<String>> {

    /**
     * 获取请求头多个值中的第一个。
     * @return 请求头的第一个值。如果没有此请求头则返回null。
     */
    fun getFirst(headerKey: String): String?

    /**
     * 获取请求头的指定索引的值。
     * @return 请求头中的指定值。如果没有此请求头则返回null。
     * @throws IndexOutOfBoundsException 如果存在请求头，但是索引越界了
     */
    fun getIndexed(headerKey: String, index: Int): String?

    /**
     * 添加一个值。
     */
    fun add(header: String, value: String)


    /**
     * 添加多个值。
     */
    fun addMultiple(header: String, headerValues: Collection<String>)


    /**
     * 设置一个值。会覆盖原有的值。等同于put，但是set的值是一个单个的值。
     */
    fun set(header: String, value: String)



    /** 提供部分默认实现。 */
    companion object {

        /**
         * 获取一个内容为空的基础实例。
         */
        val instance: HttpHeaders get() = MapHttpHeaders()

        /**
         * 根据单键值对map构建 [HttpHeaders] 实例。
         */
        fun fromSingleValueMap(map: Map<String, String>): HttpHeaders {
            return MapHttpHeaders(map.mapValues { mutableListOf(it.value) }.toMutableMap())
        }

        /**
         * 根据多值键值对map构建 [HttpHeaders] 实例。
         */
        fun fromMultiValueMap(map: Map<String, List<String>>): HttpHeaders {
            return MapHttpHeaders(map.mapValues { it.value.toMutableList() }.toMutableMap())
        }
    }


}


/**
 * 基于 [Map] 实现的 [HttpHeaders].
 */
private class MapHttpHeaders(private val map: MutableMap<String, MutableList<String>> = mutableMapOf()) : HttpHeaders, MutableMap<String, MutableList<String>> by map {

    /**
     * 获取第一个。
     */
    override fun getFirst(headerKey: String): String? = getIndexed(headerKey, 0)

    /**
     * 指定索引。
     */
    override fun getIndexed(headerKey: String, index: Int): String? =
        map[headerKey]?.takeIf { it.isNotEmpty() }?.get(index)

    /**
     * 添加一个单个的值。
     */
    override fun add(header: String, value: String) {
        map.compute(header) {_, old ->
            old?.apply { add(value) } ?: mutableListOf(value)
        }
    }

    /**
     * 添加多个。
     */
    override fun addMultiple(header: String, headerValues: Collection<String>) {
        map.compute(header) { _, old ->
            val valueList: MutableList<String> = headerValues.toMutableList()
            if (old != null) {
                valueList.addAll(old)
            }
            valueList
        }
    }

    /**
     * 覆盖一个值。
     */
    override fun set(header: String, value: String) {
        put(header, mutableListOf(value))
    }

    override fun toString(): String = "HttpHeader$map"


}













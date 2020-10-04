/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Reply.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.api.message

import java.util.*

/*
    提供一些默认的Reply以表示一些常见的返回形式。
 */

/**
 * 快捷回复类，提供一些常见的快速回复内容。
 * [Reply]实现了[Map]接口，可以被视为Map使用。但是需要注意的是，[Reply]是不可变的。
 *
 * 按照规定：
 * - reply 代表快捷回复的消息，用于消息回复，string类型
 * - quote 代表是否要引用被回复人的消息，boolean类型
 * - at 代表是否要at被回复人，boolean类型
 * - process 代表请求类型的消息的时候是否同意当前请求，boolean类型
 *
 *
 */
public data class Reply
internal constructor(
    val reply: String,
    val quote: Boolean/* = true*/,
    val at: Boolean/* = true*/,
    val process: Boolean/* = false*/
) : Map<String, Any> {
    private val entriesSet: Set<Map.Entry<String, Any>> = setOf(
        AbstractMap.SimpleEntry<String, Any>("reply", reply),
        AbstractMap.SimpleEntry<String, Any>("quote", quote),
        AbstractMap.SimpleEntry<String, Any>("at", at),
        AbstractMap.SimpleEntry<String, Any>("process", process),
    )
    private val keySet: Set<String> = entriesSet.map { it.key }.toSet()
    private val valueCollection: Collection<Any> = entriesSet.map { it.value }

    /**
     * Returns a read-only [Set] of all key/value pairs in this map.
     */
    override val entries: Set<Map.Entry<String, Any>> get() = entriesSet

    /**
     * Returns a read-only [Set] of all keys in this map.
     */
    override val keys: Set<String> = keySet

    /**
     * Returns the number of key/value pairs in the map.
     */
    override val size: Int = 4

    /**
     * Returns a read-only [Collection] of all values in this map. Note that this collection may contain duplicate values.
     */
    override val values: Collection<Any>
        get() = valueCollection

    /**
     * Returns `true` if the map contains the specified [key].
     */
    override fun containsKey(key: String): Boolean = keySet.contains(key)

    /**
     * Returns `true` if the map maps one or more keys to the specified [value].
     */
    override fun containsValue(value: Any): Boolean = valueCollection.contains(value)

    /**
     * Returns the value corresponding to the given [key], or `null` if such a key is not present in the map.
     */
    override fun get(key: String): Any? {
        return when (key) {
            "reply" -> reply
            "quote" -> quote
            "at" -> at
            "process" -> process
            else -> null
        }
    }

    /**
     * Returns `true` if the map is empty (contains no elements), `false` otherwise.
     */
    override fun isEmpty(): Boolean = false


    /**
     * 通过静态方法获取实例
     */
    companion object {
        @JvmStatic
        fun quickReply(text: String, quote: Boolean = true, at: Boolean = true) : Reply {
            return Reply(text, quote, at, false)
        }
        @JvmStatic fun quickAccept() : Reply = Accept
        @JvmStatic fun quickReject() : Reply = Reject

        /** 同意某请求 */
        private val Accept: Reply = Reply("", quote = true, at = true, process = true)
        /** 拒绝某请求 */
        private val Reject: Reply = Reply("", quote = true, at = true, process = false)
    }

}


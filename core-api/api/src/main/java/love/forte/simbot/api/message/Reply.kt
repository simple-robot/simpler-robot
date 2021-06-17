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

@file:Suppress("DEPRECATION")

package love.forte.simbot.api.message

import love.forte.common.collections.SimpleEntry
import love.forte.common.collections.arraySetOf


/**
 * 可回复接口。
 * 此接口的实现类可以提供一些通用的快速回复模板方法。
 *
 * 按照约定：
 *  - `reply` 代表快捷回复的消息，用于消息回复，string类型
 *  - `quote` 代表是否要引用被回复人的消息，boolean类型
 *  - `at` 代表是否要at被回复人，boolean类型
 *  - `process` 代表请求类型的消息的时候 同意/拒绝 当前请求，boolean类型。为null则视为不处理。
 *
 * 提供一个简便的默认实现类 [Reply]。不出意外的话，直接通过 [Reply] 所提供的静态方法构建实例即可。
 *
 *
 * @see Reply
 */
public interface ReplyAble {
    val at: Boolean
    val reply: CharSequence?
    val process: Boolean?

    /**
     * 一个空内容、无效化的 [ReplyAble] 实现。
     */
    companion object Empty : ReplyAble {
        override val reply: CharSequence? get() = null
        override val at: Boolean get() = false
        override val process: Boolean? get() = null
    }

}


/*
    提供一些默认的Reply以表示一些常见的返回形式。
 */

/**
 * 快捷回复类，提供一些常见的快速回复内容。
 * [Reply]实现了[Map]接口，可以被视为Map使用。但是需要注意的是，[Reply]是不可变的。
 * [Reply]实现了[ReplyAble]接口，可以被视为ReplyAble使用并提供快速回复功能。
 *
 *
 *
 */
public data class Reply
internal constructor(
    /**
     * 快速回复的文本消息内容，等同于使用 [love.forte.simbot.api.sender.Sender] 中的对应方法。
     */
    override val reply: CharSequence?,

    /**
     *  进行回复的时候，是否at消息回复目标。
     *  此参数只有在能够AT他人的地方使用，例如群聊时。
     */
    override val at: Boolean,

    /**
     * 同意或拒绝当前申请。
     * 只有当当前消息类型为一个 **申请** 相关的消息时有效，例如加群申请事件。
     */
    override val process: Boolean?,
) : Map<String, Any>, ReplyAble {

    private val entriesSet: Set<Map.Entry<String, Any>> = when {
        reply == null && process != null -> {
            arraySetOf(
                SimpleEntry<String, Any>("at", at),
                SimpleEntry<String, Any>("process", process),
            )
        }
        process == null && reply != null -> {
            arraySetOf(
                SimpleEntry<String, Any>("reply", reply),
                SimpleEntry<String, Any>("at", at),
            )
        }
        process == null && reply == null -> setOf(SimpleEntry("at", at))

        else -> {
            arraySetOf(
                SimpleEntry<String, Any>("reply", reply!!),
                SimpleEntry<String, Any>("at", at),
                SimpleEntry<String, Any>("process", process!!),
            )
        }
    }

    private val keySet: Set<String> = entriesSet.mapTo(mutableSetOf()) { it.key }


    private val valueCollection: Collection<Any> = entriesSet.map { it.value }

    // -- map --

    /**
     * Returns a read-only [Set] of all key/value pairs in this map.
     */
    override val entries: Set<Map.Entry<String, Any>> get() = entriesSet

    /**
     * Returns a read-only [Set] of all keys in this map.
     */
    override val keys: Set<String> get() = keySet

    /**
     * Returns the number of key/value pairs in the map.
     */
    override val size: Int get() = entriesSet.size

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
            "at" -> at
            "process" -> process
            else -> null
        }
    }

    /**
     * Never empty.
     */
    override fun isEmpty(): Boolean = false


    /**
     * 通过静态方法获取实例
     */
    companion object {

        /**
         * 回复消息。
         */
        @JvmStatic
        fun reply(
            text: CharSequence?,
            at: Boolean = true,
        ): ReplyAble {
            if (text == null && !at) {
                return ReplyAble
            }
            return Reply(text, at, null)
        }

        /**
         * 通过请求。
         */
        @JvmStatic
        fun accept(): ReplyAble = Accept

        /**
         * 拒绝请求。
         */
        @JvmStatic
        fun reject(): ReplyAble = Reject



        /** 同意某请求 */
        private val Accept: ReplyAble = Reply(null, at = false, process = true)

        /** 拒绝某请求 */
        private val Reject: ReplyAble = Reply(null, at = false, process = false)
    }

}


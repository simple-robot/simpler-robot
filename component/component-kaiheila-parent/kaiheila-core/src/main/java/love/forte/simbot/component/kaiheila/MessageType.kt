/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

@file:Suppress("unused")

package love.forte.simbot.component.kaiheila

import kotlinx.serialization.Serializable


/**
 *
 * 消息的类型。
 * [消息发送 - type字段](https://developer.kaiheila.cn/doc/http/message#%E5%8F%91%E9%80%81%E9%A2%91%E9%81%93%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
public sealed class MessageType protected constructor(val type: Int) {
    companion object {
        @JvmField
        val TEXT: MessageType = StandardMessageType("TEXT", 1)
        @JvmField
        val IMAGE: MessageType = StandardMessageType("IMAGE", 2)
        @JvmField
        val VIDEO: MessageType = StandardMessageType("VIDEO", 3)
        @JvmField
        val FILE: MessageType = StandardMessageType("FILE", 4)
        @JvmField
        val KMARKDOWN: MessageType = StandardMessageType("KMARKDOWN", 9)
        @JvmField
        val CARD: MessageType = StandardMessageType("CARD", 10)
    }

    class StandardMessageType internal constructor(private val standardName: String, type: Int) : MessageType(type) {
        override fun toString(): String = "StandardMessageType(name=$standardName, type=$type)"
    }
    class MixedMessageType(type: Int) : MessageType(type)


    override fun toString(): String = "MessageType(type=$type)"
}
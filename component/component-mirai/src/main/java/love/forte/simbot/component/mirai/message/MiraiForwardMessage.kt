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

package love.forte.simbot.component.mirai.message

import catcode.CatCodeUtil
import catcode.Neko
import cn.hutool.core.lang.UUID
import love.forte.simbot.component.mirai.utils.toNeko
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.MiraiExperimentalApi

/**
 * Mirai 的合并转发消息.
 *
 */
public class MiraiRawForwardMessage(
    private val forwardMessage: ForwardMessage,
    cache: MiraiMessageCache? = null,
) : MiraiMessageContent() {
    override suspend fun getMessage(contact: Contact) = forwardMessage

    private companion object {
        // private val nodeListSerializer = ListSerializer(ForwardMessage.Node.serializer())
    }


    override val cats: List<Neko> by lazy {
        val id = UUID.fastUUID().toString(true)
        val forwardNeko = CatCodeUtil.getNekoBuilder("forward", true)
            .key("sId").value(id)
            .key("preview").value(forwardMessage.preview)
            .key("title").value(forwardMessage.title)
            .key("brief").value(forwardMessage.brief)
            .key("source").value(forwardMessage.source)
            .key("summary").value(forwardMessage.summary)
            .key("preview").value(forwardMessage.preview)
            .key("nodeSize").value(forwardMessage.nodeList.size)
            .build()

        val nodeNekoList = forwardMessage.nodeList.map { n ->
            val chainSize = n.messageChain.size
            CatCodeUtil.getNekoBuilder("forward-node", true)
                .key("sId").value(id)
                .key("senderId").value(n.senderId)
                .key("senderName").value(n.senderName)
                .key("time").value(n.time)
                .key("chainSize").value(chainSize)
                .key("formattedNeko").value(n.messageChain.toNeko(cache))
                .build()
        }

        mutableListOf(forwardNeko).apply { addAll(nodeNekoList) }
    }

    override fun equals(other: Any?): Boolean {
        if (other is MiraiRawForwardMessage) {
            return forwardMessage == other.forwardMessage
        }
        if (other is MiraiMessageChainContent) {
            val forward = other.message[ForwardMessage] ?: return false
            return forwardMessage == forward
        }

        return false
    }

    override fun hashCode(): Int = forwardMessage.hashCode()
}


public class MiraiForwardMessage(
    private val buildBlock: suspend ForwardMessageBuilder.(Contact) -> Unit,
) : MiraiMessageContent() {

    @OptIn(MiraiExperimentalApi::class)
    constructor(nodeList: List<suspend (Contact) -> ForwardMessage.Node>) : this({ c ->
        val nodes = nodeList.map { ng -> ng(c) }
        addAll(nodes)
    })

    private companion object {
        private val NEKO_LIST = listOf(CatCodeUtil.toNeko("forward"))
    }

    @OptIn(MiraiExperimentalApi::class)
    override suspend fun getMessage(contact: Contact): Message {
        return buildForwardMessage(contact) {
            buildBlock(contact)
        }
    }

    override val cats: List<Neko> get() = NEKO_LIST


    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
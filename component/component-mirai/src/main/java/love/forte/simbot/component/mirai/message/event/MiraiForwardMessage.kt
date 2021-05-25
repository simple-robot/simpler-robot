/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     MiraiForwardMessage.kt
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

package love.forte.simbot.component.mirai.message.event

import love.forte.common.utils.secondToMill
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.TimeContainer
import love.forte.simbot.api.message.containers.accountInfo
import love.forte.simbot.api.message.events.MessageContentContainer
import love.forte.simbot.component.mirai.message.MiraiMessageChainContent
import love.forte.simbot.component.mirai.utils.userAvatar
import net.mamoe.mirai.message.data.ForwardMessage


/**
 * mirai [ForwardMessage] 消息参考映射接口。
 *
 * 接口参数均参考自 [ForwardMessage] 中的属性元素。
 *
 */
public interface ForwardMsg {

    /**
     * @see ForwardMessage.preview
     */
    val preview: List<String>

    /**
     * @see ForwardMessage.title
     */
    val title: String


    /**
     *
     * @see ForwardMessage.brief
     */
    val brief: String


    /**
     * @see ForwardMessage.source
     */
    val source: String


    /**
     *
     * @see ForwardMessage.summary
     */
    val summary: String

    /**
     * 消息节点列表。
     */
    val nodes: List<ForwardNode>

}


public data class MiraiForwardMsg(private val msg: ForwardMessage) : ForwardMsg {
    /**
     * @see ForwardMessage.preview
     */
    override val preview: List<String>
        get() = msg.preview

    /**
     * @see ForwardMessage.title
     */
    override val title: String
        get() = msg.title

    /**
     *
     * @see ForwardMessage.brief
     */
    override val brief: String
        get() = msg.brief

    /**
     * @see ForwardMessage.source
     */
    override val source: String
        get() = msg.source

    /**
     *
     * @see ForwardMessage.summary
     */
    override val summary: String
        get() = msg.summary

    /**
     * 消息节点列表。
     */
    override val nodes: List<ForwardNode> by lazy(LazyThreadSafetyMode.NONE) { msg.nodeList.map { node -> MiraiForwardNode(node) } }
}





/**
 * 转发消息的节点信息。
 * 每个消息节点就是一个消息链，即某个人发送的一段消息。因此消息依然有可能继续嵌套消息。
 */
public interface ForwardNode : AccountContainer, TimeContainer, MessageContentContainer {

    /**
     * 发送人信息。
     */
    override val accountInfo: AccountInfo

    /**
     * 时间戳。应该转化为 **毫秒**。
     */
    override val time: Long


    /**
     * 此节点对应的消息链。
     */
    override val msgContent: MessageContent


    /**
     * 节点可能是嵌套的，当没有嵌套节点的时候[nodes]得到null。
     *
     */
    val nodes: List<ForwardMsg>
}



/**
 * [ForwardNode] 消息节点封装。
 */
public data class MiraiForwardNode(private val node: ForwardMessage.Node) : ForwardNode {

    /**
     * 发送人信息。
     */
    override val accountInfo: AccountInfo = accountInfo(node.senderId.toString(), node.senderName, null, userAvatar(node.senderId))

    /**
     * 时间戳。应该转化为 **毫秒**。
     */
    override val time: Long = node.time.secondToMill()

    /**
     * 此节点对应的消息链。
     */
    override val msgContent: MessageContent = MiraiMessageChainContent(node.messageChain)

    /**
     * 节点可能是嵌套的，当没有嵌套节点的时候[nodes]得到空列表。
     */
    override val nodes: List<ForwardMsg> by lazy(LazyThreadSafetyMode.NONE) {
        node.messageChain.asSequence()
            .filter { msg -> msg is ForwardMessage }
            .map { MiraiForwardMsg(it as ForwardMessage) }
            .toList()
    }
}








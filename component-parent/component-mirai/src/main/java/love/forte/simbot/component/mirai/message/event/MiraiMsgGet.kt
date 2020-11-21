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

package love.forte.simbot.component.mirai.message.event

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.component.mirai.message.result.MiraiBotInfo
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.message.data.PlainText


/**
 * mirai 事件的 [MsgGet] 基类实现
 */
@Suppress("MemberVisibilityCanBePrivate")
public abstract class AbstractMiraiMsgGet<out ME : BotEvent>(
    val event: ME
) : MsgGet {

    /** 当前监听事件消息的ID。一般情况下应当是一个唯一ID。 */
    abstract override val id: String

    /**
     * 账号的信息。一般来讲是不可能为null的，但是其中的信息就不一定了
     */
    abstract override val accountInfo: AccountInfo

    //**************** override ****************//

    /** 消息接收到的时间。一般是一个时间戳。 */
    override val time: Long = System.currentTimeMillis()


    /** 应当重写toString方法 */
    override fun toString(): String = "MiraiEvent($event)"

    /**
     * 得到原始数据字符串。
     */
    override val originalData: String = event.toString()

    /**
     * bot信息。此处的信息将无法获取到level。
     */
    override val botInfo: BotInfo get() = MiraiBotInfo(event.bot)
}


/**
 * mirai消息类型的事件基类。
 */
public abstract class MiraiMessageMsgGet<out ME : MessageEvent>(event: ME) : AbstractMiraiMsgGet<ME>(event),
    MessageGet {

    /** 默认的ID策略，使用source获取。 */
    override val id: String = with(event.source) {
        "${this.fromId}.${this.id}"
    }

    /**
     * 消息事件主体.
     *
     * - 对于好友消息, 这个属性为 [Friend] 的实例, 与 [sender] 引用相同;
     * - 对于临时会话消息, 这个属性为 [Member] 的实例, 与 [sender] 引用相同;
     * - 对于群消息, 这个属性为 [Group] 的实例, 与 [GroupMessageEvent.group] 引用相同
     *
     * 在回复消息时, 可通过 [subject] 作为回复对象
     */
    val subject: Contact get() = event.subject

    /**
     * 消息内容.
     *
     * 第一个元素一定为 [MessageSource], 存储此消息的发送人, 发送时间, 收信人, 消息 id 等数据.
     * 随后的元素为拥有顺序的真实消息内容.
     */
    val message: MessageChain get() = event.message


    override fun isEmptyMsg(): Boolean = message.isEmpty()


    /**
     * 消息字符串，由 [message] 转化为不携带cat码的纯文本字符串。
     */
    override val text: String = message.asSequence().filterIsInstance<PlainText>().joinToString(" ") { it.content } //.takeIf { it.isNotBlank() }

}






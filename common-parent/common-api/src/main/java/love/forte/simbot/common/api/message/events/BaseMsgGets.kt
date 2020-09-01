/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     BaseMsgGets.kt
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

package love.forte.simbot.common.api.message.events

import love.forte.simbot.common.annotations.ParentListenerType
import love.forte.simbot.common.api.message.assists.Flag
import love.forte.simbot.common.api.message.assists.FlagContent
import love.forte.simbot.common.api.message.containers.AccountContainer
import love.forte.simbot.common.api.message.containers.BotContainer
import love.forte.simbot.common.api.message.containers.FlagContainer
import love.forte.simbot.common.api.message.containers.OriginalDataContainer
import java.time.LocalDateTime

/*
    什么? 你问为什么events包下的消息命名还是xxxMsgGet?
    是啊, 为什么呢
 */


/*
 * 此模块下定义基础事件父接口
 */


/**
 * 监听消息的父接口。
 *
 * 所有的监听消息都应当实现的容器：
 * - [原始信息容器][OriginalDataContainer],
 * - [bot基础信息容器][BotContainer],
 * - [用户容器][AccountContainer]
 * @since 2.0.0
 */
@ParentListenerType("所有监听类型的父接口")
public interface MsgGet: OriginalDataContainer, BotContainer, AccountContainer {
    /** 当前监听事件消息的ID。一般情况下应当是一个唯一ID。 */
    val id: String

    /** 监听消息的消息正文文本 */
    var msg: String?

    /** 消息接收到的时间。一般是一个时间戳。 */
    val time: Long

    /** 应当重写toString方法 */
    override fun toString(): String
}


/**
 * 与消息有关的事件
 *
 * [MessageEventGet]中除了需要实现[MsgGet]以外, 还要实现[FlagContainer]以标识一个消息内容的标识。
 * 但是一般来讲, [FlagContent] 都可以用 [id] 来代替。
 *
 * 因此 [FlagContent] 提供为默认方法并使用 [id] 作为返回值。如果有特殊需要则重写
 */
@ParentListenerType("消息事件父接口")
public interface MessageEventGet: MsgGet, FlagContainer<MessageEventGet.MessageFlagContent> {
    /**
     * 消息类型的标识
     */
    override val flag: Flag<MessageFlagContent>

    /**
     * [MessageEventGet] 所对应的 [标识][Flag]
     */
    public interface MessageFlagContent: FlagContent
}



/**
 * 与消息撤回有关的事件, 例如 [群消息撤回][GroupMsgRecall] 或者 [私聊撤回][PrivateMsgRecall]
 *
 *
 * 一般来讲应该可以得到撤回的[消息内容][MsgGet.msg]以及[撤回时间][recallTime]
 */
@ParentListenerType("消息撤回父接口")
public interface MessageRecallEventGet: MsgGet {
    /**
     * 撤回时间。
     * 使用[LocalDateTime]来代表一个准确时间点以防止使用事件戳导致时间格式不统一
     */
    val recallTime: LocalDateTime
}


/**
 * 成员变动事件接口，是[增加事件][IncreaseEventGet] 与 [减少事件][ReduceEventGet]的父接口.
 */
@ParentListenerType("成员数量变动父接口")
public interface MemberChangesEventGet: MsgGet

/**
 * 与 **增加** 有关的事件，例如 群友增加 或者 好友增加
 *
 */
@ParentListenerType("数量增加父接口")
public interface IncreaseEventGet: MemberChangesEventGet


/**
 * 与 **减少** 有关的事件，例如 群友减少 或者 好友减少
 *
 */
@ParentListenerType("数量减少父接口")
public interface ReduceEventGet: MemberChangesEventGet


/**
 * 与 **请求** 相关的父接口
 */
@ParentListenerType("请求相关事件父接口")
public interface RequestGet: MsgGet, FlagContainer<RequestGet.RequestFlagContent> {

    /**
     * 获取一个请求类型的标识
     */
    override val flag: Flag<RequestFlagContent>

    /**
     * 在请求类型下的 [标识主体][FlagContent] 类型
     *
     * 对于子接口的实现与命名，作为内部接口并不需要前缀
     *
     */
    interface RequestFlagContent : FlagContent
}





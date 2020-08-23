/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     baseMessages.kt
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

package love.forte.simbot.common.api.message


/**
 * 监听消息的父接口。
 *
 * 所有的监听消息都应当实现的容器：[OriginalDataContainer], [BotCodeContainer], [AccountContainer]
 * @since 2.0
 */
interface MsgGet: OriginalDataContainer, BotCodeContainer, AccountContainer {
    /** 当前监听事件消息的ID。一般情况下应当是一个唯一ID。 */
    val id: String

    /** 监听消息的消息正文文本 */
    var msg: String?

    /** 消息接收到的时间。一般是一个时间戳。 */
    val time: Long
}


//region 消息类型事件

/**
 * 与消息有关的事件
 */
interface MessageEventGet: MsgGet, FlagContainer

/**
 * 私聊消息
 */
interface PrivateMsg: MessageEventGet {
    /**
     * 获取私聊消息类型
     */
    val type: Type

    /**
     * flag标识，一般可用于撤回之类的。默认为ID的值。
     */
    @JvmDefault
    override val flag: String get() = id

    /**
     * 私聊消息类型
     */
    enum class Type {
        /** 好友消息 */
        FRIEND,
        /** 来自群的临时会话 */
        GROUP_TEMP,
        /** 其他 */
        OTHER
    }


}

/**
 * 群消息
 */
interface GroupMsg: MessageEventGet {
    /**
     * 获取消息类型
     */
    val type: Type

    /**
     * flag标识，一般可用于撤回之类的。默认为ID的值。
     */
    @JvmDefault
    override val flag: String get() = id


    /** 群消息类型 */
    enum class Type {
        /** 普通消息 */
        NORMAL,
        /** 匿名消息 */
        ANON,
        /** 系统消息 */
        SYS
    }
}

/**
 * 与消息撤回有关的事件
 */
interface MessageDeleteEventGet // TODO


//endregion


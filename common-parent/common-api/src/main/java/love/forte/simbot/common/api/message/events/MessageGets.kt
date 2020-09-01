/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     MessageGets.kt
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

import love.forte.simbot.common.api.message.assists.Flag
import love.forte.simbot.common.api.message.assists.FlagImpl
import love.forte.simbot.common.api.message.containers.GroupContainer
import love.forte.simbot.common.api.message.containers.PermissionContainer

/*
 *
 * 此处定义与 接收消息 相关的接口
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/1
 * @since
 **/


/**
 * 私聊消息. 实现[MessageEventGet]
 */
public interface PrivateMsg : MessageEventGet {
    /**
     * 获取私聊消息类型
     */
    val privateMsgType: Type

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

    /**
     * 私聊消息标识
     *
     * 一般可用于撤回之类的。默认为ID的值。
     */
    @JvmDefault
    override val flag: Flag<FlagContent>
        get() = FlagImpl(PrivateMsgIdFlagContent(id))


    /**
     * 对于 [PrivateMsg] 的 [标识主体][FlagContent] 接口
     */
    public interface FlagContent : MessageEventGet.MessageFlagContent
}

/**
 * 针对于 [PrivateMsg] 的 [标识主体][PrivateMsg.FlagContent] 的实现
 * 基于 id 作为标识主体
 * @property id String
 * @constructor
 */
internal data class PrivateMsgIdFlagContent(override val id: String) : PrivateMsg.FlagContent



/**
 * 群消息， 需要实现 [MessageEventGet] , [GroupContainer] , [PermissionContainer]
 */
public interface GroupMsg : MessageEventGet, GroupContainer, PermissionContainer {
    /**
     * 获取群消息类型
     */
    val groupMsgType: Type

    /** 群消息类型 */
    enum class Type {
        /** 普通消息 */
        NORMAL,

        /** 匿名消息 */
        ANON,

        /** 系统消息 */
        SYS
    }


    /**
     *
     * 群聊消息的标识
     *
     * 一般可用于撤回之类的。默认为ID的值。
     */
    @JvmDefault
    override val flag: Flag<FlagContent>
        get() = FlagImpl(GroupMsgIdFlagContent(id))


    /**
     * 对于 [GroupMsg] 的 [标识主体][FlagContent] 接口
     */
    public interface FlagContent : MessageEventGet.MessageFlagContent

}


/**
 * 针对 [GroupMsg.FlagContent] 的默认实现，
 * 基于 [GroupMsg] 的 id 作为Flag的标识主体
 * @property id String
 * @constructor
 */
internal data class GroupMsgIdFlagContent(override val id: String) : GroupMsg.FlagContent


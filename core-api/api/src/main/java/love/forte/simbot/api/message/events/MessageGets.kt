/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot
 * File     MiraiAvatar.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 */
@file:JvmName("MsgGets")
@file:JvmMultifileClass
package love.forte.simbot.api.message.events

import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.*

/*
 *
 * 此处定义与 接收消息 相关的接口
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/1
 * @since
 **/


/**
 * 私聊消息。
 */
public interface PrivateMsg : MessageGet {
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

        /** 系统消息 */
        SYS,

        /**
         * 自己
         */
        SELF,

        /** 其他 */
        OTHER

    }

    /**
     * 私聊消息标识
     *
     * 一般可用于撤回之类的。默认为ID的值。
     *
     * 如果只是使用 [id] 作为flag载体，在实现的时候可以参考 [PrivateMsgIdFlagContent]
     *
     */

    override val flag: MessageGet.MessageFlag<@JvmWildcard FlagContent>


    /**
     * 对于 [PrivateMsg] 的 [标识主体][FlagContent] 接口。
     */
    public interface FlagContent : MessageGet.MessageFlagContent {
        companion object {
            @JvmStatic
            fun byId(id: String): FlagContent = PrivateMsgIdFlagContent(id)
        }
    }

    // /**
    //  * 私聊消息标识。
    //  */
    // public interface MessageFlag : MessageGet.MessageFlag<FlagContent>

}


/**
 * 一个 **好友消息**。此事件类型继承自 [PrivateMsg], 并代表此事件的消息来源能够确定为一个**好友**。
 * [privateMsgType] 应该固定为 [PrivateMsg.Type.FRIEND]。
 */
public interface FriendMsg : PrivateMsg {
    /**
     * 类型，固定为[PrivateMsg.Type.FRIEND]。
     */
    // @JvmDefault
    override val privateMsgType: PrivateMsg.Type
        get() = PrivateMsg.Type.FRIEND

    /**
     * 好友账号信息。
     */
    override val accountInfo: FriendAccountInfo
}


/**
 * 针对于 [PrivateMsg] 的 [标识主体][PrivateMsg.FlagContent] 的实现
 * 基于 id 作为标识主体
 * @property id String
 * @constructor
 */
public data class PrivateMsgIdFlagContent(override val id: String) : PrivateMsg.FlagContent



/**
 * 群消息.
 * @see MessageGet
 * @see GroupContainer
 * @see PermissionContainer
 * @see GroupAccountContainer
 */
public interface GroupMsg : MessageGet, GroupContainer, GroupAccountContainer, PermissionContainer {

    /** 发消息的人在群里的权限。 */
    override val permission: Permissions

    /**
     * bot在群里的信息。
     */
    override val botInfo: GroupBotInfo

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
     * 一般可用于撤回之类的。
     *
     * 如果只是使用 [id] 作为flag载体，在实现的时候可以参考 [GroupMsgIdFlagContent]
     */
    override val flag: MessageGet.MessageFlag<@JvmWildcard FlagContent>


    /**
     * 对于 [GroupMsg] 的 [标识主体][FlagContent] 接口
     */
    public interface FlagContent : MessageGet.MessageFlagContent {
        companion object {
            @JvmStatic
            fun byId(id: String): FlagContent = GroupMsgIdFlagContent(id)
        }
    }

    //
    // /**
    //  * 群消息标识。
    //  */
    // public interface MessageFlag : MessageGet.MessageFlag<FlagContent>


}

/**
 * 基于ID作为标识主体的 [GroupMsg.FlagContent] 实现
 * @property id String
 * @constructor
 */
public data class GroupMsgIdFlagContent(override val id: String): GroupMsg.FlagContent





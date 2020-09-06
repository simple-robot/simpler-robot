/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     RequestGets.kt
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

package love.forte.simbot.common.api.messages.events

import love.forte.simbot.common.annotations.MainListenerType
import love.forte.simbot.common.api.messages.assists.ActionMotivations
import love.forte.simbot.common.api.messages.assists.Flag
import love.forte.simbot.common.api.messages.containers.ActionMotivationContainer

/*
 * 此模块下定义请求相关的监听接口
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */


/**
 * 好友请求事件。 此时申请人尚未成为好友。
 */
@MainListenerType("好友请求")
public interface FriendAddRequest : RequestGet {

    /**
     * 获取请求标识。
     *
     * @see FriendAddRequestIdFlagContent
     */
    override val flag: Flag<FlagContent>

    /**
     * 请求类型的 标识主体
     */
    public interface FlagContent : RequestGet.RequestFlagContent
}

/**
 * 使用 [id] 作为标识载体的 [标识主体][FriendAddRequest.FlagContent] 实现。
 */
public data class FriendAddRequestIdFlagContent(override val id: String) : FriendAddRequest.FlagContent


/**
 * 群添加请求。此时申请人尚未入群。
 */
public interface GroupAddRequest : RequestGet {

    /**
     * 当前请求的邀请者。在 **组件不支持** 、**请求非邀请** 等情况下可能为null
     */
    val invitor: GroupAddRequestInvitor?

    /**
     * 申请的类型
     */
    val requestType: Type

    /**
     * 获取请求标识。
     *
     * @see GroupAddRequestIdFlagContent
     */
    override val flag: Flag<FlagContent>


    /**
     * [群添加请求][GroupAddRequest] 的 [请求标识主体][RequestGet.RequestFlagContent]
     */
    public interface FlagContent : RequestGet.RequestFlagContent


    /**
     * 群添加请求的类型。
     */
    public enum class Type(override val actionMotivations: ActionMotivations): ActionMotivationContainer {
        /**
         * 被动的，一般是指被他人邀请进入的
         */
        PASSIVE(ActionMotivations.PASSIVE),

        /** 主动的, 一般指代入群者为主动申请加入群聊的 */
        PROACTIVE(ActionMotivations.PROACTIVE)
    }
}

/**
 * [群添加请求][GroupAddRequest] 中的邀请者信息。
 *
 */
public interface GroupAddRequestInvitor {
    /** 邀请者账号。一般情况下，当前实例存在此参数便不可为null */
    val invitorCode: String
    /** 邀请者昵称。可能无法获取 */
    val invitorNickname: String?
    /** 邀请者备注 可能无法获取*/
    val invitorRemark: String?
    /** 备注或昵称 */
    @JvmDefault val invitorRemarkOrNickname: String? get() = invitorRemark ?: invitorNickname
}


/**
 * 使用 [id] 作为标识载体的 [GroupAddRequest.FlagContent] 实现
 */
public data class GroupAddRequestIdFlagContent(override val id: String) : GroupAddRequest.FlagContent







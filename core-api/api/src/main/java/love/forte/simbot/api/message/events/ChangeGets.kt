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
@file:JvmName("MsgGets")
@file:JvmMultifileClass
package love.forte.simbot.api.message.events

import love.forte.simbot.annotation.MainListenerType
import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.*

/*
 * 此模块定义与 变更 有关的事件接口
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */


/**
 * 群成员权限变动事件。
 * 一般来讲，权限变动无非就是 **成为管理** 或者 **被取消管理**
 */
@MainListenerType("群成员权限变更事件")
public interface GroupMemberPermissionChanged :
    ChangedGet<Permissions>,
    GroupContainer,
    OperatingContainer {
    /**
     * [被操作者][beOperatorInfo] 默认实现为 [当前主体账户][accountInfo]
     */
    override val beOperatorInfo: BeOperatorInfo
        get() = accountInfo.asBeOperator()

    /**
     * 此次变动是否为 **得到管理权限**
     */
    /* @JvmDefault */val isGetManagementRights: Boolean get() = afterChange?.isOwnerOrAdmin() == true


    /**
     * 此次变动是否为 **失去管理权限**
     */
    /* @JvmDefault */val isLostManagementRights: Boolean get() = afterChange?.isMember() == true
}






/**
 * 群名称变动事件。
 */
@MainListenerType("群名称变更事件")
public interface GroupNameChanged :
    ChangedGet<String>,
    GroupContainer,
    OperatorContainer {
    /** 当前事件的bot的信息。 */
    override val accountInfo: AccountInfo

    /** 操作者信息。获取不到则为null。 */
    override val operatorInfo: OperatorInfo?
}


/**
 * 群友群名片变动事件。
 */
@MainListenerType("群友群名片变动事件")
public interface GroupMemberRemarkChanged :
    ChangedGet<String>,
    GroupContainer,
    OperatingContainer {
    /**
     * [被操作者][beOperatorInfo] 默认实现为 [当前主体账户][accountInfo]
     */
    override val beOperatorInfo: BeOperatorInfo
        get() = accountInfo.asBeOperator()
}


/**
 * 群友头衔变动事件。
 */
@MainListenerType("群友头衔变动事件")
public interface GroupMemberSpecialChanged :
    ChangedGet<String>,
    GroupContainer,
    OperatingContainer {

    /**
     * [被操作者][beOperatorInfo] 默认实现为 [当前主体账户][accountInfo]
     */
    override val beOperatorInfo: BeOperatorInfo
        get() = accountInfo.asBeOperator()
}


/**
 * 好友昵称变动事件
 */
@MainListenerType("好友昵称变动事件")
public interface FriendNicknameChanged : ChangedGet<String>


/**
 * 好友头像变动事件
 */
@MainListenerType("好友头像变动事件")
public interface FriendAvatarChanged : ChangedGet<String>

















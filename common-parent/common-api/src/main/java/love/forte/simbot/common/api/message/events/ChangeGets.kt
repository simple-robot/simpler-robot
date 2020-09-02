/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     ChangeGets.kt
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

import love.forte.simbot.common.annotations.MainListenerType
import love.forte.simbot.common.api.message.assists.Permissions
import love.forte.simbot.common.api.message.containers.*

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
    @JvmDefault
    override val beOperatorInfo: BeOperatorInfoContainer
        get() = AccountAsBeOperator(accountInfo)

    // 得到管理权限
    // 失去管理权限

    /**
     * 是否为 **得到管理权限**
     */
    @JvmDefault val isGetManagementRights: Boolean get() = afterChange.isOwnerOrAdmin()

    /**
     * 是否为 **失去管理权限**
     */
    @JvmDefault val isLostManagementRights: Boolean get() = afterChange.isMember()




}

















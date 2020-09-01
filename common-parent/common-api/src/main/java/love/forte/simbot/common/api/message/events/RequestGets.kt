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

package love.forte.simbot.common.api.message.events

import love.forte.simbot.common.annotations.MainListenerType
import love.forte.simbot.common.api.message.assists.Flag

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
 * 使用 id 作为标识载体的 [FriendAddRequest.FlagContent] 实现
 */
public data class FriendAddRequestIdFlagContent(override val id: String) : FriendAddRequest.FlagContent


/**
 * 群添加请求。此时申请人尚未入群。
 */
public interface GroupAddRequest : RequestGet {


    /**
     * 获取请求标识。
     *
     * @see GroupAddRequestIdFlagContent
     */
    override val flag: Flag<FlagContent>

    /**
     * 请求类型的 标识主体
     */
    public interface FlagContent : RequestGet.RequestFlagContent
}


/**
 * 使用 id 作为标识载体的 [GroupAddRequest.FlagContent] 实现
 */
public data class GroupAddRequestIdFlagContent(override val id: String) : GroupAddRequest.FlagContent







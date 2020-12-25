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
@file:JvmName("MsgGets")
@file:JvmMultifileClass
package love.forte.simbot.api.message.events

import love.forte.simbot.api.message.assists.ActionMotivations
import love.forte.simbot.api.message.containers.ActionMotivationContainer
import love.forte.simbot.api.message.containers.GroupContainer

/*
 * 消息撤回相关子接口
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/1
 * @since
 */

/**
 * 私聊消息撤回。
 */
public interface PrivateMsgRecall: MessageRecallEventGet



/**
 * 群聊消息撤回。
 */
public interface GroupMsgRecall: MessageRecallEventGet, GroupContainer {

    /** 群消息撤回类型。 */
    val groupRecallType: Type

    /**
     * 群聊撤回的类型。
     */
    public enum class Type(override val actionMotivations: ActionMotivations): ActionMotivationContainer {
        /** 主动的, 一般代表消息是由发送人主动撤回的 */
        PROACTIVE(ActionMotivations.PROACTIVE),
        /** 被动的, 一般代表消息是被管理员等撤回的 */
        PASSIVE(ActionMotivations.PASSIVE)
    }
}

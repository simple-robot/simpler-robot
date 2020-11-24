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

package love.forte.simbot.component.lovelycat.message

import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.PrivateMsg


public object EmptyLovelyCatPrivateMsgFlagContent : PrivateMsg.FlagContent {
    override val id: String get() = ""
}


public object EmptyLovelyCatGroupMsgFlag : Flag<GroupMsg.FlagContent> {
    override val flag: GroupMsg.FlagContent
        get() = EmptyLovelyCatGroupMsgFlagContent
}
public object EmptyLovelyCatGroupMsgFlagContent : GroupMsg.FlagContent {
    override val id: String get() = ""
}

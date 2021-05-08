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

import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.events.PrivateMsg



public object EmptyLovelyCatGroupMsgFlag : MessageGet.MessageFlag<GroupMsg.FlagContent> {
    override val flag: GroupMsg.FlagContent
        get() = EmptyLovelyCatFlagContent
}

public object EmptyLovelyCatPrivateMsgFlag : MessageGet.MessageFlag<PrivateMsg.FlagContent> {
    override val flag: PrivateMsg.FlagContent
        get() = EmptyLovelyCatFlagContent
}


public object EmptyLovelyCatFlagContent : GroupMsg.FlagContent, PrivateMsg.FlagContent {
    override val id: String get() = ""
}

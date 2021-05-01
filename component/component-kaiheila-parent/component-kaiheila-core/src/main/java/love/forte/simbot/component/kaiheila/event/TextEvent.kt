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

package love.forte.simbot.component.kaiheila.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.`object`.Role
import love.forte.simbot.component.kaiheila.`object`.User


/**
 * [文字消息](https://developer.kaiheila.cn/doc/event/message#%E6%96%87%E5%AD%97%E6%B6%88%E6%81%AF)
 *
 */
public interface TextEvent : Event<TextEvent.Extra, Int> {


    /**
     * [TextEvent.extra] value.
     */
    @Serializable
    public data class Extra(
        override val type: Int,
        @SerialName("guild_id")
        override val guildId: String,
        @SerialName("channel_name")
        override val channelName: String,
        override val mention: List<String>,
        @SerialName("mention_all")
        override val mentionAll: Boolean,
        @SerialName("mention_roles")
        override val mentionRoles: List<Role>,
        @SerialName("mention_here")
        override val mentionHere: Boolean,
        override val author: User,
    ) : TextExtra
}


/*
    Extra:
        {
            "type": 1,
            "guild_id": "xxxxx",
            "channel_name": "文字频道",
            "mention": [],
            "mention_all": false,
            "mention_roles": [],
            "mention_here": false,
            "code": "",
            "author": {
                "online": true,
                "status": 1,
                "bot": false,
                "mobile_verified": true,
                "system": false,
                "mobile_prefix": "+86",
                "mobile": "1145141919",
                "invited_count": 0,
                "identify_num": "xxxxx",
                "avatar": "https://img.kaiheila.cn/avatars/2020-11/r0j9.jpg/icon",
                "username": "xxxxx",
                "id": "xxxxx",
                "nickname": "xxxxx",
                "roles": []
            }
        }
 */

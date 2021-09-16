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

package love.forte.simbot.component.kaiheila.event.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.objects.Role
import love.forte.simbot.component.kaiheila.objects.User


/**
 * [文字消息事件](https://developer.kaiheila.cn/doc/event/message#%E6%96%87%E5%AD%97%E6%B6%88%E6%81%AF)
 * @author ForteScarlet
 */
@Serializable
public data class TextEventExtra(
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
) : MessageEventExtra

/*
{
    "s": 0,
    "d": {
        "channel_type": "GROUP",
        "type": 1,
        "target_id": "xxxxxx",
        "author_id": "xxxxx",
        "content": "dddd",
        "msg_id": "67637d4c-xxxx-xxxx-xxxx-xxxxx",
        "msg_timestamp": 1607674740160,
        "nonce": "",
        "extra": {
            "type": 1,
            "guild_id": "xxxxx",
            "channel_name": "文字频道",
            "mention": [],
            "mention_all": false,
            "mention_roles": [],
            "mention_here": false,
            "code": "",
            "author": {
                "identify_num": "xxxxx",
                "avatar": "https://img.kaiheila.cn/avatars/2020-11/r0j9.jpg/icon",
                "username": "xxxxx",
                "id": "xxxxx",
                "nickname": "xxxxx",
                "roles": []
            }
        }
    },
    "sn": 2199
}
 */
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
import love.forte.simbot.component.kaiheila.objects.Attachments
import love.forte.simbot.component.kaiheila.objects.Role
import love.forte.simbot.component.kaiheila.objects.User

/**
 *
 * [视频消息事件](https://developer.kaiheila.cn/doc/event/message#%E8%A7%86%E9%A2%91%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
public data class VideoEventExtra(
    override val type: Int,

    /**
     * Unknown field.
     */
    val code: String,
    @SerialName("guild_id")
    override val guildId: String,
    /**
     * 附件
     */
    val attachments: Attachments,
    override val author: User,
) : MessageEventExtra {
    override val channelName: String get() = ""
    override val mention: List<String> get() = emptyList()
    override val mentionAll: Boolean get() = false
    override val mentionRoles: List<Role> get() = emptyList()
    override val mentionHere: Boolean get() = false
}

/*
{
    "s": 0,
    "d": {
        "channel_type": "GROUP",
        "type": 3,
        "target_id": "xxxxx",
        "author_id": "xxxxx",
        "content": "https://img.kaiheila.cn/attachments/2020-12/11/asd.mp4",
        "msg_id": "67637d4c-xxxx-xxxx-xxxx-xxxxx",
        "msg_timestamp": 1607679613599,
        "nonce": "",
        "extra": {
            "type": 3,
            "guild_id": "xxxx",
            "code": "",
            "attachments": {
                "type": "video",
                "url": "https://img.kaiheila.cn/attachments/2020-12/11/asd.mp4",
                "name": "002iQMhagx07Fx0S41200323o0E010.mp4",
                "file_type": "video/mp4",
                "size": 722882,
                "duration": 15.673,
                "width": 360,
                "height": 635
            },
            "author": {
                "identify_num": "xxxxx",
                "avatar": "https://img.kaiheila.cn/avatars/2020-11/r20f20j9.jpg/icon",
                "username": "xxxxx",
                "id": "xxxxx",
                "nickname": "xxxxx",
                "roles": []
            }
        }
    },
    "sn": 2582
}
 */
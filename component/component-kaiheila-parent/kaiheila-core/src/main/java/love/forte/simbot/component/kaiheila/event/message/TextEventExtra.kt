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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.component.kaiheila.event.Event
import love.forte.simbot.component.kaiheila.event.EventLocatorRegistrarCoordinate
import love.forte.simbot.component.kaiheila.objects.Role
import love.forte.simbot.component.kaiheila.objects.User


/**
 * [文字消息事件](https://developer.kaiheila.cn/doc/event/message#%E6%96%87%E5%AD%97%E6%B6%88%E6%81%AF)
 * @author ForteScarlet
 */
@Serializable
public data class TextEventExtra(
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
) : MessageEventExtra {
    override val type: Int get() = Event.Type.TEXT.type
}


@Serializable
public data class TextEvent(
    @SerialName("channel_type")
    override val channelType: String,
    @SerialName("target_id")
    override val targetId: String,
    @SerialName("author_id")
    override val authorId: String,
    override val content: String,
    @SerialName("msg_id")
    override val msgId: String,
    @SerialName("msg_timestamp")
    override val msgTimestamp: Long,
    override val nonce: String,
    override val extra: TextEventExtra,
) : MessageEvent<TextEventExtra> {
    override val type: Event.Type get() = Event.Type.TEXT

    override val originalData: String
        get() = toString()

    override val msgContent: MessageContent
        get() = TODO("Not yet implemented")
    override val flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent>
        get() = TODO("Not yet implemented")


    companion object : EventLocatorRegistrarCoordinate<TextEvent> {
        override val type: Event.Type get() = Event.Type.TEXT

        override val extraType: String
            get() = type.type.toString()

        override fun coordinateSerializer(): KSerializer<TextEvent> = serializer()
    }
}

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
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

import catcode.Neko
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.FlagContent
import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.assists.flag
import love.forte.simbot.api.message.containers.GroupAccountInfo
import love.forte.simbot.api.message.containers.GroupBotInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.events.*
import love.forte.simbot.component.kaiheila.event.Event
import love.forte.simbot.component.kaiheila.event.EventLocatorRegistrarCoordinate
import love.forte.simbot.component.kaiheila.objects.Channel
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
public sealed class TextEvent : AbstractMessageEvent<TextEventExtra>() {

    /**
     * 群消息.
     */
    @Serializable
    public data class Group(
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
    ) : TextEvent(), GroupMsg {

        override val channelType: Channel.Type
            get() = Channel.Type.GROUP


        override val accountInfo: GroupAccountInfo
            get() = TODO("Not yet implemented")
        override val groupInfo: GroupInfo
            get() = TODO("Not yet implemented")
        override val permission: Permissions
            get() = TODO("Not yet implemented")
        override val botInfo: GroupBotInfo
            get() = TODO("Not yet implemented")

        override val groupMsgType: GroupMsg.Type
            get() = GroupMsg.Type.NORMAL

        override val flag: MessageGet.MessageFlag<GroupMsg.FlagContent> =
            MessageFlag(GroupMsgIdFlagContent(msgId))

        companion object : EventLocatorRegistrarCoordinate<Group> {
            override val type: Event.Type get() = Event.Type.TEXT

            override val extraType: String
                get() = type.type.toString()

            override fun coordinateSerializer(): KSerializer<Group> = serializer()
        }
    }


    /**
     * 私聊消息.
     */
    @Serializable
    public data class Private(
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
    ) : TextEvent(), PrivateMsg {

        override val channelType: Channel.Type
            get() = Channel.Type.PERSON

        override val privateMsgType: PrivateMsg.Type
            get() = PrivateMsg.Type.FRIEND

        override val flag: MessageGet.MessageFlag<PrivateMsg.FlagContent> =
            MessageFlag(PrivateMsgIdFlagContent(msgId))

        companion object : EventLocatorRegistrarCoordinate<Private> {
            override val type: Event.Type get() = Event.Type.TEXT

            override val extraType: String
                get() = type.type.toString()

            override fun coordinateSerializer(): KSerializer<Private> = serializer()
        }
    }


    override val type: Event.Type get() = Event.Type.TEXT

    override val originalData: String
        get() = toString()

    override val msgContent: MessageContent
        get() = TODO("Not yet implemented")

    // override val flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent> =

}

internal class MessageFlag<F : MessageGet.MessageFlagContent>(override val flag: F) : MessageGet.MessageFlag<F>


// TODO
class TextEventMessageContent() : MessageContent {
    override val msg: String
        get() = TODO("Not yet implemented")

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override val cats: List<Neko>
        get() = TODO("Not yet implemented")
}





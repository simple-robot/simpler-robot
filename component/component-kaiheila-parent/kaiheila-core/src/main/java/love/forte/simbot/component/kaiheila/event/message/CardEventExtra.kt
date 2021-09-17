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
import love.forte.simbot.component.kaiheila.objects.KMarkdown
import love.forte.simbot.component.kaiheila.objects.Role
import love.forte.simbot.component.kaiheila.objects.User

/**
 * [Card消息事件](https://developer.kaiheila.cn/doc/event/message#Card%E6%B6%88%E6%81%AF)
 *
 * Maybe it looks like [KMarkdownEventExtra].
 *
 * @author ForteScarlet
 */
public data class CardEventExtra(
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

    // nav_channels: Array

    val code: String = "",

    override val author: User,
    val kmarkdown: KMarkdown,
) : MessageEventExtra

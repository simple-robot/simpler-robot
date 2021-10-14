package love.forte.simbot.kaiheila.event.system.channel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *
 * 自己加入服务器
 *
 * `self_joined_guild`
 *
 * @author ForteScarlet
 */
@Serializable
public data class SelfJoinedGuildExtraBody(
    @SerialName("guild_id")
    val guildId: String,
) : ChannelEventExtraBody
package love.forte.simbot.kaiheila.event.system.guild.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 *
 * @author ForteScarlet
 */
@Serializable
public data class GuildMemberOfflineEventBody(
    /**
     *用户id
     */
    @SerialName("user_id")
    val userId: String,
    /**
     *用户下线时间(毫秒)
     */
    @SerialName("event_time")
    val eventTime: Long,
    /**
     *服务器id组成的数组, 代表与该用户所在的共同的服务器
     */
    val guilds: List<String>,
) : GuildMemberEventExtraBody


/**
 *
 *
 * `guild_member_offline`
 *
 * @author ForteScarlet
 */
@Serializable
public data class GuildMemberOfflineEventExtra(override val body: GuildMemberOfflineEventBody) :
    GuildMemberEventExtra<GuildMemberOfflineEventBody> {
    override val type: String
        get() = "guild_member_offline"
}
package love.forte.simbot.component.kaiheila.event.system.guild.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 *
 * @author ForteScarlet
 */
 @Serializable
public data class GuildMemberOnlineEventBody(
    /**
     * userId
     */
    @SerialName("user_id")
    val userId: String,
    /**
     * 上线时间（ms）
     */
    @SerialName("event_time")
    val eventTime: Long,
    /**
     *服务器id组成的数组, 代表与该用户所在的共同的服务器
     */
    val guilds: List<String>, // ["601638990000000"]
) : GuildMemberEventExtraBody


/**
 *
 *
 * `guild_member_online`
 *
 * @author ForteScarlet
 */
 @Serializable
public data class GuildMemberOnlineEventExtra(override val body: GuildMemberOnlineEventBody) : 
    GuildMemberEventExtra<GuildMemberOnlineEventBody> { 
    override val type: String
        get() = "guild_member_online"
}
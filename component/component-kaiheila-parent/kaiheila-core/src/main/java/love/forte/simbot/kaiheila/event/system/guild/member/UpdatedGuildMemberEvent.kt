package love.forte.simbot.kaiheila.event.system.guild.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 *
 * @author ForteScarlet
 */
 @Serializable
public data class UpdatedGuildMemberEventBody(
    /**
     * User ID
     */
    @SerialName("user_id")
    val userId: String,
    /**
     * 昵称
     */
    val nickname: String,
) : GuildMemberEventExtraBody


/**
 *
 *
 * `updated_guild_member`
 *
 * @author ForteScarlet
 */
 @Serializable
public data class UpdatedGuildMemberEventExtra(override val body: UpdatedGuildMemberEventBody) : 
    GuildMemberEventExtra<UpdatedGuildMemberEventBody> { 
    override val type: String
        get() = "updated_guild_member"
}
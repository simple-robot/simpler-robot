package love.forte.simbot.component.kaiheila.event.system.guild.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
public data class JoinedGuildEventBody(
    @SerialName("user_id")
    val userId: String,
    @SerialName("joined_at")
    val joinedAt: Long,
) : GuildMemberEventExtraBody


/**
 * 新成员加入服务器
 *
 * `joined_guild`
 * @author ForteScarlet
 */
@Serializable
public data class JoinedGuildEventExtra(override val body: JoinedGuildEventBody) :
    GuildMemberEventExtra<JoinedGuildEventBody> {
    override val type: String
        get() = "joined_guild"
}



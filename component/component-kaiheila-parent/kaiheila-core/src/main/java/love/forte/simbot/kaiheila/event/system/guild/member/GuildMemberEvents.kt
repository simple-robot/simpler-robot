package love.forte.simbot.kaiheila.event.system.guild.member

import love.forte.simbot.kaiheila.event.system.guild.GuildEventExtra
import love.forte.simbot.kaiheila.event.system.guild.GuildEventExtraBody


/**
 *
 * 服务器成员相关事件 extra 的 body。
 *
 * @author ForteScarlet
 */
public interface GuildMemberEventExtraBody : GuildEventExtraBody



public interface GuildMemberEventExtra<B : GuildMemberEventExtraBody> : GuildEventExtra<B>



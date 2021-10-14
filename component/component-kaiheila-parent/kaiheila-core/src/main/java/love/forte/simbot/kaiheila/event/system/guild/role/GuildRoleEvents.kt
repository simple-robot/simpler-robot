package love.forte.simbot.kaiheila.event.system.guild.role

import love.forte.simbot.kaiheila.event.system.guild.GuildEventExtra
import love.forte.simbot.kaiheila.event.system.guild.GuildEventExtraBody


/**
 *
 * 服务器角色相关事件 extra 的 body。
 *
 * @author ForteScarlet
 */
public interface GuildRoleEventExtraBody : GuildEventExtraBody



public interface GuildRoleEventExtra<B : GuildRoleEventExtraBody> : GuildEventExtra<B>



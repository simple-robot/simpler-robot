/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.definition

import love.forte.simbot.Bot
import love.forte.simbot.ID


/**
 * 一个 [Bot] 在一个 [组织][Organization] 中所扮演的 [成员][Member] 对象。
 *
 * [BotMember] 同时实现 [Bot] 和 [Member], 通过 [Organization.bot] 获取并代表此bot在目标组织中的成员身份。
 *
 * @author ForteScarlet
 */
public interface BotMember : Bot, Member {
    /**
     * 唯一标识。
     */
    override val id: ID
    
    /**
     * [BotMember] 中包含的真正bot实例。
     */
    override val bot: Bot
    
    /**
     * 当前bot的用户名。
     */
    override val username: String
    
    /**
     * 当前bot在当前组织中的昵称。假如未设置则可能为空。
     */
    override val nickname: String
}


/**
 * 一个 [Bot] 在一个 [群][Group] 中所扮演的 [成员][Member] 对象。
 *
 * [BotGroupMember] 同时实现 [BotMember] 和 [GroupMember], 通过 [Group.bot] 获取并代表此bot在目标群中的成员身份。
 *
 * @see GroupMember
 * @author ForteScarlet
 */
public interface BotGroupMember : BotMember, GroupMember


/**
 * 一个 [Bot] 在一个 [频道服务器][Guild] 中所扮演的 [成员][Member] 对象。
 *
 * [BotGroupMember] 同时实现 [BotMember] 和 [GuildMember], 通过 [Guild.bot] 获取并代表此bot在目标频道服务器中的成员身份。
 *
 * @see GuildMember
 * @author ForteScarlet
 */
public interface BotGuildMember : BotMember, GuildMember
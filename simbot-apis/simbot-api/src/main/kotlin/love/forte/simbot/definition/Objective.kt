/*
 * Copyright (c) 2021-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simbot.definition

import love.forte.simbot.ID
import love.forte.simbot.action.SendSupport
import love.forte.simbot.bot.Bot


/**
 * [Objective] 是对与 [Bot] 相关联的对象 （一个[组织][Organization]或一个具体的[用户][User]） 的统称。
 *
 * 不论 [组织][Organization] 还是 [用户][User]，它们均来自一个 [Bot].
 *
 * [Objective] 本身仅代表这个对象的概念，不能保证其本身拥有 [发送消息][SendSupport] 的能力。
 *
 *
 *
 * @author ForteScarlet
 */
public sealed interface Objective : BotContainer, IDContainer {
    
    /**
     * 当前对象对应的唯一ID。
     *
     * @see ID
     */
    override val id: ID
    
    /**
     * 当前 [Objective] 来自的bot。
     */
    override val bot: Bot
}

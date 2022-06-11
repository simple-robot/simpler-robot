/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

@file:JvmName("AccountUtil")

package love.forte.simbot.definition

import love.forte.simbot.Bot
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID

/**
 * 一个 **用户**。
 *
 * [Bot] 也是 [用户][User].
 *
 * 对于Bot来讲，一个用户可能是一个陌生的人，一个[群成员][Member], 或者一个好友。
 *
 * 当然，[User] 也有可能代表了 [love.forte.simbot.Bot] 自身.
 *
 * @author ForteScarlet
 */
public interface User : Objective, UserInfo {

    /**
     * 这个账号的唯一ID。
     */
    override val id: ID
    override val bot: Bot

    //region from user info
    override val username: String
    override val avatar: String
    //endregion

    /**
     * 这个用户的状态属性。
     *
     * **ExperimentalSimbotApi: see [UserStatus]**
     */
    @ExperimentalSimbotApi
    public val status: UserStatus

}

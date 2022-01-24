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
 *
 */

package love.forte.simbot.action

import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInBlocking


/**
 * 允许一种删除行为。
 * 标记一个消息为可删除的，通常可理解为是可撤回的。
 *
 * 一般用于消息回执或者从远端接收到的消息事件上。
 * @author ForteScarlet
 */
public interface DeleteSupport {

    /**
     * 删除当前目标。
     *
     * @return 是否删除成功，不代表会捕获异常。
     */
    @JvmSynthetic
    public suspend fun delete(): Boolean

    @Api4J
    public fun deleteBlocking(): Boolean = runInBlocking { delete() }
}

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

package love.forte.simbot.definition

import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.utils.runInBlocking

/**
 * 一个非阻塞的 **结构化** 定义。
 *
 * 结构化的东西，可以有一个 [上级][previous]，以及多个 [下级][children]。
 *
 * @author ForteScarlet
 */
public interface Structured<P, N> {

    /**
     * 上一级的内容。
     */
    @JvmSynthetic
    public suspend fun previous(): P

    @Api4J
    public val previous: P
        get() = runInBlocking { previous() }

    /**
     * 下一级的内容。
     *
     * 结构化的东西下，其下层 *可能* 需要一个分组信息(分组ID)来得到特定的内容。
     *
     */
    @JvmSynthetic
    public suspend fun children(groupingId: ID? = null): N
}

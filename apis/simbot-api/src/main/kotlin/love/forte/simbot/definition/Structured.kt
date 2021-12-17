/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.definition

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID

/**
 * 一个非阻塞的 **结构化** 定义。
 *
 * 结构化的东西，他可以有一个 [上级][previous]，以及一个 [下级][children]。
 *
 * 一个结构化的内容，它可能存在一个上级，以及多个下级。
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
    public fun getPrevious(): P = runBlocking { previous() }

    /**
     * 下一级的内容。
     *
     * 结构化的东西下，其下层**可能**需要一个分组信息(分组ID)来得到特定的内容。
     *
     */
    @JvmSynthetic
    public suspend fun children(groupingId: ID? = null): N
}

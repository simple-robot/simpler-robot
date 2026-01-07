/*
 *     Copyright (c) 2025-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.component.kook

import love.forte.simbot.kook.api.ApiResponseException
import love.forte.simbot.kook.api.channel.UpdateChannelApi
import love.forte.simbot.suspendrunner.ST

/**
 * [KookChannel] 的更新器。
 * @since 4.3.0
 */
public interface KookChannelUpdater {
    public val channel: KookChannel

    /**
     * 用于构建 [UpdateChannelApi] 的构建器。在 [execute] 中构建并执行。
     */
    public var builder: UpdateChannelApi.Builder

    /**
     * 使用当前的 [builder] 在DSL中配置更新信息。
     */
    public fun applyBuilder(block: UpdateChannelApi.Builder.() -> UpdateChannelApi.Builder): KookChannelUpdater =
        apply {
            builder = builder.block()
        }

    /**
     * 根据当前最终的 [builder] 信息，提供给 [UpdateChannelApi] 并执行更新。
     *
     * @see UpdateChannelApi
     *
     * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
     * @throws love.forte.simbot.kook.api.ApiResultException 请求结果的 `code` 校验失败
     */
    @ST
    public suspend fun execute(): KookChannel
}

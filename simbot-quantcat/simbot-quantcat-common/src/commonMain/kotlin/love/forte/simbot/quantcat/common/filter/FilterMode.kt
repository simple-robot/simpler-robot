/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.quantcat.common.filter
/**
 * [Filter] 标记的结果最终产生的“过滤器”的模式。
 *
 */
public enum class FilterMode {
    /**
     * 将 `Filter` 中的逻辑作为 [EventInterceptor][love.forte.simbot.event.EventInterceptor] 注册。
     * 可以通过优先级的控制来使其与其他全局拦截器之间的关系。
     *
     */
    INTERCEPTOR,

    /**
     * 作为一段逻辑注入到事件处理器的前置中。
     * 由于最终执行逻辑是与事件处理器的逻辑融为一体的，
     * 所以使用此模式时，`Filter` 所产生的逻辑始终会在所有拦截器之后执行。
     */
    IN_LISTENER
}

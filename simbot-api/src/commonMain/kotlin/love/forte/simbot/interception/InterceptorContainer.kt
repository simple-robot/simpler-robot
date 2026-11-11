/*
 *     Copyright (c) 2026. ForteScarlet.
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
package love.forte.simbot.interception

/**
 * 查询和注销已注册拦截器的容器，可通过字符串 ID 重新定位注册对象。
 *
 * 查询是弱一致性的，但序列在实际迭代时会过滤已注销的内容。
 * 但元素在检查后仍可能立即被并发注销，因此返回对象不能保证继续有效，因此如有需要仍需要进行失效检查。
 *
 * @since 5.0
 * @author ForteScarlet
 */
public interface InterceptorContainer {
    /**
     * 返回所有当前可见的有效注册项的序列，会过滤掉已注销的内容。
     * 同一个拦截点的拦截器信息会连续出现，并按优先级升序、同优先级按注册顺序排列。不同拦截点之间的顺序随机。
     */
    public val interceptors: Sequence<InterceptorRegistration<*, *, *>>

    /**
     * 查询 [point] 对应的有效注册项，按优先级升序、同优先级按注册顺序排列。
     *
     * @param point 注册时使用的拦截点。
     * @return 与注册返回值相同的注册对象组成的弱一致性序列。
     */
    public fun <C : Any, R, P : InterceptionPoint<C, R>> interceptors(
        point: P
    ): Sequence<InterceptorRegistration<C, R, P>>

    /**
     * 判断此时 [point] 是否存在可见的有效拦截器信息。
     */
    public fun hasInterceptors(point: InterceptionPoint<*, *>): Boolean

    /**
     * 根据所属容器内的 [id] 查找有效的拦截器注册信息，不存在或已经注销时返回 `null`。
     */
    public fun findById(id: String): InterceptorRegistration<*, *, *>?

    /**
     * 根据所属容器内的 [id] 以及其对应的 [point] 查找有效的拦截器注册信息，不存在或已经注销时返回 `null`。
     * 如果仅仅是 id 相同但 point 不匹配也会返回 `null`。
     */
    public fun <C : Any, R, P : InterceptionPoint<C, R>> findById(
        id: String,
        point: P
    ): InterceptorRegistration<C, R, P>?

    /**
     * 注销 [id] 对应的注册，与注册对象的 [InterceptorRegistration.dispose] 具有相同效果。
     *
     * @return 本次调用首次将有效注册标记为失效时返回 `true`，不存在或已失效时返回 `false`。
     * 同一注册的并发注销只有一个调用能成功完成状态切换。
     */
    public fun disposeById(id: String): Boolean
}

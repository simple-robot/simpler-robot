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
 * 一次拦截器注册，统一作为注册返回值、查询结果和注销句柄。
 *
 * 同一个拦截器多次注册会产生不同的注册对象及 [id]，各自独立。
 *
 * @param C 非空业务上下文类型。
 * @param R 拦截链返回值类型。
 * @param P 拦截点类型。
 * @since 5.0
 */
public interface InterceptorRegistration<C : Any, R, P : InterceptionPoint<C, R>> {
    /**
     * 本次注册的拦截器的唯一标识（通常为 UUID 字符串），在所属容器内唯一且不变。
     */
    public val id: String

    /**
     * 本次注册所属的拦截点。
     */
    public val point: P

    /**
     * 本次注册的优先级。
     */
    public val priority: Int

    /**
     * 受本次注册状态控制的执行视图。不保证与注册时传入的拦截器是同一个对象。
     */
    public val interceptor: Interceptor<C, R, P>

    /**
     * 是否已注销。只会从 `false` 变为 `true`，注销后状态不可恢复。
     */
    public val isDisposed: Boolean

    /**
     * 将本次注册标记为失效，并从所属容器移除，重复调用没有额外效果。
     * 与 [InterceptorContainer.disposeById] 使用同一注销流程。
     * 不打断、也不等待已经进入用户逻辑的调用结束，只会影响迭代时的可见性和调用时的逻辑有效性。
     */
    public fun dispose()
}

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

import love.forte.simbot.common.PriorityConstant

/**
 * 为指定拦截点注册拦截器的注册器。
 *
 * 同一拦截器可以多次注册，每次产生具有不同 ID 的独立注册对象。
 *
 * @since 5.0
 * @author ForteScarlet
 */
public interface InterceptorRegistrar {
    /**
     * 为 [point] 注册一个拦截器，返回可查询其信息及注销本次注册的对象。
     *
     * 同一个点内优先级数值越小越先进入，同优先级按注册顺序进入，正常返回时顺序相反。
     * 注册后的拦截器可能被多个操作并发调用，其实现应自行保证并发安全。
     *
     * 注册过程是弱一致性的。
     *
     * Java 语言中构建 [Interceptor] 实例可以使用 `Interceptors` 提供的静态工厂方法，例如
     * `Interceptors.blocking`、`Interceptors.async`、`Interceptors.reactive`。
     *
     * @param point 关联上下文及返回值类型的拦截点。
     * @param priority 当前点内的执行优先级，默认为 [PriorityConstant.DEFAULT]。
     * @param interceptor 用户提供的拦截逻辑。
     * @return 包含 ID、拦截点、优先级及受失效状态控制的执行视图的注册对象。
     * @since 5.0
     */
    public fun <C : Any, R, P : InterceptionPoint<C, R>> register(
        point: P,
        priority: Int = PriorityConstant.DEFAULT,
        interceptor: Interceptor<C, R, P>
    ): InterceptorRegistration<C, R, P>
}

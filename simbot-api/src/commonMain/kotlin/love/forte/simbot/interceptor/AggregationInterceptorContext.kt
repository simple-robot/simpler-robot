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

package love.forte.simbot.interceptor

/**
 * 聚合式拦截器上下文。
 *
 * 聚合式上下文负责把一组同类型拦截器与最终目标逻辑组合成一次完整的拦截流程。
 * 实现应当使 [invoke][Interceptor.Context.invoke] 成为这条完整流程的执行入口：
 * 调用它时，流程预期会按实现定义的顺序经过所有适用的拦截逻辑，
 * 并在拦截器全部放行后执行最终的真实逻辑。
 *
 * 在流程推进过程中，具体实现可以自行决定每一次 [invoke][Interceptor.Context.invoke]
 * 是进入下一层拦截器，还是在没有后续拦截器时执行最终目标逻辑。
 *
 * 该接口只表达“上下文具备聚合拦截流程”的语义，不规定拦截器存储结构、推进方式、
 * 重复调用行为或并发行为。需要基于 [Iterator] 游标推进的默认实现时，
 * 可继承 [IteratorAggregationInterceptorContext]。
 *
 * @see Interceptor.Context
 * @see IteratorAggregationInterceptorContext
 * @author Forte Scarlet
 */
public interface AggregationInterceptorContext<R> : Interceptor.Context<R>

/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot

import love.forte.simbot.utils.runInNoScopeBlocking

/**
 *
 * 拦截器.
 *
 * @author ForteScarlet
 */
public interface Interceptor<C : Interceptor.Context<R>, R> {
    
    /**
     * 对当前指定的拦截内容进行处理。
     *
     * 想要继续流程则使用 [Context.proceed] 进入到下一个拦截器，或者进入正常流程。
     *
     * 例如放行:
     * ```kotlin
     * override suspend fun intercept(context: FooContext): FooResult {
     *    // do something...?
     *
     *    // 执行 context.proceed() 就是放行。
     *    val result = context.proceed()
     *    // and do something...?
     *
     *    return result
     * }
     * ```
     * 例如拦截:
     * ```kotlin
     * override suspend fun intercept(context: BarContext): BarResult {
     *    // 不执行 context.proceed() 就是拦截。
     *    // 自行构建一个result实例
     *    return BarResult(...)
     *
     * }
     * ```
     *
     *
     */
    @JvmSynthetic
    public suspend fun intercept(context: C): R
    
    
    /**
     * 拦截器的拦截对象。此对象可能是下一层拦截器，或者是真正的目标。
     */
    public interface Context<R> {
        
        /**
         * 继续进行后续流程。
         *
         * 当执行了 [proceed], 即可理解为通常意义的“放行”。[proceed] 中会继续进行后续逻辑。
         */
        @JvmSynthetic
        public suspend fun proceed(): R
        
        /**
         * 阻塞的执行 [proceed]。
         *
         */
        @Api4J
        public fun proceedBlocking(): R = runInNoScopeBlocking { proceed() }
    }
}



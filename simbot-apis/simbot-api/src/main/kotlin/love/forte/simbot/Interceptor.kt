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
 */

package love.forte.simbot

import love.forte.simbot.utils.runInBlocking

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
        public fun proceedBlocking(): R = runInBlocking { proceed() }
    }
}



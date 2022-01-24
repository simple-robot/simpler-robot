/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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
     */
    @JvmSynthetic
    public suspend fun intercept(context: C): R


    /**
     * 拦截器的拦截对象。此对象可能是下一层拦截器，或者是真正的目标。
     */
    public interface Context<R> {
        @JvmSynthetic
        public suspend fun proceed(): R

        @Api4J
        public fun proceedBlocking(): R = runInBlocking { proceed() }
    }
}



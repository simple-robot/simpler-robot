/*
 *  Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
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

package test

import love.forte.simbot.*
import love.forte.simbot.core.event.*
import love.forte.simbot.event.*
import kotlin.test.*


/**
 *
 * @author ForteScarlet
 */
class CoreListenerManagerTest {

    @Test
    fun createTest() {


        coreListenerManager {
            interceptors {
                // 一个处理流程拦截器, 即针对每一次的整个流程进行整体拦截
                processingIntercept { context: EventProcessingInterceptor.Context ->
                    context.eventContext.event.bot.logger.info("Processing intercept: {}", context)
                    context.proceed()
                }

                // 一个监听函数拦截器, 会拦截在所有监听函数上
                listenerIntercept { context: EventListenerInterceptor.Context ->
                    context.listener.logger.info("Listener intercept: {}", context)
                    context.proceed()
                }
            }

            // 或者独立添加拦截器实例
            // 参数是此拦截器对应ID的映射表

            addListenerInterceptors(mapOf(randomID() to coreListenerInterceptor { it.proceed() }))

            addProcessingInterceptors(mapOf(randomID() to coreProcessingInterceptor { it.proceed() }))

        }
    }

}
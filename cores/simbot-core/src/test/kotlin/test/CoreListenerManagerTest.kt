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
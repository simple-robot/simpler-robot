package love.forte.simboot.spring.autoconfigure

import love.forte.di.BeanContainer
import love.forte.di.all
import love.forte.simboot.interceptor.AnnotatedEventListenerInterceptor
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationBuilder
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfiguration
import love.forte.simbot.core.event.EventInterceptorsGenerator
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventProcessingInterceptor
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import kotlin.reflect.full.isSubclassOf

/**
 *
 * 自动扫描并注册所有的全局拦截器的spring boot starter配置类。
 *
 * @author ForteScarlet
 */
public open class SimbotSpringBootInterceptorsAutoConfigure : SimbotSpringBootApplicationBuildConfigure,
    ApplicationContextAware {
    
    private lateinit var beanContainer: BeanContainer
    
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        beanContainer = SpringBeanContainer(applicationContext)
    }
    
    override fun SpringBootApplicationBuilder.config(configuration: SpringBootApplicationConfiguration) {
        eventProcessor {
            interceptors {
                processingInterceptors()
                listenerInterceptors()
            }
        }
    }
    
    private fun EventInterceptorsGenerator.processingInterceptors() {
        beanContainer.all<EventProcessingInterceptor>().forEach { name ->
            processingIntercept(name) {
                val instance = beanContainer[name] as EventProcessingInterceptor
                instance.intercept(it)
            }
        }
    }
    
    private fun EventInterceptorsGenerator.listenerInterceptors() {
        beanContainer.all<EventListenerInterceptor>().forEach { name ->
            val type = beanContainer.getType(name)
            if (type.isSubclassOf(AnnotatedEventListenerInterceptor::class)) {
                // 不使用 AnnotatedEventListenerInterceptor
                return@forEach
            }
            listenerIntercept(name) {
                val instance = beanContainer[name, type] as EventListenerInterceptor
                instance.intercept(it)
            }
        }
    }
    
}
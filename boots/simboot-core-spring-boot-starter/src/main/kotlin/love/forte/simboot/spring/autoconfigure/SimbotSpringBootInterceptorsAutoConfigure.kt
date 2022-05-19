package love.forte.simboot.spring.autoconfigure

import love.forte.di.BeanContainer
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationBuilder
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfiguration
import love.forte.simbot.core.event.EventInterceptorsGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

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
        
        TODO("Not yet implemented")
    }
    
    private fun EventInterceptorsGenerator.processingInterceptors() {
    
    }
    
    private fun EventInterceptorsGenerator.listenerInterceptors() {
    
    }
    
}
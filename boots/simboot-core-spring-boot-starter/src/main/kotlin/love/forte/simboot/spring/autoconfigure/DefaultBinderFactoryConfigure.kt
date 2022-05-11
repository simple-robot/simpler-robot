package love.forte.simboot.spring.autoconfigure

import love.forte.simboot.core.binder.AutoInjectBinderFactory
import love.forte.simboot.core.binder.EventParameterBinderFactory
import love.forte.simboot.core.binder.InstanceInjectBinderFactory
import love.forte.simboot.core.binder.KeywordBinderFactory
import love.forte.simboot.listener.ParameterBinderFactory
import org.springframework.context.annotation.Bean


/**
 * 将部分默认的 [ParameterBinderFactory] 实现添加到环境中。
 *
 */
public open class DefaultBinderFactoryConfigure {
    
    @Bean
    public fun autoInjectBinderFactory(): AutoInjectBinderFactory = AutoInjectBinderFactory
    
    @Bean
    public fun eventParameterBinderFactory(): EventParameterBinderFactory = EventParameterBinderFactory
    
    @Bean
    public fun instanceInjectBinderFactory(): InstanceInjectBinderFactory = InstanceInjectBinderFactory
    
    @Bean
    public fun keywordBinderFactory(): KeywordBinderFactory = KeywordBinderFactory
    
    // TODO Simple Message Value Binder
    
}

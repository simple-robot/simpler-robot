/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.spring.autoconfigure

import love.forte.simboot.core.binder.AutoInjectBinderFactory
import love.forte.simboot.core.binder.EventParameterBinderFactory
import love.forte.simboot.core.binder.InstanceInjectBinderFactory
import love.forte.simboot.core.binder.KeywordBinderFactory
import love.forte.simboot.listener.ParameterBinderFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean


/**
 * 将部分默认的 [ParameterBinderFactory] 实现添加到环境中。
 *
 */
public open class DefaultBinderFactoryConfigure {
    
    @Bean
    @ConditionalOnMissingBean(AutoInjectBinderFactory::class)
    public fun autoInjectBinderFactory(): AutoInjectBinderFactory = AutoInjectBinderFactory
    
    @Bean
    @ConditionalOnMissingBean(EventParameterBinderFactory::class)
    public fun eventParameterBinderFactory(): EventParameterBinderFactory = EventParameterBinderFactory
    
    @Bean
    @ConditionalOnMissingBean(InstanceInjectBinderFactory::class)
    public fun instanceInjectBinderFactory(): InstanceInjectBinderFactory = InstanceInjectBinderFactory
    
    @Bean
    @ConditionalOnMissingBean(KeywordBinderFactory::class)
    public fun keywordBinderFactory(): KeywordBinderFactory = KeywordBinderFactory
    
    // TODO Simple Message Value Binder
    
}

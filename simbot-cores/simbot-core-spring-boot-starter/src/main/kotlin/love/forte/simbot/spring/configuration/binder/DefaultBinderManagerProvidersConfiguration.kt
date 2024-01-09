/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.spring.configuration.binder

import love.forte.simbot.quantcat.annotations.FilterValue
import love.forte.simbot.quantcat.annotations.toProperties
import love.forte.simbot.quantcat.common.binder.impl.EventParameterBinderFactory
import love.forte.simbot.quantcat.common.binder.impl.KeywordBinderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.reflect.full.findAnnotation

/**
 * 用以注册部分默认提供的 Spring 环境下全局配置的绑定器配置。
 */
@Configuration(proxyBeanMethods = false)
public open class DefaultBinderManagerProvidersConfiguration {
    @Bean
    public open fun eventParameterBinderFactory(): EventParameterBinderFactory = EventParameterBinderFactory

    @Bean
    public open fun keywordBinderFactory(): KeywordBinderFactory = KeywordBinderFactory { context ->
        context.parameter.findAnnotation<FilterValue>()?.toProperties()
    }
}

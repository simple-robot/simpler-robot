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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor


/**
 * 在 `spring-boot-starter` 中提供事件调度器 [CoroutineDispatcher] 的容器。
 */
public open class CoroutineDispatcherContainer(public open val dispatcher: CoroutineDispatcher)


/**
 *
 * 对 [CoroutineDispatcherContainer] 的默认配置类。
 *
 * 将 spring 所提供的线程池实例 [ThreadPoolTaskExecutor] 转化为 [CoroutineDispatcher]
 * 并置于容器中。
 *
 * @author ForteScarlet
 */
public open class CoroutineDispatcherConfiguration {
    /**
     * 默认情况下将spring内部的 [ThreadPoolTaskExecutor] 作为调度器。
     */
    @Bean
    @ConditionalOnMissingBean(CoroutineDispatcherContainer::class)
    public open fun defaultSimbotEventDispatcher(executor: ThreadPoolTaskExecutor): CoroutineDispatcherContainer =
        CoroutineDispatcherContainer(executor.asCoroutineDispatcher())
    
    
}

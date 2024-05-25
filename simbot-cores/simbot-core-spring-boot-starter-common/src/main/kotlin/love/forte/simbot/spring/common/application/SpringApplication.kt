/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.simbot.spring.common.application

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.application.*
import love.forte.simbot.core.event.SimpleEventDispatcherConfiguration
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

/**
 * 使用在 Spring Starter 中的 [Application] 实现。
 * 主要由内部使用。
 */
public interface SpringApplication : Application

/**
 * Factory for [SpringApplication].
 */
public interface SpringApplicationFactory :
    ApplicationFactory<
        SpringApplication,
        SpringApplicationBuilder,
        SpringApplicationLauncher,
        SpringApplicationEventRegistrar,
        SpringEventDispatcherConfiguration
        >

/**
 * Simbot Spring Boot Starter 中使用的 [ApplicationBuilder]
 *
 */
public open class SpringApplicationBuilder : AbstractApplicationBuilder() {
    /**
     * @see SpringApplicationConfigurationProperties
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public open var applicationConfigurationProperties: SpringApplicationConfigurationProperties =
        SpringApplicationConfigurationProperties()

    /**
     * Build [SpringApplicationConfiguration]
     */
    @InternalSimbotAPI
    public open fun build(): SpringApplicationConfiguration =
        SpringApplicationConfigurationImpl(
            coroutineContext,
            applicationConfigurationProperties
        )

}

public interface SpringApplicationConfiguration : ApplicationConfiguration {
    /**
     * @see SpringApplicationConfigurationProperties
     */
    public val applicationConfigurationProperties: SpringApplicationConfigurationProperties
}

/**
 * Simbot Spring Boot Starter 中使用的 [ApplicationLauncher] 实现。
 *
 */
public interface SpringApplicationLauncher : ApplicationLauncher<SpringApplication>

/**
 * Simbot Spring Boot Starter 中使用的 [ApplicationEventRegistrar] 实现。
 *
 */
public interface SpringApplicationEventRegistrar : ApplicationEventRegistrar

/**
 * Simbot Spring Boot Starter 中使用的调度器配置。
 *
 */
public interface SpringEventDispatcherConfiguration : SimpleEventDispatcherConfiguration {

    /**
     * 添加一个 [Executor] 并作为 [协程调度器][CoroutineDispatcher] 添加到 [CoroutineContext] 中。
     */
    public fun setExecutorDispatcher(executor: Executor) {
        coroutineContext += executor.asCoroutineDispatcher()
    }
}

/**
 * Implementation for [SpringApplicationConfiguration]
 */
@InternalSimbotAPI
public open class SpringApplicationConfigurationImpl(
    override val coroutineContext: CoroutineContext,
    override val applicationConfigurationProperties: SpringApplicationConfigurationProperties,
) : SpringApplicationConfiguration

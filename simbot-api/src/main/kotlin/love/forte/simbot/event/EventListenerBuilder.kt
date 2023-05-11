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

package love.forte.simbot.event

/**
 *
 * 一个 [EventListener] 的标准构建器接口。
 *
 * [EventListenerBuilder] 除了用于各构建器的实现之外，
 * 也会使用在 `boot` 模块和 `spring-boot-starter`
 * 模块中通过依赖注入功能来通过 [EventListenerBuilder] 提供自定义监听函数实现。
 *
 * 例如在 `spring-boot-starter` 模块中：
 * ```kotlin
 * @Configuration
 * class FooListenerConfiguration {
 *   @Bean
 *   fun fooListener() = FooListenerBuilder(FooEvent).also {
 *      // ...
 *   }
 * }
 * ```
 *
 * 其效果类似于直接向依赖环境中注入一个 [EventListener] 实例。例如：
 * ```kotlin
 * @Configuration
 * class FooListenerConfiguration {
 *   @Bean
 *   fun fooListener() = fooBuildListener(FooEvent) {
 *       // ...
 *   }
 * }
 * ```
 *
 * 注意：这种情况下，你不能使用 [@Listener][love.forte.simboot.annotation.Listener] 注解，
 * 而是应该使用对应IOC框架中的注入注解，例如springboot中的 `@Bean` 或者 boot 模块中的 `@Beans`。
 *
 * 在 spring 环境下，[@Listener][love.forte.simboot.annotation.Listener] 注解支持判断当前函数的类型。
 * 如果 [@Listener][love.forte.simboot.annotation.Listener] 标记的函数返回值为 [EventListenerBuilder] 类型，
 * 则当前函数将会作为构建器被加入依赖环境，而不会被作为监听函数解析。
 *
 * [EventListenerBuilder] 的特殊子类型 [EventListenerRegistrationDescriptionBuilder] 将会被检测, 并在支持的情况下优先产生
 * [EventListenerRegistrationDescription] 实例.
 *
 * @see EventListenerRegistrationDescriptionBuilder
 *
 * @author ForteScarlet
 */
public interface EventListenerBuilder {
    
    /**
     * 构建得到当前构建器中所描述的 [EventListener] 实例。
     */
    public fun build(): EventListener
    
}


/**
 * 在 [EventListenerBuilder] 的基础上允许根据一些可能的额外信息构建一个 [EventListenerRegistrationDescription] 实例.
 *
 *
 *
 */
public interface EventListenerRegistrationDescriptionBuilder : EventListenerBuilder {
    
    /**
     * 构建得到当前构建器中所描述地 [EventListenerRegistrationDescription] 实例.
     */
    public fun buildDescription(): EventListenerRegistrationDescription
    
}

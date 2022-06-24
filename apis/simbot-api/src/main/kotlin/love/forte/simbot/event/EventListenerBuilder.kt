package love.forte.simbot.event

import love.forte.simbot.PriorityConstant

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
 * @author ForteScarlet
 */
public interface EventListenerBuilder {
    
    /**
     * 当前监听函数的ID.
     * 如果尚未初始化，则获取会得到空字符串。
     */
    public var id: String
    
    /**
     * 监听函数的优先级。
     *
     * 通常情况下默认值为 [PriorityConstant.NORMAL]。
     */
    public var priority: Int
    
    /**
     * 异步标记。通常情况下默认值为 `false`。
     */
    public var isAsync: Boolean
    
    
    /**
     * 构建得到当前构建器中所描述的 [EventListener] 实例。
     */
    public fun build(): EventListener
    
}
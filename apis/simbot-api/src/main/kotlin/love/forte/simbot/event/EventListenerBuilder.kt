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
 * @author ForteScarlet
 */
public interface EventListenerBuilder {
    
    /**
     * 构建得到当前构建器中所描述的 [EventListener] 实例。
     */
    public fun build(): EventListener
    
}
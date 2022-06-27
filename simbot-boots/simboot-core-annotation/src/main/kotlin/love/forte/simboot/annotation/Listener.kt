/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simboot.annotation

import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerBuilder
import org.springframework.context.annotation.Bean


/**
 * 标记一个函数为监听器/监听函数，并尝试自动检测其监听类型。
 *
 * 当标记在一个函数上的时候，应当提供**至多一个**事件类型的参数作为你需要监听的事件类型。eg：
 *
 * ```kotlin
 * @Listener
 * suspend fun ChannelMessageEvent.myListener() { ... }
 * ```
 *
 * 默认情况下，此函数的ID为其全限定名，你可以通过 [@Listener(id="...")][Listener.id] 指定一个ID。
 * 当不指定的时候默认为当前标记对象的全限定二进制名称或随机UUID字符串。
 *
 * 在 Boot相关模块下，[@Listener][Listener] 会检测标记函数的返回值类型。如果返回值类型为 [EventListenerBuilder] 或者 [EventListener],
 * 则会将当前函数会直接被当作为一个需要注册的bean，而不再解析为一个函数式的监听函数。
 *
 * 以Spring环境下为例，上述情况的函数将会被视作一个 Spring Bean 出处理，相当于标记了 [@Bean][Bean], 并不会再作为监听函数注册。
 *
 * 需要注意的是，如果作为一个 [EventListenerBuilder] 或 [EventListener] Bean 注册的话，被标记函数**不可**为挂起函数。
 *
 * ```kotlin
 * @Component
 * class Foo {
 *    @Listener
 *    suspend fun EventProcessingContext.listener(event: FooEvent) {
 *      // 此函数为普通的监听函数
 *      // 可以使用挂起函数
 *    }
 *
 *    @Listener
 *    fun fooListenerBuilder(): EventListenerBuilder {
 *       // 此函数的返回值会作为Bean注册，不会被作为监听函数。
 *       // 不可使用挂起函数
 *       return ...
 *    }
 *
 *    @Listener
 *    fun fooListenerBuilder() = buildSimpleListener(FooEvent) {
 *       // 此函数的返回值会作为Bean注册，不会被作为监听函数。
 *       // 不可使用挂起函数
 *
 *       id = "..."
 *       match { condition1 }
 *       match { condition2 }
 *       handle {
 *          // ...
 *          EventResult.defaults()
 *       }
 *
 *    }
 *
 *    @Bean
 *    fun barListenerBuilder(): EventListenerBuilder {
 *       // 更推荐养成使用明确区分二者注解的标准写法的习惯。
 *       // 不可使用挂起函数
 *       return ...
 *    }
 *
 *    @Bean
 *    fun barListener(): EventListener {
 *       // 更推荐养成使用明确区分二者注解的标准写法的习惯。
 *       // 不可使用挂起函数
 *       return ...
 *    }
 * }
 * ```
 * 当通过直接返回 [EventListener] 或者 [EventListenerBuilder] 的形式注册监听函数的时候，[@Listener][Listener] 将仅作为标记用，
 * 其中的所有参数（ 例如 [id][Listener.id] 、[async][Listener.async]）都将无效。
 *
 *
 * **⚠️注意:** 与在 spring 环境下不同，在 boot 模块中支持通过 [@Listener][Listener] 注册一个 [EventListenerBuilder] 或 [EventListener],
 * 但是不支持这个被标记的函数**存在参数**。这也是为什么建议在任何环境下都使用明确的注解：
 * 如果是注册 [EventListenerBuilder] 或 [EventListener]，最好使用明确的 `@Beans` 或者 `@Bean`, 而不是统一使用 [@Listener][Listener]。
 *
 *
 * @param id 监听函数ID。
 * @param priority 此事件的优先级。
 * @param async 此函数是否为异步函数。
 * 如果为 `object` 类型，则会直接获取实例，否则会尝试通过可用途径获取对应结果。
 *
 * @see Filter
 * @see Interceptor
 * @see love.forte.simbot.event.EventListener
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
public annotation class Listener(
    val id: String = "",
    val priority: Int = PriorityConstant.NORMAL,
    val async: Boolean = false, // TODO 存在未知问题会导致此事件无法被触发
)

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

import love.forte.simboot.annotation.TargetFilter.Companion.NON_PREFIX
import love.forte.simboot.filter.MatchType
import love.forte.simboot.filter.MultiFilterMatchType
import love.forte.simbot.event.ChannelEvent
import love.forte.simbot.event.GroupEvent
import love.forte.simbot.event.GuildEvent
import kotlin.reflect.KClass


/**
 * 与 [Listener] 配合使用，会被解析为对应监听函数的通用属性过滤器。
 * e.g.
 * ```kotlin
 * // 默认情况下, 多个 @Filter 存在的时候匹配模式为 ANY, 即其中任意一个匹配即可。
 * @Filter("example1", matchType = MatchType.REGEX_MATCHES)
 * @Filter("example2", target = TargetFilter(authors = ["id1", "id2"]))
 * suspend fun FooEvent.bar() {
 *  // ...
 * }
 *
 * ```
 * <br>
 *
 * ### Spring的堆栈溢出
 * 如果你要在 [Spring](https://spring.io/) 中使用此注解，那么你必须保证你项目中的 [Spring Framework](https://spring.io/projects/spring-framework) 的版本大于等于 **`5.3.16`**。
 * (**注: SpringBoot应当至少为 `2.6.6`。**)
 *
 * 具体原因请参考 [Spring Framework#28012](https://github.com/spring-projects/spring-framework/issues/28012) .
 *
 * 在Spring Boot中更新 Spring Framework的方式可参考 [Dependency Versions](https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html).
 *
 * **Maven**
 * ```properties
 * <properties>
 *      <spring-framework.version>5.3.16</spring-framework.version>
 * </properties>
 * ```
 *
 * **Gradle Groovy**
 * ```groovy
 *  ext['spring-framework.version'] = '5.3.16'
 * ```
 *
 * **Gradle Kotlin DSL**
 * ```kotlin
 * ext["spring-framework.version"] = "5.3.16"
 * ```
 *
 
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@JvmRepeatable(Filters::class)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
public annotation class Filter(
    /**
     * 匹配规则值，会对 [EventListenerProcessingContext.textContent][love.forte.simbot.event.EventListenerProcessingContext.textContent] 进行匹配。
     * 如果此属性为空，则相当于不生效。
     */
    val value: String = "",
    
    /**
     * 当 [value] 匹配的目标（[EventListenerProcessingContext.textContent][love.forte.simbot.event.EventListenerProcessingContext.textContent]）的值为 null 的时候，
     * 是否直接放行。如果为 `true`, 则代表匹配值为null的时候视为匹配通过，反之则为匹配失败。默认为 `false`。此参数只有当 [value] 不为空的时候有效。如果 [value] 为空，则不会进行匹配。
     * 这并不仅仅局限于[消息事件][love.forte.simbot.event.MessageEvent], 而生效与所有的事件类型。而对于那些本身即不是消息事件的事件来说, `textContent` 默认为null。
     *
     */
    val ifNullPass: Boolean = false,
    
    /**
     * 针对匹配目标所使用的匹配规则。
     * 默认情况下使用 [正则完全匹配][MatchType.REGEX_MATCHES].
     */
    val matchType: MatchType = MatchType.REGEX_MATCHES,
    
    /**
     * 目标过滤内容。
     *
     * 整合了常见的一些过滤参数，例如群号或者发送人账号。
     *
     * @see [TargetFilter]
     */
    val target: TargetFilter = TargetFilter(),
    
    /**
     * 可以再提供一个 `&&` (与关系)的子过滤器，最终结果为 `当前filter && and-filters` .
     * ### Kotlin
     * ```kotlin
     * @Filter("Foo1")
     * @Filter("Foo2", and = Filters(
     *      Filter("Foo3"),
     *      Filter("Foo4"),
     * ))
     * @Listener
     * suspend fun Event.test(){ ... }
     * ```
     *
     * ### Java
     * ```java
     * @Filter("Foo1")
     * @Filter(value = "Foo2", and = @Filters(value = {
     *       @Filter("Foo3"),
     *       @Filter("Foo4")
     * }))
     * @Listener
     * public void listen() {
     * }
     *
     * ```
     *
     *
     * 与 [or] 同时存在时候，匹配效果则如：`this-filter && and-filters || or-filters`
     *
     * 不建议在注解中存在过多的filter嵌套，假如有需要，考虑使用 [by] 指定具体的过滤器实现。
     *
     */
    val and: Filters = Filters(),
    
    /**
     * 可以再提供一个 `||` (或关系)的子过滤器，最终结果为 `当前filter || and-filters`.
     * ### Kotlin
     * ```kotlin
     * @Filter("Foo1")
     * @Filter("Foo2", or = Filters(
     *      Filter("Foo3"),
     *      Filter("Foo4"),
     * ))
     * @Listener
     * suspend fun Event.test(){ ... }
     * ```
     *
     * ### Java
     * ```java
     * @Filter("Foo1")
     * @Filter(value = "Foo2", or = @Filters(value = {
     *       @Filter("Foo3"),
     *       @Filter("Foo4")
     * }))
     * @Listener
     * public void listen() {
     * }
     *
     * ```
     *
     * 与 [and] 同时存在时候，匹配效果则如：`this-filter && and-filters || or-filters`
     *
     */
    val or: Filters = Filters(),

    /**
     * 指定一个对当前 [Filter] 的处理过滤器。当 [by] 指定了任意一个不直接等同于 [AnnotationEventFilter]
     * 的类型时，此注解的上述其他参数将不再继续被解析，而是直接交由指定目标进行处理。
     *
     *
     * ```kotlin
     * @Filter(by = FooAnnotationEventFilter::class)
     * suspend fun Event.onEvent() { ... }
     * ```
     *
     */
    val by: KClass<out AnnotationEventFilter> = AnnotationEventFilter::class
    
    )


/**
 * 通用属性过滤规则。
 *
 * 针对目标对象（例如事件组件、bot、群、联系人等）的匹配规则，不可标记在任何地方，作为 [Filter] 的参数 [target][Filter.target] 使用.
 *
 * 以下所有属性的匹配结果为并集，即**全部**匹配成功后得到true。假如某参数为空，则认为其为 `true`.
 *
 * ## “非”前缀
 *
 * 下述所有属性中，如果某个值的前缀为 [NON_PREFIX], 则其代表的意思与正常相反：即需要不等于。
 * 例如一个 `@TargetFilter(bots = ["forliy"])`, 其代表此事件只能由 `bot.id == "forliy"` 的bot匹配。
 * 而如果使用 `@TargetFilter(bots = ["!forliy"])`, 则代表由所有 `bot.id != "forliy"` 的bot匹配。
 *
 * @see Filter
 */
@Retention(AnnotationRetention.SOURCE)
@Target(allowedTargets = [])
public annotation class TargetFilter(
    /**
     * 对接收事件的组件匹配. 大多数情况下，对于组件的唯一ID，组件实现库都应当有所说明或通过常量提供。 `["comp1", "comp2"]`
     *
     * 相当于:
     * ```kotlin
     *  event.component.id.literal in components
     * ```
     *
     * 除了通过此 [components] 作为组件的筛选条件，直接监听一个组件下特有的事件类型能够更好的起到组件过滤的作用。
     */
    val components: Array<String> = [],
    
    /**
     * 对接收事件的botID匹配。
     *
     * 相当于:
     * ```kotlin
     * event.bot.id.literal in bots
     * ```
     */
    val bots: Array<String> = [],
    
    /**
     * 对消息发送者的ID匹配。
     *
     * 相当于:
     * ```kotlin
     * event.author().id.literal in authors
     * ```
     */
    val authors: Array<String> = [],
    
    /**
     * 如果这是个[群相关事件][GroupEvent] ，则对群ID匹配。
     *
     * 相当于:
     * ```kotlin
     * event.group().id.literal in groups
     * ```
     *
     */
    val groups: Array<String> = [],
    
    /**
     * 如果是个[子频道相关事件][ChannelEvent], 则对频道ID匹配。
     *
     * 相当于:
     * ```kotlin
     * event.channel().id.literal in channels
     * ```
     */
    val channels: Array<String> = [],
    
    /**
     * 如果是个[频道服务器相关事件][GuildEvent], 则对频道服务器ID匹配。
     */
    val guilds: Array<String> = [],
    
    /**
     * 只有当前消息中存在任意一个 [At.target][love.forte.simbot.message.At.target] == event.bot.id 的 [At][love.forte.simbot.message.At] 消息的时候才会通过匹配。
     *
     * 相当于:
     * ```kotlin
     * event.messageContent.messages.any { it is At && it.target == event.bot.id }
     * ```
     * 此参数只有在当前事件类型为 [ChatroomMessageEvent][love.forte.simbot.event.ChatRoomMessageEvent] 的时候才会生效，且建议配合 [ContentTrim] 一起使用。
     *
     * 需要注意的是, [atBot] 的匹配结果**不一定准确**，例如当 bot.id 与实际的at目标ID不一致的时候。是否准确由对应组件下Bot、At等相关内容的构建与实现方式有关。此问题或许会在后续版本提供一个约定接口来完善相关匹配。
     *
     */
    val atBot: Boolean = false,
) {
    public companion object {
        /**
         * [TargetFilter] 中的“非”前缀。
         *
         */
        public const val NON_PREFIX: String = "!"
    }
}


/**
 * 多个 [子过滤器][Filter] 的容器。一个 [Filters] 最终会表现为一个汇总过滤器。
 *
 * @see Filter
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
public annotation class Filters(
    /**
     * 所有子过滤器。
     */
    vararg val value: Filter,
    
    /**
     * 多个过滤器之间的匹配策略。默认情况下为 [any][MultiFilterMatchType.ANY] 匹配。
     */
    val multiMatchType: MultiFilterMatchType = MultiFilterMatchType.ANY,
)
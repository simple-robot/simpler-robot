/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.annotation

import love.forte.simboot.filter.*
import love.forte.simbot.event.ChannelEvent
import love.forte.simbot.event.GroupEvent
import love.forte.simbot.event.GuildEvent
import kotlin.reflect.KClass

/**
 * 与 [Listener] 或 [Listen] 配合使用，会被解析为对应监听函数的默认过滤器。
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@JvmRepeatable(Filters::class)
@Target(AnnotationTarget.FUNCTION)
public annotation class Filter(
    /**
     * 匹配规则值。
     */
    val value: String,

    /**
     * 针对匹配目标所使用的匹配规则。
     * 默认情况下使用 [正则完全匹配][MatchType.REGEX_MATCHES].
     */
    val matchType: MatchType = MatchType.REGEX_MATCHES,

    /**
     * 目标过滤.
     */
    val target: TargetFilter = TargetFilter(),


    /**
     * 可以再提供一个 `&` 与关系的子过滤器，最终结果为 `当前filter && (and filters)`
     *
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
     * 与 [or] 同时存在时候，匹配效果则如：`this filter && and filter || or filter`
     *
     */
    val and: Filters = Filters(),


    /**
     * 可以再提供一个 `|` 或关系的子过滤器，最终结果为 `当前filter || (and filters)`.
     *
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
     * 与 [and] 同时存在时候，匹配效果则如：`this filter && and filter || or filter`
     */
    val or: Filters = Filters(),


    /**
     * 当前注解应使用的注解处理器。
     * 对应的处理器类型如果是 `object` 类型，则会直接获取其实例。
     *
     * 如果为普通class则在环境允许的情况下会尝试通过依赖管理器获取，
     * 如果当前环境不存在诸如依赖获取器一类的东西，则会直接实例化。
     *
     * 当指定了自定义 [processor] 后，
     * 上述所有字段的最终表现形式均无法得到保证，其最终结果由指定的处理器全权负责。
     *
     * 当 [Filters.processor] 被重新指定后，无法保证对于当前 [processor] 的处理方式。
     *
     */
    val processor: KClass<out FilterAnnotationProcessor> = BootFilterAnnotationProcessor::class,


    )


/**
 * 针对目标对象（例如事件组件、bot、群、联系人等）的匹配规则，不可标记在任何地方，作为 [Filter] 的参数 [Filter.target].
 *
 * 以下所有参数的匹配结果为合集，即全部匹配成功后得到true。
 *
 */
public annotation class TargetFilter(
    /**
     *
     */
    val components: Array<String> = [],
    /**
     * 对接收事件的botID匹配
     */
    val bots: Array<String> = [],
    /**
     * 对消息发送者的ID匹配。
     */
    val authors: Array<String> = [],

    /**
     * 如果这是个群相关事件 [GroupEvent] ，则对群ID匹配
     */
    val groups: Array<String> = [],

    /**
     * 如果是个频道相关事件 [ChannelEvent], 则对频道ID匹配
     */
    val channels: Array<String> = [],

    /**
     * 如果是个频道服务器相关事件 [GuildEvent], 则对频道服务器ID匹配
     */
    val guilds: Array<String> = []
)


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
public annotation class Filters(
    /**
     * 所有过滤器
     */
    vararg val value: Filter,

    /**
     * 多个过滤器之间的匹配策略。
     */
    val multiMatchType: MultiFilterMatchType = MultiFilterMatchType.ALL,

    /**
     * 当前注解应使用的注解处理器。
     * 对应的处理器类型如果是 `object` 类型，则会直接获取其实例。
     *
     * 如果为普通class则在环境允许的情况下会尝试通过依赖管理器获取，
     * 如果当前环境不存在诸如依赖获取器一类的东西，则会直接实例化。
     *
     * 当指定了自定义 [processor] 后，
     * 上述所有字段的最终表现形式均无法得到保证，其最终结果由指定的处理器全权负责。
     *
     */
    val processor: KClass<out FiltersAnnotationProcessor> = BootFiltersAnnotationProcessor::class
)
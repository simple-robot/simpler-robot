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

import love.forte.simboot.interceptor.AnnotatedEventListenerInterceptor
import love.forte.simbot.PriorityConstant
import kotlin.reflect.KClass

/**
 * 配合 [Listener] 使用，为一个监听函数指定一个或多个**专属**的监听函数拦截器.
 *
 * 在 [Interceptor] 中使用的拦截器类型为 [AnnotatedEventListenerInterceptor], **而不是**普通的 [love.forte.simbot.event.EventListenerInterceptor], 需要注意。
 *
 * 专属拦截器的整体优先级低于全局拦截器。有关[流程拦截器][love.forte.simbot.event.EventProcessingInterceptor]、[监听函数拦截器][love.forte.simbot.event.EventListenerInterceptor]、[专属监听函数拦截器][AnnotatedEventListenerInterceptor]、
 * [监听函数过滤器][love.forte.simbot.event.EventFilter]、[监听器][love.forte.simbot.event.EventListener] 之间的整体流程顺序大致如下：
 * ```
 *     + -------- +
 *     | 流程拦截器 | EventProcessingInterceptor
 *     + ---+---- +
 *          |
 *   + -----+----- +
 *   | 监听函数拦截器 | EventListenerInterceptor
 *   + -----+----- +
 *          |
 * + -------+------- +
 * | 专属监听函数拦截器 | AnnotatedEventListenerInterceptor (当前)
 * + -------+------- +
 *          |
 *   +------+----- +
 *   | 监听函数过滤器 | EventFilter
 *   +------+----- +
 *          |
 *      + --+-- +
 *      | 监听器 | EventListener
 *      + ----- +
 * ```
 *
 * [Interceptor] 是可重复的。当使用的时候，目标函数上需要标记 [Listener]。
 * ```
 * @Interceptor(...)
 * @Interceptor(...)
 * @Listener
 * suspend fun GroupEvent.myListener() { ... }
 * ```
 *
 *
 * @property value 通过**唯一标识**获取所需拦截器。这个唯一标识一般为注入到bean容器中的唯一标识名称，你需要保证此 [value] 对应的元素值的类型即为 [AnnotatedEventListenerInterceptor] 类型实现类。
 * 当使用 [value] 的时候（[value] 不为空字符串时），[type]的值将会被忽略。
 *
 * @property type 通过**类型**获取所需拦截器。
 * 对于类型的获取，流程如下：
 * 1. 如果对应类型为 kotlin object 类型，则会直接获取实例；
 * 2. 如果当前环境下的bean容器能且仅能获取到此类型的 **唯一实例**，那么将会使用bean容器中的结果；（如果存在多个实例，不会捕获异常）
 * 3. 如果上述两种方法不可行，则会 _尝试_ 直接通过类型的 **公开无参构造** 构建一个实例并使用。
 *
 * 只有当 [value] 为空时此参数生效，且不可以直接等于 [AnnotatedEventListenerInterceptor] 类型自身。
 *
 * @property priority 此拦截器在所有标记的 **专属** 拦截器中的优先级。
 *
 * @see Listener
 * @see AnnotatedEventListenerInterceptor
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@JvmRepeatable(Interceptors::class)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
public annotation class Interceptor(
    val value: String = "",
    val type: KClass<out AnnotatedEventListenerInterceptor> = AnnotatedEventListenerInterceptor::class,
    val priority: Int = PriorityConstant.NORMAL
)


/**
 * [Interceptor] 的可重复容器.
 *
 * @see Interceptor
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
public annotation class Interceptors(vararg val value: Interceptor)
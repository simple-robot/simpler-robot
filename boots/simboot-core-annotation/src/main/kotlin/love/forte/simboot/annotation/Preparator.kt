package love.forte.simboot.annotation

import love.forte.simboot.interceptor.ListenerPreparator
import love.forte.simbot.PriorityConstant
import kotlin.reflect.KClass

/**
 * 配合 [@Listener][Listener] 注解使用，标记需要在监听函数执行前进行的准备函数。
 *
 * 概念与 [拦截器][Interceptor] 类似，但是 [Preparator] 代表"准备"，无法对流程造成影响（除非产生异常）。
 * 更多说明参考 [ListenerPreparator].
 *
 * @see ListenerPreparator
 */
@Repeatable
@JvmRepeatable(Preparators::class)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
public annotation class Preparator(
    /**
     * 指定目标的预处理器。
     *
     * 如果 [value] 目标类型为 `object` 类型，则会尝试直接获取其实例，
     * 否则首先尝试通过 bean 容器获取，如果始终无法获取，则会尝试通过其 **无参公开构造**
     * 来获取其实例。
     *
     */
    val value: KClass<out ListenerPreparator>,
    
    /**
     * 假如提供的 [value] 能够从 bean 容器中获取，
     * 可以通过 [name] 指定此 bean 在容器中的唯一标识。
     *
     */
    val name: String = "",

    /**
     * 相对于所有预处理器之间的优先级。
     */
    val priority: Int = PriorityConstant.NORMAL
)


/**
 * [@Preparator][Preparator] 的容器接口
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
public annotation class Preparators(vararg val value: Preparator)
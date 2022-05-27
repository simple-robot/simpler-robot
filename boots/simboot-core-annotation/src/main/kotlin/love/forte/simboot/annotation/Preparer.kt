/*
 *  Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
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

import love.forte.simboot.interceptor.ListenerPreparer
import love.forte.simbot.PriorityConstant
import kotlin.reflect.KClass

/**
 * 注解错误拼写注解的保留类型，暂做兼容。
 *
 * @see Preparer
 */
@Deprecated("Use @Preparer.", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("@Preparer(value)", "love.forte.simboot.annotation.Preparer"))
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
public annotation class Preparator(
    val value: KClass<out ListenerPreparer>,
    val name: String = "",
    val priority: Int = PriorityConstant.NORMAL
)


/**
 * 配合 [@Listener][Listener] 注解使用，标记需要在监听函数执行前进行的准备函数。
 *
 * 概念与 [拦截器][Interceptor] 类似，但是 [Preparer] 代表"准备"，无法对流程造成影响（除非产生异常）。
 *
 * @see ListenerPreparer
 */
@Repeatable
@JvmRepeatable(Preparers::class)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
public annotation class Preparer(
    /**
     * 指定目标的预处理器。
     *
     * 如果 [value] 目标类型为 `object` 类型，则会尝试直接获取其实例，
     * 否则首先尝试通过 bean 容器获取，如果始终无法获取，则会尝试通过其 **无参公开构造**
     * 来获取其实例。
     *
     * @see ListenerPreparer
     *
     */
    val value: KClass<out ListenerPreparer>,
    
    /**
     * 假如提供的 [value] 能够从 bean 容器中获取，
     * 可以通过 [name] 指定此 bean 在容器中的唯一标识。
     *
     * 不为空才生效。
     *
     */
    val name: String = "",
    
    /**
     * 相对于所有预处理器之间的优先级。
     */
    val priority: Int = PriorityConstant.NORMAL
)


/**
 * [Preparer] 的容器
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
public annotation class Preparers(vararg val value: Preparer)
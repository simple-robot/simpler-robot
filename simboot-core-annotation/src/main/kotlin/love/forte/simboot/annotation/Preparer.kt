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

package love.forte.simboot.annotation

import love.forte.simboot.interceptor.ListenerPreparer
import love.forte.simbot.PriorityConstant
import kotlin.reflect.KClass

/**
 * 配合 [@Listener][Listener] 注解使用，标记需要在监听函数执行前进行的准备函数。
 *
 * 概念与 [拦截器][Interceptor] 类似，但是 [Preparer] 代表"准备"，无法对流程造成影响（除非产生异常）。
 *
 * @see ListenerPreparer
 */
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

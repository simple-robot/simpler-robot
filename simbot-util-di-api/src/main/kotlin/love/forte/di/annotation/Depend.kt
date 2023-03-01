/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.di.annotation

import love.forte.annotationtool.AnnotationMapper
import javax.inject.Inject


/**
 * 为某个Bean中的属性或者某些函数指定其注入值。
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
)
@MustBeDocumented
@AnnotationMapper(value = [Inject::class]) // support in forte-di only.
public annotation class Depend(

    /**
     * 无效的参数。
     */
    @Deprecated("Use @Named(...)")
    val name: String = "",

    /**
     * 是否为必须的。如果 [required] 为 false，且在无法断定其最终目标的时候尝试为其注入一个 `null`.
     */
    val required: Boolean = true

)

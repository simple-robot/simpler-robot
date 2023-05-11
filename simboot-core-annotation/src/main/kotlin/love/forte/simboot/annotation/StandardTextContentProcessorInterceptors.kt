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

import love.forte.annotationtool.AnnotationMapper
import love.forte.simboot.listener.StandardTextContentProcessor
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.EventListenerProcessingContext


/**
 * 对 [EventListenerProcessingContext.textContent] 进行前置处理, 清除前后空格（如果不为null的话）。
 *
 * [ContentTrim] 是 [Preparer] 的变体，且在默认情况下，[ContentTrim] 的优先级是 **最高** 的。
 *
 * 不建议 [ContentTrim] 与 [ContentToNull] 一起使用。
 *
 * @see Interceptor
 * @see StandardTextContentProcessor.Trim
 */
@ExperimentalSimbotApi
@Preparer(StandardTextContentProcessor.Trim::class, priority = PriorityConstant.FIRST)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
// @AnnotationMapper(Preparer::class) // bug in KAnnotationTool
public annotation class ContentTrim(
    @get:AnnotationMapper.Property(value = "priority", target = Preparer::class)
    val priority: Int = PriorityConstant.FIRST,
)

/**
 *
 * 对 [EventListenerProcessingContext.textContent] 进行前置处理, 使 `textContent` 结果始终为null。
 *
 * 不建议 [ContentTrim] 与 [ContentToNull] 一起使用, 或者说，不建议 [ContentToNull] 与其他任何操作 [EventListenerProcessingContext.textContent] 的相关内容放在一起使用。
 *
 * @see Interceptor
 * @see StandardTextContentProcessor.Null
 */
@ExperimentalSimbotApi
@Preparer(StandardTextContentProcessor.Null::class, priority = PriorityConstant.FIRST)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
// @AnnotationMapper(Preparer::class) // bug in KAnnotationTool
public annotation class ContentToNull(
    @get:AnnotationMapper.Property(value = "priority", target = Preparer::class)
    val priority: Int = PriorityConstant.FIRST,
)




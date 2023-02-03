/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
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




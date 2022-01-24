/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.annotation

import love.forte.annotationtool.AnnotationMapper
import love.forte.simboot.listener.EventListenerTextContentProcessor
import love.forte.simboot.listener.StandardTextContentProcessor
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.EventListenerProcessingContext


/**
 * 对 [EventListenerProcessingContext.textContent] 进行前置处理, 清除前后空格（如果不为null的话）。
 *
 * [ContentTrim] 是 [Interceptor] 的变体，且在默认情况下，[ContentTrim] 的优先级是 **最高** 的。
 *
 * 不建议 [ContentTrim] 与 [ContentToNull] 一起使用。
 *
 * @see Interceptor
 * @see EventListenerTextContentProcessor
 * @see StandardTextContentProcessor.Trim
 */
@ExperimentalSimbotApi
@Interceptor(type = StandardTextContentProcessor.Trim::class, priority = PriorityConstant.FIRST)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@AnnotationMapper(Interceptor::class)
public annotation class ContentTrim(
    @get:AnnotationMapper.Property(value = "priority", target = Interceptor::class)
    val priority: Int = PriorityConstant.FIRST
)

/**
 *
 * 对 [EventListenerProcessingContext.textContent] 进行前置处理, 使 `textContent` 结果始终为null。
 *
 * 不建议 [ContentTrim] 与 [ContentToNull] 一起使用, 或者说，不建议 [ContentToNull] 与其他任何操作 [EventListenerProcessingContext.textContent] 的相关内容放在一起使用。
 *
 * @see Interceptor
 * @see EventListenerTextContentProcessor
 * @see StandardTextContentProcessor.Null
 */
@ExperimentalSimbotApi
@Interceptor(type = StandardTextContentProcessor.Null::class, priority = PriorityConstant.FIRST)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@AnnotationMapper(Interceptor::class)
public annotation class ContentToNull(
    @get:AnnotationMapper.Property(value = "priority", target = Interceptor::class)
    val priority: Int = PriorityConstant.FIRST
)




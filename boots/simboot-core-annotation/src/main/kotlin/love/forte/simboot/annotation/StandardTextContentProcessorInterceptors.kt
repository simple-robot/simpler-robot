package love.forte.simboot.annotation

import love.forte.annotationtool.AnnotationMapper
import love.forte.simboot.listener.EventListenerTextContentProcessor
import love.forte.simboot.listener.StandardTextContentProcessor
import love.forte.simbot.PriorityConstant
import love.forte.simbot.annotation.SimbotExperimentalApi
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
@SimbotExperimentalApi
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
@SimbotExperimentalApi
@Interceptor(type = StandardTextContentProcessor.Null::class, priority = PriorityConstant.FIRST)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@AnnotationMapper(Interceptor::class)
public annotation class ContentToNull(
    @get:AnnotationMapper.Property(value = "priority", target = Interceptor::class)
    val priority: Int = PriorityConstant.FIRST
)




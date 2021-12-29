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




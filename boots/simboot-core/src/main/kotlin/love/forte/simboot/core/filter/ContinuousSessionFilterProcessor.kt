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

package love.forte.simboot.core.filter

import love.forte.annotationtool.AnnotationMapper
import love.forte.simboot.annotation.Filter
import love.forte.simboot.filter.FilterAnnotationProcessContext
import love.forte.simboot.filter.FilterAnnotationProcessor
import love.forte.simbot.event.ContinuousSession
import love.forte.simbot.event.ContinuousSessionContext3
import love.forte.simbot.event.EventFilter
import love.forte.simbot.event.EventListenerProcessingContext

/**
 *
 *
 *
 */
@Target(AnnotationTarget.FUNCTION)
@Filter(value = "", processor = ContinuousSessionFilterProcessor::class)
@Deprecated("Not in used")
public annotation class OnContinuousSession(
    /**
     * 在 [ContinuousSessionContext3] 中需要进行过滤的 group 与 key。
     * 格式：`${group}:${key}`, 其中，`:${key}` 的部分可以省略。
     *
     * example1:
     * ```kotlin
     * @OnContinuousSession("MY_GROUP:MY_KEY")
     * @Listener suspend fun EventProcessingContext.sessionListenerA(event: Event) {
     *      val value = getMyValueA(event) // 根据事件尝试获取一个想要进行推送的值。
     *      push("MY_GROUP", "MY_KEY", value)
     * }
     *
     * ```
     *
     * example2:
     * ```kotlin
     * @OnContinuousSession("MY_GROUP")
     * @Listener suspend fun EventProcessingContext.sessionListenerB(event: Event) {
     *      val value = getMyValueB(event) // 根据事件尝试获取一个想要进行推送的值。
     *      push("MY_GROUP", "MY_KEY", value)
     * }
     * ```
     *
     */
    @get:AnnotationMapper.Property(value = "value", target = Filter::class)
    val value: String
)


/**
 *
 * 通过 [ContinuousSession] 进行解析的过滤器。
 *
 */
public object ContinuousSessionFilterProcessor : FilterAnnotationProcessor {
    private const val GROUP_KEY_DELIMITER = ":"
    override fun process(context: FilterAnnotationProcessContext): EventFilter {
        val splitList = context.filter.value.split(GROUP_KEY_DELIMITER, limit = 2, ignoreCase = false)
        val group = splitList[0]
        val key = if (splitList.size > 1) splitList[1] else null
        return ContinuousSessionFilter(group, key)
    }
}


private class ContinuousSessionFilter(private val group: String, private val key: String?) : EventFilter {
    override suspend fun test(context: EventListenerProcessingContext): Boolean {
        // val scope = context.getAttribute(EventProcessingContext.Scope.ContinuousSession) ?: return false
        // val container = scope[group]
        // return if (key == null) container != null else container?.contains(key) == true
        return false
    }
}
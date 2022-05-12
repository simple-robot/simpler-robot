package love.forte.simboot.spring.autoconfigure

import kotlinx.coroutines.CoroutineDispatcher

/**
 * 在 `spring-boot-starter` 中作为事件调度器的容器。
 */
public open class SimbotEventDispatcherContainer(public val dispatcher: CoroutineDispatcher)

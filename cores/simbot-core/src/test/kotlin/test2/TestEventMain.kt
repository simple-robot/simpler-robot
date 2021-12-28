package test2

import kotlinx.coroutines.*
import love.forte.simbot.core.event.coreListenerManager
import love.forte.simbot.core.event.listen
import love.forte.simbot.event.*
import love.forte.simbot.randomID
import kotlin.time.Duration.Companion.seconds


suspend fun main() = coroutineScope {
    val manager = coreListenerManager { }
    manager.myListener()

    println("group event")

    launch { manager.pushIfProcessable(GroupEvent) { TestGroupEvent() } }

    delay(1000)

    println("channel event")
    manager.pushIfProcessable(ChannelEvent) { TestChannelEvent() }

    Job().join()

}


suspend fun EventListenerManager.myListener() {
    listen { processingContext: EventListenerProcessingContext, _: GroupEvent ->
        // 获取 session context, 并认为它不为null。
        val sessionContext = processingContext.getAttribute(EventProcessingContext.Scope.ContinuousSession)
            ?: error("不支持会话！")

        // 使用 waitingFor<Int>
        val num0 = sessionContext.waiting<Int>(randomID(), 50.seconds) { context, provider ->
            delay(100)
            provider.push(1)
        }

        try {
            num0.await()
        } catch (e: TimeoutCancellationException) {
            println("timeout: $e")
        } catch (e: CancellationException) {
            println("cancel: $e")
        }

        // 使用 waitingFor<Int>
        try {
            sessionContext.waitingFor<Int>(randomID(), 50) { context, provider ->
                delay(100)
                provider.push(1)
            }
        } catch (e: TimeoutCancellationException) {
            println("timeout: $e")
        } catch (e: CancellationException) {
            println("cancel: $e")
        }

        null
    }
}

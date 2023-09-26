import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.Job
import love.forte.simbot.Api4J
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.application.BotManagers
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotVerifyInfo
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 *
 * @author ForteScarlet
 */
class BotManagersTests {

    @OptIn(Api4J::class)
    @Test
    fun `bot manager get first test`() {
        assertNull(SimpleBotManagersImpl(listOf()).getFirstOrNull(SimpleBotMangerImpl::class.java))
        assertNull(SimpleBotManagersImpl(listOf(SimpleBotMangerImpl(), SimpleBotMangerImpl())).getFirstOrNull(SimpleBotMangerImpl2::class.java))
        assertNotNull(SimpleBotManagersImpl(listOf(SimpleBotMangerImpl2(), SimpleBotMangerImpl())).getFirstOrNull(SimpleBotMangerImpl::class.java))
        assertNotNull(SimpleBotManagersImpl(listOf(SimpleBotMangerImpl(), SimpleBotMangerImpl())).getFirstOrNull(BotManager::class.java))
        
    }

}

private class SimpleBotManagersImpl(list: List<BotManager<*>>) : BotManagers, List<BotManager<*>> by list {
    override fun register(botVerifyInfo: BotVerifyInfo): Bot? = null
}

private class SimpleBotMangerImpl : BotManager<Bot>() {
    private val job = Job()
    override val coroutineContext: CoroutineContext = EmptyCoroutineContext
    override fun register(verifyInfo: BotVerifyInfo): Bot {
        TODO("Not yet implemented")
    }

    override suspend fun doCancel(reason: Throwable?): Boolean = false

    override fun get(id: ID): Bot? = null

    override fun all(): List<Bot> = emptyList()

    override val component: Component
        get() = TODO("Not yet implemented")

    override suspend fun join() {
    }

    override fun invokeOnCompletion(handler: CompletionHandler) {
        job.invokeOnCompletion(handler)
    }

    override suspend fun start(): Boolean = false

    override val isStarted: Boolean by job::isCancelled
    override val isActive: Boolean by job::isActive
    override val isCancelled: Boolean by job::isCancelled

}
private class SimpleBotMangerImpl2 : BotManager<Bot>() {
    private val job = Job()
    override val coroutineContext: CoroutineContext = EmptyCoroutineContext
    override fun register(verifyInfo: BotVerifyInfo): Bot {
        TODO("Not yet implemented")
    }

    override suspend fun doCancel(reason: Throwable?): Boolean = false

    override fun get(id: ID): Bot? = null

    override fun all(): List<Bot> = emptyList()

    override val component: Component
        get() = TODO("Not yet implemented")

    override suspend fun join() {
    }

    override fun invokeOnCompletion(handler: CompletionHandler) {
        job.invokeOnCompletion(handler)
    }

    override suspend fun start(): Boolean = false

    override val isStarted: Boolean by job::isCancelled
    override val isActive: Boolean by job::isActive
    override val isCancelled: Boolean by job::isCancelled

}

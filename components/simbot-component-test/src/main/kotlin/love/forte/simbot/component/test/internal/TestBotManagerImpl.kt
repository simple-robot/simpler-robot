/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.component.test.internal

import kotlinx.coroutines.*
import love.forte.simbot.*
import love.forte.simbot.component.test.*
import love.forte.simbot.event.*
import java.util.concurrent.*
import kotlin.coroutines.*
import kotlin.coroutines.cancellation.*


/**
 *
 * @author ForteScarlet
 */
internal class TestBotManagerImpl(
    private val eventProcessor: EventProcessor,
    override val configuration: TestBotManagerConfiguration,
) : TestBotManager() {
    private val job = SupervisorJob(configuration.coroutineContext[Job])
    override val coroutineContext: CoroutineContext =
        configuration.coroutineContext.minusKey(Job) + job + CoroutineName("simbot.test.botManager")

    /**
     * bot列表。
     */
    private val bots = ConcurrentHashMap<String, TestBotImpl>(8)

    /**
     * botManager实现者自定义的close函数，
     * 例如关闭所有的BOT。
     */
    override suspend fun doCancel(reason: Throwable?): Boolean {
        if (job.isCancelled) return false

        if (reason != null) {
            job.cancel(CancellationException(reason))
            job.join()
        } else {
            job.cancelAndJoin()
        }
        return true
    }

    /**
     * 通过 [TestBotConfiguration] 注册一个 [TestBot] 实例。
     */
    override fun register(configuration: TestBotConfiguration): TestBot {
        val bot = bots.compute(configuration.id.literal) { id, old ->
            if (old != null) {
                throw BotAlreadyRegisteredException(id)
            }
            TestBotImpl(
                id = configuration.id,
                component = component,
                username = configuration.username,
                manager = this,
                eventProcessor = eventProcessor,
                parentJob = job,
                configuration = this.configuration,
                coroutineContext = coroutineContext,
                avatar = configuration.avatar,
                logger = this.configuration.apiCallLoggerFactory(configuration),
                status = configuration.status,

            )
        }
        return bot!!
    }

    /**
     * 根据Bot的ID获取一个已经注册过的 [TestBot]。
     *
     */
    override fun get(id: ID): TestBot? = bots[id.literal]

    /**
     * 获取当前管理器下的所有BOT。
     */
    override fun all(): Sequence<TestBot> = bots.values.asSequence()

    override val component: TestComponent = eventProcessor.getComponent(TestComponent.ID_VALUE) as? TestComponent ?: throw ComponentMismatchException("The component['${TestComponent.ID_VALUE}'] cannot cast to [love.forte.simbot.component.test.TestComponent]")


    /**
     * 挂起, 直到当前实例被 [cancel] 或完成.
     *
     * @see waiting
     * @see toAsync
     */
    override suspend fun join() {
        job.join()
    }

    /**
     * 当完成（或被cancel）时执行一段处理。
     */
    override fun invokeOnCompletion(handler: CompletionHandler) {
        job.invokeOnCompletion(handler)
    }

    override suspend fun start(): Boolean {
        // nothing.
        return true
    }

    /**
     * 是否已经启动过了。
     */
    override val isStarted: Boolean get() = true

    /**
     * 是否正在运行，即启动后尚未关闭。
     */
    override val isActive: Boolean get() = job.isActive

    /**
     * 是否已经被取消。
     */
    override val isCancelled: Boolean get() = job.isCancelled


}
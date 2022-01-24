/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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
 *
 */

package love.forte.simbot

import love.forte.simbot.utils.runInBlocking
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.stream.Stream
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.streams.asStream


/**
 * [OriginBotManager] 是所有[BotManager]的管理器 .
 */
public object OriginBotManager : Set<BotManager<*>> {
    private val logger = LoggerFactory.getLogger(OriginBotManager::class)
    private val lock = ReentrantReadWriteLock()
    private val managers: MutableMap<BotManager<*>, Unit> = WeakHashMap()
    private val shutdown = AtomicBoolean(false)

    @Synchronized
    @JvmOverloads
    public fun cancel(reason: Throwable? = null) {
        lock.write {
            shutdown.set(true)
            logger.debug("OriginBotManager shutdown...")
            var err: Throwable? = null
            for (manager in managers.keys.toList()) {
                kotlin.runCatching {
                    runInBlocking { manager.cancel(reason) }
                }.getOrElse { e ->
                    kotlin.runCatching {
                        val err0 = err
                        if (err0 == null) {
                            err = SimbotIllegalStateException("BotManager shutdown failed.")
                            err!!.addSuppressed(
                                SimbotIllegalStateException(
                                    "Manager $manager of component ${manager.component.name}",
                                    e
                                )
                            )
                        } else {
                            err0.addSuppressed(
                                SimbotIllegalStateException(
                                    "Manager $manager of component ${manager.component.name}",
                                    e
                                )
                            )
                        }
                    }.onFailure {
                        logger.error("Bot manager shutdown failed! ", it)
                    }
                }
            }
            logger.debug("All managed bot manager shutdown finished.")
            if (err != null) {
                logger.error("Some bot manager shutdown failed.", err)
            }
        }
    }

    private inline fun checkShutdown(message: () -> String = { "OriginBotManager has already shutdown!" }) {
        if (shutdown.get()) {
            throw IllegalStateException(message())
        }
    }

    internal fun register(manager: BotManager<*>) {
        lock.write {
            checkShutdown()
            managers[manager] = Unit
        }
    }

    internal fun remove(manager: BotManager<*>): Boolean {
        lock.write {
            return managers.remove(manager) != null
        }
    }

    @JvmSynthetic
    public fun getManagers(component: Component): Sequence<BotManager<*>> {
        lock.read {
            checkShutdown()
            return sequence {
                val iter = managers.keys.iterator()
                while (iter.hasNext()) {
                    val next: BotManager<*> = iter.next()
                    if (next.component == component) yield(next)
                    else continue
                }
            }
        }
    }

    /**
     * 返回 [Stream]. 兼容java。
     *
     * @see getManagers
     */
    @Api4J
    @JvmName("getManagers")
    public fun getManagers4J(component: Component): Stream<BotManager<*>> = getManagers(component).asStream()

    public fun getFirstManager(component: Component): BotManager<*>? = lock.read {
        checkShutdown()
        managers.keys.firstOrNull { it.component == component }
    }

    public fun getBot(id: ID, component: Component): Bot? {
        return managers.keys.firstOrNull {
            it.component == component
        }?.get(id)
    }

    //// set

    override val size: Int
        get() = managers.size

    override fun contains(element: BotManager<*>): Boolean = managers.containsKey(element)

    override fun containsAll(elements: Collection<BotManager<*>>): Boolean {
        return elements.all { contains(it) }
    }

    override fun isEmpty(): Boolean = managers.isEmpty()
    override fun iterator(): Iterator<BotManager<*>> = managers.keys.iterator()
}
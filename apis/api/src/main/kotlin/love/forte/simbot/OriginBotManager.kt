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

package love.forte.simbot

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.thread
import kotlin.concurrent.write


/**
 * [OriginBotManager] 是所有[BotManager]的管理器 .
 */
public object OriginBotManager : Set<BotManager<*>> {
    private val lock = ReentrantReadWriteLock()
    private val managers: MutableMap<BotManager<*>, Unit> = WeakHashMap()
    private val shutdown = AtomicBoolean(false)

    init {
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            lock.write {
                shutdown.set(true)
                for (key in managers.keys) {
                    runBlocking { key.cancel() }
                }
            }
        })
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

    internal fun remove(manager: BotManager<*>) {
        lock.write {
            checkShutdown()
            managers.remove(manager)
        }
    }

    public fun getManagers(component: Component): Flow<BotManager<*>> = flow {
        val iter = managers.keys.iterator()
        while (iter.hasNext()) {
            val next: BotManager<*> = iter.next()
            if (next.component == component) emit(next)
            else continue
        }
    }

    public fun getFirstManager(component: Component): BotManager<*>? =
        managers.keys.firstOrNull { it.component == component }

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
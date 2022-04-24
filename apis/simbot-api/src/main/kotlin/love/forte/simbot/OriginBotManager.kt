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
 */

package love.forte.simbot

import love.forte.simbot.OriginBotManager.cancel
import love.forte.simbot.utils.runInBlocking
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


/**
 * 所有 [BotManager] 的统一管理器.
 *
 * [OriginBotManager] 是所有 [BotManager] 实例的统一管理类，在正常情况下，
 * 所有的 [BotManager] 实现类在新建实例的时候都会将自身交由 [OriginBotManager] 进行统一管理。
 * [OriginBotManager] 内部不会持有这些manager，当一个 [BotManager] 执行了 [cancel] 或被垃圾回收后，
 * [OriginBotManager] 中将无法再获取到它。
 *
 * 如果你想要某个 [BotManager] 脱离 [OriginBotManager] 的管理，使用 [BotManager.breakAway]
 *
 * **遍历所有**:
 * ```kotlin
 * OriginBotManager.forEach { manager ->
 *  // do...
 *  }
 * ```
 *
 * ## ⚠ 谨慎使用
 * [OriginBotManager] 是脱离环境的 **全局性** 功能，当你的整个应用中存在多个环境时，使用它则很可能造成各种混乱。
 * [BotManager] 作为属性存在于很多对象中，你应当优先考虑使用那些明确的api（例如 [Bot.manager]、[love.forte.simbot.event.Event.bot] 等），
 * 而对于全局性的 [OriginBotManager], 是最后的选择。
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
@FragileSimbotApi
public object OriginBotManager : Set<BotManager<*>> {
    private val logger = LoggerFactory.getLogger(OriginBotManager::class)
    private val lock = ReentrantReadWriteLock()
    private val managers: MutableMap<BotManager<*>, Unit> = WeakHashMap()

    @Volatile
    private var shutdown = false

    /**
     * 关闭 [OriginBotManager]. 如果没有特殊需求，此方法不需要也不应该被手动调用。[cancel] 通常使用在 shutdown hook 等系统终止的相关回调中。
     *
     * 当 [OriginBotManager] 被关闭后，将无法再使用 [register]、[getManagers]、[getFirstManager]. 因此你无法再获取任何 [BotManager], 也无法再构建任何新的 [BotManager].
     *
     */
    @JvmOverloads
    public fun cancel(reason: Throwable? = null) {
        lock.write {
            if (shutdown) {
                logger.debug("OriginBotManager has been shutdown, reject this call.")
                return
            }

            shutdown = true
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
                                    "Manager $manager of component ${manager.component.id}",
                                    e
                                )
                            )
                        } else {
                            err0.addSuppressed(
                                SimbotIllegalStateException(
                                    "Manager $manager of component ${manager.component.id}",
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
        if (shutdown) {
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

    /**
     * 通过一个 [Component] 获取对应组件下的BotManager序列。
     *
     * 例如, 获取某组件下所有bot的所有好友：
     * ```kotlin
     * OriginBotManager
     * .getManagers("component.id".ID)
     * .flatMap { manager -> manager.all() }
     * .asFlow() // Bot.friends() 是Flow类型
     * .flatMapConcat { bot -> bot.friends() }
     * ```
     *
     * ## getManagers()?
     *
     * 如果你在寻找不需要参数的 `getManagers()` 函数，请停下。[OriginBotManager] 其本身作为一个 [Set] 的实现即可以直接代表所有的 [BotManager]。
     *
     * 假如你需要遍历所有，那么：
     * ```kotlin
     * OriginBotManager.forEach { manager ->
     *      // do...
     * }
     * ```
     *
     */
    public fun getManagers(component: Component): List<BotManager<*>> {
        lock.read {
            checkShutdown()
            return filter { it.component == component }
        }
    }

    /**
     * 根据指定ID查询组件ID与其相等的 [BotManager].
     */
    public fun getManagers(componentId: ID): List<BotManager<*>> {
        lock.read {
            checkShutdown()
            return filter { it.component.id == componentId }
        }
    }


    /**
     * 获取某个指定组件下的第一个能够得到的 [BotManager], 如果找不到则返回null。
     *
     * @param component [Component]
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun getFirstManager(component: Component): BotManager<*>? = lock.read {
        checkShutdown()
        managers.keys.firstOrNull { it.component == component }
    }

    /**
     * 获取某个指定组件下的第一个能够得到的 [BotManager], 如果找不到则返回null。
     *
     * @param componentId component的id。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun getFirstManager(componentId: ID): BotManager<*>? = lock.read {
        checkShutdown()
        managers.keys.firstOrNull { it.component.id == componentId }
    }

    /**
     * 根据一个Bot的id以及对应的组件对象来得到一个此组件下指定ID的bot。如果manager不存在或者没有这个id的bot，则会得到null。
     *
     * @param id Bot的id
     * @param component [Component]
     */
    public fun getBot(id: ID, component: Component): Bot? {
        return managers.keys.firstOrNull {
            it.component == component
        }?.get(id)
    }

    /**
     * 根据一个Bot的id以及对应的组件ID来得到一个此组件下指定ID的bot。如果manager不存在或者没有这个id的bot，则会得到null。
     *
     * 如果提供的 [组件ID][componentId] 为null，则会尝试寻找第一个id匹配的bot。
     *
     * @param id Bot的id
     * @param componentId [Component.id]
     */
    @JvmOverloads
    public fun getBot(id: ID, componentId: ID? = null): Bot? {
        if (componentId == null) {
            val managers = managers.keys
            for (manager in managers) {
                val bot = manager.get(id)
                if (bot != null) return bot
            }
            return null
        }

        return managers.keys.firstOrNull {
            it.component.id == componentId
        }?.get(id)
    }


    /**
     * 尝试获取任意一个 [BotManager] 下的任意一个 [Bot]。如果当前元素为空则会得到null。
     *
     * @param component 可以提供一个组件信息。默认为null
     */
    @JvmOverloads
    public fun getAnyBot(component: Component? = null): Bot? {
        fun Iterable<BotManager<*>>.firstBotOrNull() = firstOrNull()?.all()?.firstOrNull()

        return (component?.let { getManagers(component) } ?: this).firstBotOrNull()
    }

    /**
     * 尝试获取任意一个manager。如果当前元素为空则会得到null。
     *
     * @param component 可以提供一个组件信息。默认为null
     */
    @JvmOverloads
    public fun getAny(component: Component? = null): BotManager<*>? {
        if (component == null) return firstOrNull()

        return getManagers(component).firstOrNull()
    }


    //// set

    /**
     * 得到当前被管理的manager数量。
     */
    override val size: Int
        get() = managers.size

    /**
     * 判断是否存在某个BotManager。
     */
    override fun contains(element: BotManager<*>): Boolean = managers.containsKey(element)

    /**
     * 判断是否包含提供的所有manager。
     */
    override fun containsAll(elements: Collection<BotManager<*>>): Boolean {
        return elements.all { contains(it) }
    }

    /**
     * 是否为空
     */
    override fun isEmpty(): Boolean = managers.isEmpty()

    /**
     * 获取迭代器。
     */
    override fun iterator(): Iterator<BotManager<*>> = managers.keys.iterator()
}
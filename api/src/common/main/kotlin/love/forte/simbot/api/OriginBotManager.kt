package love.forte.simbot.api

import love.forte.simbot.api.OriginBotManager.parentManager
import love.forte.simbot.api.utils.computeIfAbsent
import love.forte.simbot.api.utils.concurrentCollection
import love.forte.simbot.api.utils.concurrentMap


/**
 * 顶层的 [BotManager], 是所有BotManager的最终 [parentManager] .
 */
public object OriginBotManager : BotManager<Bot>() {
    override val parentManager: BotManager<*>? get() = null
    override fun get(id: String): Bot? = null
}
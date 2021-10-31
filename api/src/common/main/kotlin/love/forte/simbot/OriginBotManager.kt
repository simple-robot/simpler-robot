package love.forte.simbot

import love.forte.simbot.OriginBotManager.parentManager


/**
 * 顶层的 [BotManager], 是所有BotManager的最终 [parentManager] .
 */
public object OriginBotManager : BotManager<Bot>() {
    override val parentManager: BotManager<*>? get() = null
    override fun get(id: String): Bot? = null
}
package love.forte.simbot.api


/**
 * 顶层的 [BotManager], 是所有BotManager的最终 [parentManager] .
 */
public object OriginBotManager : BotManager<Bot>(), BotManager.Key {
    override val id: String
        get() = "origin"

    override val key: Key
        get() = this

    private val managers: MutableMap<Key, MutableCollection<BotManager<out Bot>>> = mutableMapOf()

    // Return type must be specified in explicit API mode

    override val parentManager: BotManager<*>? get() = null
    override fun get(id: String): Bot? = TODO()

    internal fun register(key: Key, botManager: BotManager<out Bot>) {
        if (botManager != this) {
            managers // .merge TODO
        }
    }

    public val allManagers: Collection<BotManager<out Bot>> get() = managers.values.flatten()

}
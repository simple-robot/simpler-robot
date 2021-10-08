package love.forte.simbot.api


/**
 *
 *  Bot 管理器。
 *
 * @author ForteScarlet
 */
public sealed class BotManager<B : Bot> {
    /**
     * Manager或许有一个父类管理器。
     */
    public abstract val parentManager: BotManager<*>?

    /**
     * 尝试通过ID获取一个 [Bot].
     */
    public abstract operator fun get(id: String): B?

    /**
     * 必须有个唯一Key。
     */
    public abstract val key: Key

    /**
     * [BotManager]'s [Key] for [OriginBotManager].
     *
     */
    public interface Key {
        public val id: String
    }
}




public abstract class BaseBotManager<B: Bot>(
    override val parentManager: BotManager<*> = OriginBotManager,
    final override val key: Key
) : BotManager<B>() {
    init {
        OriginBotManager.register(key, this)
    }
}

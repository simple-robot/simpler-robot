package love.forte.simbot

import kotlin.jvm.JvmOverloads


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


}// prototypes

/**
 * 基础的 [BotManager] 抽象类。
 *
 */
public abstract class BaseBotManager<B : Bot> @JvmOverloads constructor(
    override val parentManager: BotManager<*> = OriginBotManager,
) : BotManager<B>(), ComponentContainer

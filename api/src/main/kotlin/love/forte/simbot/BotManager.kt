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

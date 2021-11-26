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


/**
 *
 * [Bot] 管理器。
 * [BotManager] 应当是 获取、注册 [Bot] 的唯一公开途径，
 * 所有 [BotManager] 均由 [OriginBotManager] 进行管理。
 *
 * @author ForteScarlet
 */

public abstract class BotManager<B : Bot> : ComponentContainer {
    init {
        @Suppress("LeakingThis")
        OriginBotManager.register(this)
    }

    /**
     * 执行关闭操作。
     * [doCancel] 为当前manager的自定义管理，当前manager关闭后，将会从 [OriginBotManager] 剔除自己。
     */
    @JvmSynthetic
    public suspend fun cancel() {
        // remove first.
        OriginBotManager.remove(this)
        doCancel()
    }

    /**
     * botManager实现者自定义的close函数，
     * 例如关闭所有的BOT。
     */
    protected abstract suspend fun doCancel()

    /**
     * 根据通用配置信息注册一个BOT。
     * 此信息是从 `.bot` 配置文件中读取而来的 Properties格式文件。
     *
     * 可以考虑直接通过properties序列化进行。
     *
     * 对于任意一个组件，其注册方式可能存在其他任何可能的方式，
     * 但是 [BotManager] 要求实现 [register] 来为 `boot` 模块的自动注册服务。
     *
     *
     */
    public abstract suspend fun register(properties: Map<String, String>): Bot

    /**
     * 根据Bot的ID获取一个已经注册过的 [Bot]。
     *
     * [Bot] 通过 [BotManager] 进行注册，但是不通过 [BotManager] 进行销毁，而是通过 [Bot.cancel] 进行关闭。
     * 当 [Bot] 关闭后，[BotManager] 中不应能够再获取到此Bot。
     *
     */
    public abstract fun get(id: ID): B?

}

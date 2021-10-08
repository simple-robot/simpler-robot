package love.forte.simbot.api

import love.forte.simbot.api.definition.User


/**
 *
 * 一个 [Bot]. 同时, [Bot] 也属于一个账户 [User]。
 *
 * @author ForteScarlet
 */
public interface Bot : User {
    /**
     * 每个bot都肯定会由一个 [BotManager] 进行管理。
     *
     */
    public val manager: BotManager<Bot>

    /**
     * 每个Bot都有一个所属组件。
     *
     */
    public val component: Component

    // other..?

}
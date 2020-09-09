package love.forte.simbot.common.api.messages.results

import love.forte.simbot.common.api.messages.containers.BotInfo


/**
 *
 * bot的基础登录信息，以及一个可能存在的 **等级** 信息。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
interface LoginInfo: BotInfo {
    /**
     * 等级信息。如果无法获取，则默认值为-1
     */
    val level: Int

    /**
     * 用于展示一个等级信息的。例如当level不支持获取的时候，返回一个 "无法获取"
     */
    fun showLevel(): String
}
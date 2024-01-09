/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.bot

import love.forte.simbot.plugin.Plugin

/**
 * 一个与 [Bot] 相关的插件。是一个具有特殊意义的插件类型。
 *
 * [BotPlugin] 本身不提供任何API，请参考 [BotManager] 或 [AutoConfigurableBotPlugin]。
 * 实现者通常会提供个性化的注册API来支持注册能力。
 *
 * 如果希望 [BotPlugin] 能支持在 `quantcat` 模块下多组件环境中自动解析配置文件并加载，
 * 则需要考虑实现 [AutoConfigurableBotPlugin]，仅实现 [BotPlugin] 无法支持自动注册 [Bot]。
 *
 * @author ForteScarlet
 * @see BotManager
 * @see AutoConfigurableBotPlugin
 *
 */
public interface BotPlugin : Plugin

/**
 * [BotPlugin] 的标准扩展类型，
 * 允许根据其提供的 API 检测和使用 [SerializableBotConfiguration] 进行配置。
 *
 * [SerializableBotConfiguration] 通常由 `quantcat` 或其他集成环境在多组件模式下自动加载并解析，
 * 因此无法明确具体类型，需要先通过 [configurable] 校验可用性，而用使用 [register] 注册。
 */
public interface AutoConfigurableBotPlugin : BotPlugin {
    /**
     * 检测提供的 [configuration] 是否能够应用于 [register] 中。通常是一种类型检测。
     * 如果 [configurable] 结果为 `true`，则使用此 [configuration] 执行 [register]
     * 时不应出现 [UnsupportedBotConfigurationException] 异常。
     */
    public fun configurable(configuration: SerializableBotConfiguration): Boolean

    /**
     * 使用一个 [configuration] 注册并得到 [Bot]。
     *
     * 如果是 [configurable] 会得到 `true` 的 [configuration]
     * 则不应引发 [UnsupportedBotConfigurationException] 异常。
     *
     * @throws BotRegisterFailureException 构建 [Bot] 过程失败
     * @throws UnsupportedBotConfigurationException 如果提供的 [configuration] 不符合预期
     */
    public fun register(configuration: SerializableBotConfiguration): Bot
}

/**
 * 尝试使用 [register][AutoConfigurableBotPlugin.register] 注册并得到一个 [Bot]。
 * 如果 [configurable][AutoConfigurableBotPlugin.configurable]
 * 校验通过则通过 [register][AutoConfigurableBotPlugin.register] 注册得到 [Bot]，
 * 否则得到 `null`。
 *
 * @throws BotRegisterFailureException 构建 [Bot] 过程失败
 */
public fun AutoConfigurableBotPlugin.tryRegister(configuration: SerializableBotConfiguration): Bot? {
    if (configurable(configuration)) {
        return register(configuration)
    }

    return null
}

/**
 * 尝试使用 [register][AutoConfigurableBotPlugin.register] 注册并得到一个 [Bot]。
 * 如果 [configurable][AutoConfigurableBotPlugin.configurable]
 * 校验通过则通过 [register][AutoConfigurableBotPlugin.register] 注册得到 [Bot]，
 * 并用于执行 [block]。
 *
 * @throws BotRegisterFailureException 构建 [Bot] 过程失败
 */
public inline fun <T> AutoConfigurableBotPlugin.tryRegister(
    configuration: SerializableBotConfiguration,
    block: (Bot) -> T
): T? {
    return tryRegister(configuration)?.let(block)
}

/**
 * @see BotPlugin
 * @see AutoConfigurableBotPlugin
 */
public open class UnsupportedBotConfigurationException : IllegalArgumentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * @see BotPlugin
 * @see AutoConfigurableBotPlugin
 */
public open class BotRegisterFailureException : BotException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

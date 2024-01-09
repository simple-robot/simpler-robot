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

package love.forte.simbot.spring

import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.SerializableBotConfiguration

/**
 * 当解析函数为事件处理器时，参数中出现了多个不兼容的事件类型时的异常。
 */
public open class MultipleIncompatibleTypesEventException : IllegalArgumentException {
    public constructor() : super()
    public constructor(s: String?) : super(s)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * 当自动扫描的bot的配置文件加载失败时（找不到文件、无法读取、无法解析为 [SerializableBotConfiguration] 等）。
 *
 */
public open class BotConfigResourceLoadOnFailureException : IllegalStateException {
    public constructor() : super()
    public constructor(s: String?) : super(s)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * 如果没有匹配的 [BotManager] 可供注册。
 */
public open class MismatchConfigurableBotManagerException(s: String?) : NoSuchElementException(s)

/**
 * 当 Bot 自动启动时出现了错误并失败了
 *
 */
public open class BotAutoStartOnFailureException : IllegalStateException {
    public constructor() : super()
    public constructor(s: String?) : super(s)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/*
 *     Copyright (c) 2025-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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


/**
 * 一个拥有配置信息的 [Bot]。
 * 配置信息的具体类型由实现者决定。
 *
 * 配置信息类可能是一个可变类型，当使用它构建 [Bot] 后，它理应不再被修改。
 * 如果实现了 [InitializableBot]，则直到 [init][InitializableBot.init] 调用前，配置信息 [configuration] 中的信息可以被修改。
 *
 * 配置类的内容也许在上述要求“不可修改”的情况下进行修改时不会产生异常（例如在 [init][InitializableBot.init]后继续修改），
 * 但是它的修改可能不会生效，或可能引发任何不可预知的错误或埋下隐患。
 *
 * ## 特殊含义
 * [ConfigurableBot] 具有特殊含义，可能会在部分场景被判断并有特殊作用。
 *
 * @see Bot
 * @see InitializableBot
 *
 * @since 4.13.0
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(InheritanceBotApi::class)
public interface ConfigurableBot : Bot {
    /**
     * 当前 Bot 持有的配置信息。
     */
    public val configuration: Any
}

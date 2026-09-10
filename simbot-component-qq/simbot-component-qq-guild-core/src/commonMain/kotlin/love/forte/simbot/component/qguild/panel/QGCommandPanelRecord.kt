/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.panel

import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.qguild.model.panel.CommandPanel
import love.forte.simbot.qguild.model.panel.CommandPanelRecord
import love.forte.simbot.qguild.model.panel.CommandPanelScope
import love.forte.simbot.qguild.model.panel.CommandPanelTargetType
import kotlin.jvm.JvmExposeBoxed

/**
 * 指令面板记录信息。
 *
 * 它的内容信息是获取时基于 [CommandPanelRecord] 的瞬时**快照**。
 *
 * @since 4.7.0
 */
@OptIn(ExperimentalStdlibApi::class)
@SubclassOptInRequired(InternalForInheritanceQGPanelApi::class)
public abstract class QGCommandPanelRecord : QGObjectiveContainer<CommandPanelRecord>, QGCommandPanelHandle {
    /**
     * 面板生效场景的原始值。
     */
    public val scopeValue: String
        get() = source.scope.scope

    /**
     * 面板生效场景的 scope 。
     *
     * @since 5.0
     */
    @get:JvmExposeBoxed
    public val scope: CommandPanelScope
        get() = source.scope

    /**
     * 面板生效范围的原始值。
     */
    public val targetTypeValue: String
        get() = source.targetType.value

    /**
     * 面板生效范围。
     *
     * @since 5.0
     */
    @get:JvmExposeBoxed
    public val targetType: CommandPanelTargetType
        get() = source.targetType

    /**
     * 面板配置。
     */
    public val panel: CommandPanel
        get() = source.panel

    /**
     * 面板版本。
     */
    public val version: Int?
        get() = source.version
}

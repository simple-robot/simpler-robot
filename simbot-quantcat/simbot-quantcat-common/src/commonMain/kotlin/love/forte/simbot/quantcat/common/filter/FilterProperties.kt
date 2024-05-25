/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.quantcat.common.filter


/**
 * 参考注解 `@Filter` 中的属性说明。
 *
 * @author ForteScarlet
 */
public data class FilterProperties(
    public val value: String,
    public val mode: FilterMode,
    public val priority: Int,
    public val targets: List<FilterTargetsProperties>,
    public val ifNullPass: Boolean,
    public val matchType: MatchType,
)

/**
 * 参考注解 `@Filter.Targets` 中的属性说明。
 *
 * @author ForteScarlet
 */
public data class FilterTargetsProperties(
    val components: List<String>,
    val bots: List<String>,
    val actors: List<String>,
    val authors: List<String>,
    val chatRooms: List<String>,
    val organizations: List<String>,
    val groups: List<String>,
    val guilds: List<String>,
    val contacts: List<String>,
    val ats: List<String>,
    val atBot: Boolean,
)

/**
 * 参考注解 `@FilterValue` 中的属性说明。
 *
 * @author ForteScarlet
 */
public data class FilterValueProperties(
    val value: String,
    val required: Boolean = true
)

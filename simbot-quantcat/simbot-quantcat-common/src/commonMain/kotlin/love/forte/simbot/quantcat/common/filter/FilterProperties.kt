/*
 *     Copyright (c) 2024-2025. ForteScarlet.
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
 * Filter properties 相关的类型的构造器应当仅由内部调用，对外不保证兼容性。
 * @since 4.15.0
 */
@Retention(AnnotationRetention.RUNTIME)
@RequiresOptIn(message = "Filter properties 相关的类型的构造器应当仅由内部调用，对外不保证兼容性。")
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
public annotation class FilterPropertiesConstructor

/**
 * 参考注解 `@Filter` 中的属性说明。
 *
 * @author ForteScarlet
 */
public data class FilterProperties @FilterPropertiesConstructor public constructor(
    public val value: String,
    public val mode: FilterMode,
    public val priority: Int,
    public val targets: List<FilterTargetsProperties>,
    public val ifNullPass: Boolean,
    public val matchType: MatchType,
    public val regexOptions: Set<RegexOption>
) {
    /**
     * 构造。
     *
     * 与 4.15.0 之前的构造兼容。
     * @since 4.15.0
     */
    @FilterPropertiesConstructor
    public constructor(
        value: String,
        mode: FilterMode,
        priority: Int,
        targets: List<FilterTargetsProperties>,
        ifNullPass: Boolean,
        matchType: MatchType,
    ) : this(value, mode, priority, targets, ifNullPass, matchType, emptySet())
}

/**
 * 参考注解 `@Filter.Targets` 中的属性说明。
 *
 * @author ForteScarlet
 */
public data class FilterTargetsProperties @FilterPropertiesConstructor public constructor(
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

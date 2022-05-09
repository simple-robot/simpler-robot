/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simboot.filter


/**
 * Annotation data for [love.forte.simboot.annotation.Filter].
 *
 * @param value same as [love.forte.simboot.annotation.Filter].value
 * @param matchType same as [love.forte.simboot.annotation.Filter].matchType
 * @param target same as [love.forte.simboot.annotation.Filter].target
 * @param and same as [love.forte.simboot.annotation.Filter].and
 * @param or same as [love.forte.simboot.annotation.Filter].or
 * @param processor same as [love.forte.simboot.annotation.Filter].processor
 * @param source [FilterData] 注解的 *源*。大多数情况下代表此注解所在的 [kotlin.reflect.KAnnotatedElement] 或 [java.lang.reflect.AnnotatedElement]。
 * 并不一定存在真实的源，当 [FilterData] 是通过手动构建等情况得到的，则无法确定 [source] 为何。
 *
 * @see love.forte.simboot.annotation.Filter
 */
public data class FilterData(
    val source: Any?,
    val value: String,
    val ifNullPass: Boolean,
    val matchType: MatchType = MatchType.REGEX_MATCHES,
    val target: TargetFilterData = TargetFilterData(source = source),
    val and: FiltersData = FiltersData(source = source),
    val or: FiltersData = FiltersData(source = source)
)

/**
 * Annotation data for [love.forte.simboot.annotation.TargetFilter].
 *
 * @param components same as [love.forte.simboot.annotation.TargetFilter].components
 * @param bots same as [love.forte.simboot.annotation.TargetFilter].bots
 * @param authors same as [love.forte.simboot.annotation.TargetFilter].authors
 * @param groups same as [love.forte.simboot.annotation.TargetFilter].groups
 * @param channels same as [love.forte.simboot.annotation.TargetFilter].channels
 * @param guilds same as [love.forte.simboot.annotation.TargetFilter].guilds
 * @param atBot same as [love.forte.simboot.annotation.TargetFilter].atBot
 */
public data class TargetFilterData(
    val source: Any?,
    val components: List<String> = emptyList(),
    val bots: List<String> = emptyList(),
    val authors: List<String> = emptyList(),
    val groups: List<String> = emptyList(),
    val channels: List<String> = emptyList(),
    val guilds: List<String> = emptyList(),
    val atBot: Boolean = false
)

/**
 * Annotation data for [love.forte.simboot.annotation.Filters].
 *
 * @param value same as [love.forte.simboot.annotation.Filters].value
 * @param multiMatchType same as [love.forte.simboot.annotation.Filters].multiMatchType
 * @param processor same as [love.forte.simboot.annotation.Filters].processor
 *
 * @param source [FiltersData] 的*源*, 大多数情况下代表此注解所在的 [kotlin.reflect.KAnnotatedElement] 或 [java.lang.reflect.AnnotatedElement]。
 * 并不一定存在真实的源，当 [FiltersData] 是通过手动构建等情况得到的，则无法确定 [source] 为何。
 */
public data class FiltersData(
    val source: Any?,
    val value: List<FilterData> = emptyList(),
    val multiMatchType: MultiFilterMatchType = MultiFilterMatchType.ANY,
)







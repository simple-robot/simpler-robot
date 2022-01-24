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
 *
 */

package love.forte.simbot.provide


/**
 *
 * 一个 **提供者**.
 *
 * 对于各种[对象][love.forte.simbot.definition.Objectives]，例如 [love.forte.simbot.Bot] 或者 [love.forte.simbot.definition.Organization],
 *
 * 除了它们自身约束的属性内容以外 （例如一个 [组织][love.forte.simbot.definition.Organization] 肯定含有 [组织成员][love.forte.simbot.definition.Organization.members]），
 * 它们在不同平台组件上很有可能存在更多其他的额外信息，并且这些额外信息虽然不是人人都有，但是十分常见。比如对于一个群成员，他有可能存在一些 “标签” 或 “头衔”.
 *
 * [Provider] 定义这些常见属性的提供者接口，如果在实现中存在极为相似的可提供属性，优先考虑实现此接口下的内容。
 *
 * @author ForteScarlet
 */
public interface Provider // ?
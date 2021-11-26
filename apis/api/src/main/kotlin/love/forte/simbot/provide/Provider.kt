/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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
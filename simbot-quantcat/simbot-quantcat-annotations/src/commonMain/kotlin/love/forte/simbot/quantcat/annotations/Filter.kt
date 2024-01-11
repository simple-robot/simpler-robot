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

package love.forte.simbot.quantcat.annotations

import love.forte.simbot.common.PriorityConstant
import love.forte.simbot.quantcat.annotations.Filter.Targets
import love.forte.simbot.quantcat.common.filter.*

/**
 * 与 [@Listener][Listener] 配合使用，标记为对当前事件处理器的基础属性过滤器。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
public annotation class Filter(
    /**
     * 基于一定规则，对 **消息事件** 的 [**纯文本内容**][love.forte.simbot.message.MessageContent.plainText] 进行逻辑匹配。
     *
     * ## 参数提取
     *
     * 当 [matchType] 为正则相关的匹配时（例如 [MatchType.REGEX_MATCHES]、[MatchType.REGEX_CONTAINS] 等），
     * 可以通过占位符 `{{name[,regex]}}` （例如 `age:{{age,\\d+}}`、`name:{{name}}`）
     * 或正则的 name group (参考:
     * [regular-expressions: Named Capturing Groups and Backreferences](https://www.regular-expressions.info/named.html)、
     * [Java Pattern: named-capturing group](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) 或其他相关网站)
     * `(?<name>group)` （例如 `age:(?<age>\\d+)`、`name:(?<name>.+)`）
     * 来提取某个匹配到的变量（通过注解 [FilterValue] 标记参数获取）。
     * 其中，占位符的形式 `{{...}}` 会在解析时转化为正则 name group 的形式，其二者的最终原理是相同的，最终基于 [Regex] 实现。
     */
    val value: String = "",
    /**
     * [Filter] 所产生的“过滤器”的实现模式。默认为注入到事件处理器逻辑之前。
     */
    val mode: FilterMode = FilterMode.IN_LISTENER,
    /**
     * 优先级。
     * 当 [mode]
     * 为 [FilterMode.INTERCEPTOR]
     * 时代表其作为拦截器注册时的优先级。
     */
    val priority: Int = PriorityConstant.DEFAULT,
    /**
     * 针对部分特定目标的过滤匹配。
     *
     * 只能提供 0-1 个 [Targets] 。
     * 当提供多个 [Targets] 时，只会取第一个元素的值。
     */
    val targets: Array<Targets> = [],
    /**
     * 当 [value] 对消息事件进行匹配时，
     * 如果消息的 [纯文本内容][love.forte.simbot.message.MessageContent.plainText] 为 `null`，
     * 是否直接放行。如果为 `true`, 则纯文本内容为 `null` 的时候视为匹配通过，反之则为匹配失败。默认为 `false`。
     * 此参数只有当 `value` 不为空的时候有效。
     *
     */
    val ifNullPass: Boolean = false,
    // 这并不仅仅局限于消息事件, 而生效与所有的事件类型。而对于那些本身即不是消息事件的事件来说, textContent 默认为null。 ?

    /**
     * 针对匹配目标所使用的匹配规则。
     * 默认情况下使用 [正则完全匹配][MatchType.REGEX_MATCHES].
     */
    val matchType: MatchType = MatchType.REGEX_MATCHES

    // TODO by?

) {

    /**
     * 针对部分特定目标的过滤匹配。
     */
    @Retention(AnnotationRetention.SOURCE)
    @Target(allowedTargets = [])
    public annotation class Targets(
        /**
         * 对 [Component][love.forte.simbot.component.Component] 进行匹配。
         * 如果事件为 [ComponentEvent][love.forte.simbot.event.ComponentEvent],
         * 则只有 [component.id][love.forte.simbot.component.Component.id] 在此列表中时才会放行。
         *
         */
        val components: Array<String> = [],
        /**
         * 对 [Bot][love.forte.simbot.bot.Bot] 进行匹配。
         * 如果事件为 [BotEvent][love.forte.simbot.event.BotEvent],
         * 则只有 [Bot.id][love.forte.simbot.bot.Bot.id] 在此列表中时才会放行。
         *
         * ```kotlin
         * event.bot.id in bots
         * ```
         */
        val bots: Array<String> = [],

        /**
         * 对 [Actor][love.forte.simbot.definition.Actor] 进行匹配。
         * 如果事件为 [ActorEvent][love.forte.simbot.event.ActorEvent],
         * 则只有 [Actor.id][love.forte.simbot.definition.Actor.id] 在此列表中时才会放行。
         *
         * ```kotlin
         * event.content().id in actors
         * ```
         *
         */
        val actors: Array<String> = [],

        /**
         * 对消息发送者进行匹配。
         * 如果事件为 [MessageEvent][love.forte.simbot.event.MessageEvent]，
         * 则只有 [MessageEvent.authorId][love.forte.simbot.event.MessageEvent.authorId]
         * 在此列表中才会放行。
         *
         * ```kotlin
         * event.authorId in authors
         * ```
         */
        val authors: Array<String> = [],

        /**
         * 对事件的 [ChatRoom][love.forte.simbot.definition.ChatRoom] 进行匹配。
         * 如果事件为 [ChatRoomEvent][love.forte.simbot.event.ChatRoomEvent]，
         * 则只有 [ChatRoomEvent.content.id][love.forte.simbot.definition.ChatRoom.id]
         * 在此列表中才会放行。
         *
         * ```kotlin
         * event.content.id in chatRooms
         * ```
         */
        val chatRooms: Array<String> = [],

        /**
         * 对事件的 [Organization][love.forte.simbot.definition.Organization] 进行匹配。
         * 如果事件为 [OrganizationEvent][love.forte.simbot.event.OrganizationEvent]，
         * 则只有 [OrganizationEvent.content.id][love.forte.simbot.definition.Organization.id]
         * 在此列表中才会放行。
         *
         * ```kotlin
         * event.content.id in organizations
         * ```
         */
        val organizations: Array<String> = [],

        /**
         * 对事件的 [ChatGroup][love.forte.simbot.definition.ChatGroup] 进行匹配。
         * 如果事件为 [ChatGroupEvent][love.forte.simbot.event.ChatGroupEvent]，
         * 则只有 [ChatGroupEvent.content.id][love.forte.simbot.definition.ChatGroup.id]
         * 在此列表中才会放行。
         *
         * ```kotlin
         * event.content.id in groups
         * ```
         */
        val groups: Array<String> = [],

        /**
         * 对事件的 [Guild][love.forte.simbot.definition.Guild] 进行匹配。
         * 如果事件为 [GuildEvent][love.forte.simbot.event.GuildEvent]，
         * 则只有 [GuildEvent.content.id][love.forte.simbot.definition.Guild.id]
         * 在此列表中才会放行。
         *
         * ```kotlin
         * event.content.id in guilds
         * ```
         */
        val guilds: Array<String> = [],

        /**
         * 对事件的 [Contact][love.forte.simbot.definition.Contact] 进行匹配。
         * 如果事件为 [ContactEvent][love.forte.simbot.event.ContactEvent]，
         * 则只有 [ContactEvent.content.id][love.forte.simbot.definition.Contact.id]
         * 在此列表中才会放行。
         *
         * ```kotlin
         * event.content.id in contacts
         * ```
         */
        val contacts: Array<String> = [],

        // messages

        /**
         * 对消息事件中 [At][love.forte.simbot.message.At] 进行匹配。
         * 如果事件为 [MessageEvent][love.forte.simbot.event.MessageEvent]，
         * 则只有 [MessageEvent.messageContent.messages][love.forte.simbot.message.MessageContent.messages]
         * 中存在 [At][love.forte.simbot.message.At] 消息且包含下述 **任意** at 目标时才会放行。
         *
         * ```kotlin
         * event.messageContent.messages.any { m -> m is At && m.id in atAny }
         * ```
         */
        val ats: Array<String> = [],

        /**
         * 对消息事件中 [At][love.forte.simbot.message.At] 进行匹配。
         * 如果事件为 [MessageEvent][love.forte.simbot.event.MessageEvent]，
         * 则只有 [MessageEvent.messageContent.messages][love.forte.simbot.message.MessageContent.messages]
         * 中存在 [At][love.forte.simbot.message.At] 消息且 id 属于事件 `bot` 时才会放行。
         *
         * ```kotlin
         * event.messageContent.messages.any { m -> m is At && event.bot.isMe(m.id) }
         * ```
         */
        val atBot: Boolean = false,

        // val explicitTypeCheck: Boolean = false,
    ) {
        public companion object {
            /**
             * [Filter.Targets] 中的“非”前缀。
             *
             */
            public const val NON_PREFIX: String = "!" // TODO
        }
    }
}

public fun Filter.toProperties(): FilterProperties =
    FilterProperties(
        value = value,
        mode = mode,
        priority = priority,
        targets = targets.map { it.toProperties() },
        ifNullPass = ifNullPass,
        matchType = matchType,
    )

public fun Filter.Targets.toProperties(): FilterTargetsProperties =
    FilterTargetsProperties(
        components = components.toList(),
        bots = bots.toList(),
        actors = actors.toList(),
        authors = authors.toList(),
        chatRooms = chatRooms.toList(),
        organizations = organizations.toList(),
        groups = groups.toList(),
        guilds = guilds.toList(),
        contacts = contacts.toList(),
        ats = ats.toList(),
        atBot = atBot,
    )


/**
 * 配合 [Filter] 使用。针对多个 [Filter] 之间的协同配置。
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
public annotation class MultiFilter(
    /**
     * 多个过滤器之间的匹配策略。
     * 策略只会对 [FilterMode.IN_LISTENER] 模式的过滤器配置生效。
     */
    val matchType: MultiFilterMatchType
)

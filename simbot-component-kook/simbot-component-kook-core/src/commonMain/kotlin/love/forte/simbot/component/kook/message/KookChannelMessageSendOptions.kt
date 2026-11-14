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

package love.forte.simbot.component.kook.message

import love.forte.simbot.ability.SendOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal

/**
 * 频道消息发送的单项协议选项。
 *
 * 同类选项至多出现一次，重复时会抛出异常。
 * 消息内的引用或临时目标元素仍按原有消息转换规则覆盖默认值。
 * 用于原生请求时，已指定的选项可覆盖请求中的对应参数。
 *
 * @since 5.0
 */
public sealed class KookChannelMessageSendOption : KookMessageSendOption, KookMessageReplyOption {

    /**
     * 引用消息的 ID。
     */
    public class Quote internal constructor(public val id: ID) : KookChannelMessageSendOption() {
        override fun equals(other: Any?): Boolean =
            this === other || other is Quote && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "Quote(id=$id)"
    }

    /**
     * 平台原样返回的 nonce。
     */
    public class Nonce internal constructor(public val nonce: String) : KookChannelMessageSendOption() {
        override fun equals(other: Any?): Boolean =
            this === other || other is Nonce && nonce == other.nonce

        override fun hashCode(): Int = nonce.hashCode()

        override fun toString(): String = "Nonce(nonce=$nonce)"
    }

    /**
     * 临时消息的目标用户 ID。
     */
    public class TempTarget internal constructor(public val id: ID) : KookChannelMessageSendOption() {
        override fun equals(other: Any?): Boolean =
            this === other || other is TempTarget && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "TempTarget(id=$id)"
    }

    /**
     * KookTempTarget.current 使用的默认目标用户 ID。
     */
    public class DefaultTempTarget internal constructor(public val id: ID) : KookChannelMessageSendOption() {
        override fun equals(other: Any?): Boolean =
            this === other || other is DefaultTempTarget && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "DefaultTempTarget(id=$id)"
    }

    /**
     * 模板消息 ID，消息内容作为模板输入。
     */
    public class Template internal constructor(public val id: ID) : KookChannelMessageSendOption() {
        override fun equals(other: Any?): Boolean =
            this === other || other is Template && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "Template(id=$id)"
    }

    companion {
        /**
         * 引用消息的 ID。
         */
        public fun quote(id: ID): Quote = Quote(id)

        /**
         * 平台原样返回的 nonce。
         */
        public fun nonce(nonce: String): Nonce = Nonce(nonce)

        /**
         * 临时消息的目标用户 ID。
         */
        public fun tempTarget(id: ID): TempTarget = TempTarget(id)

        /**
         * KookTempTarget.current 使用的默认目标用户 ID。
         */
        public fun defaultTempTarget(id: ID): DefaultTempTarget = DefaultTempTarget(id)

        /**
         * 模板消息 ID，消息内容作为模板输入。
         */
        public fun template(id: ID): Template = Template(id)

    }
}

/**
 * 将频道消息的单项协议选项合并为发送参数，忽略其他类型的选项。
 * 没有可用的频道消息选项时返回 null。
 *
 * @throws IllegalArgumentException 同类选项出现多次（即使值相同）。
 * @since 5.0
 */
public fun Iterable<SendOption>.toKookChannelMessageSendOptions(): KookChannelMessageSendOptions? {
    var quote: String? = null
    var nonce: String? = null
    var tempTargetId: String? = null
    var defaultTempTargetId: String? = null
    var templateId: String? = null

    for (option in this) {
        if (option !is KookChannelMessageSendOption) continue
        when (option) {
            is KookChannelMessageSendOption.Quote -> {
                require(quote == null) { "Duplicate KOOK channel message send option: Quote" }
                quote = option.id.literal
            }

            is KookChannelMessageSendOption.Nonce -> {
                require(nonce == null) { "Duplicate KOOK channel message send option: Nonce" }
                nonce = option.nonce
            }

            is KookChannelMessageSendOption.TempTarget -> {
                require(tempTargetId == null) { "Duplicate KOOK channel message send option: TempTarget" }
                tempTargetId = option.id.literal
            }

            is KookChannelMessageSendOption.DefaultTempTarget -> {
                require(defaultTempTargetId == null) { "Duplicate KOOK channel message send option: DefaultTempTarget" }
                defaultTempTargetId = option.id.literal
            }

            is KookChannelMessageSendOption.Template -> {
                require(templateId == null) { "Duplicate KOOK channel message send option: Template" }
                templateId = option.id.literal
            }
        }
    }

    if (quote == null && nonce == null && tempTargetId == null && defaultTempTargetId == null && templateId == null) {
        return null
    }

    return KookChannelMessageSendOptions(quote, nonce, tempTargetId, defaultTempTargetId, templateId)
}

/**
 * 将频道消息的单项协议选项合并为发送参数，忽略其他类型的选项。
 * 没有可用的频道消息选项时返回 null。
 *
 * @throws IllegalArgumentException 同类选项出现多次（即使值相同）。
 * @since 5.0
 */
public fun Array<out SendOption>.toKookChannelMessageSendOptions(): KookChannelMessageSendOptions? =
    asIterable().toKookChannelMessageSendOptions()

/**
 * 频道发送消息的额外协议选项。
 *
 * 消息内的引用或临时目标元素仍按原有消息转换规则覆盖默认值。
 * 用于原生请求时，已指定的选项覆盖请求中的对应参数。
 *
 * @since 5.0
 */
public class KookChannelMessageSendOptions internal constructor(
    /**
     * 引用消息的 ID。
     */
    public val quote: String? = null,
    /**
     * 平台原样返回的 nonce。
     */
    public val nonce: String? = null,
    /**
     * 临时消息的目标用户 ID。
     */
    public val tempTargetId: String? = null,
    /**
     * KookTempTarget.current 使用的默认目标用户 ID。
     */
    public val defaultTempTargetId: String? = null,
    /**
     * 模板消息 ID。消息内容作为模板输入。
     */
    public val templateId: String? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KookChannelMessageSendOptions) return false

        if (quote != other.quote) return false
        if (nonce != other.nonce) return false
        if (tempTargetId != other.tempTargetId) return false
        if (defaultTempTargetId != other.defaultTempTargetId) return false
        if (templateId != other.templateId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = quote.hashCode()
        result = 31 * result + nonce.hashCode()
        result = 31 * result + tempTargetId.hashCode()
        result = 31 * result + defaultTempTargetId.hashCode()
        result = 31 * result + templateId.hashCode()
        return result
    }

    override fun toString(): String {
        return "KookChannelMessageSendOptions(" +
            "quote=$quote, " +
            "nonce=$nonce, " +
            "tempTargetId=$tempTargetId, " +
            "defaultTempTargetId=$defaultTempTargetId, " +
            "templateId=$templateId)"
    }
}

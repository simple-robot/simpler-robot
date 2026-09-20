/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

@file:OptIn(ExperimentalStdlibApi::class)

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * 公告类别，`0` 为成员公告，`1` 为欢迎公告，默认成员公告。
 */
@JvmInline
@Serializable
@JvmExposeBoxed
public value class AnnouncesType private constructor(public val value: Int) {
    public companion object {
        /**
         * 成员公告值常量
         */
        public const val MEMBER_VALUE: Int = 0

        /**
         * 欢迎公告值常量
         */
        public const val WELCOME_VALUE: Int = 1

        /**
         * 成员公告
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Member: AnnouncesType = AnnouncesType(MEMBER_VALUE)

        /**
         * 欢迎公告
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Welcome: AnnouncesType = AnnouncesType(WELCOME_VALUE)

        /**
         * 构建任意 [AnnouncesType] 值。
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun of(value: Int): AnnouncesType = AnnouncesType(value)
    }
}


/**
 * [公告对象](https://bot.q.qq.com/wiki/develop/api/openapi/announces/model.html)
 *
 * @property guildId 频道 ID。
 * @property channelId 子频道 ID。
 * @property messageId 消息 ID。
 * @property typeOfAnnounces 公告类别，`0` 为成员公告，`1` 为欢迎公告，默认成员公告。实际是 `announces_type` 的序列化目标。
 * @property announcesType 同 [typeOfAnnounces]，早期定义的类型，已经由 [AnnouncesType] 替代，但因为名称已被占用，
 * 因此使用 [typeOfAnnounces] 作为替代。实际上的序列化属性 `announces_type` 是 [typeOfAnnounces]。
 * @property recommendChannels 推荐子频道对象数组。
 */
@ApiModel
@Serializable
public class Announces internal constructor(
    @SerialName("guild_id")
    public val guildId: String,
    @SerialName("channel_id")
    public val channelId: String,
    @SerialName("message_id")
    public val messageId: String,
    @SerialName("announces_type")
    @get:JvmExposeBoxed
    public val typeOfAnnounces: AnnouncesType,
    @Deprecated(
        "Use `typeOfAnnounces` instead.",
        ReplaceWith("typeOfAnnounces.value.toUInt()")
    )
    @get:JvmName("getAnnouncesType")
    @SerialName("_deprecated_announces_type")
    public val announcesType: UInt = typeOfAnnounces.value.toUInt(),
    @SerialName("recommend_channels")
    public val recommendChannels: List<RecommendChannel> = emptyList()
) {
    @Deprecated(DataClassCompatibilities.DEPRECATED_CONSTRUCTOR_MESSAGE, level = DeprecationLevel.ERROR)
    @ApiModelConstructor
    public constructor(
        guildId: String,
        channelId: String,
        messageId: String,
        announcesType: UInt,
        recommendChannels: List<RecommendChannel> = emptyList()
    ) : this(
        guildId = guildId,
        channelId = channelId,
        messageId = messageId,
        typeOfAnnounces = AnnouncesType.of(announcesType.toInt()),
        recommendChannels = recommendChannels,
    )

    /**
     * 获取 [announcesType] 的结果并从 [Unsigned integer type](https://kotlinlang.org/docs/unsigned-integer-types.html) 转为Java可用的 [Int]
     *
     */
    @Api4J
    @Deprecated(
        "Use `typeOfAnnounces` instead.",
        ReplaceWith("typeOfAnnounces.value")
    )
    public val announcesTypeIntValue: Int get() = typeOfAnnounces.value

    override fun toString(): String {
        return "Announces(" +
            "guildId='$guildId', " +
            "channelId='$channelId', " +
            "messageId='$messageId', " +
            "typeOfAnnounces=$typeOfAnnounces, " +
            "recommendChannels=$recommendChannels)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component1(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component2(): String = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("messageId"))
    public operator fun component3(): String = messageId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("announcesType"))
    public operator fun component4(): UInt = typeOfAnnounces.value.toUInt()

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("recommendChannels"))
    public operator fun component5(): List<RecommendChannel> = recommendChannels

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        guildId: String = this.guildId,
        channelId: String = this.channelId,
        messageId: String = this.messageId,
        announcesType: UInt = this.typeOfAnnounces.value.toUInt(),
        recommendChannels: List<RecommendChannel> = this.recommendChannels,
    ): Announces = Announces(
        guildId = guildId,
        channelId = channelId,
        messageId = messageId,
        typeOfAnnounces = AnnouncesType.of(announcesType.toInt()),
        recommendChannels = recommendChannels
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Announces) return false

        if (guildId != other.guildId) return false
        if (channelId != other.channelId) return false
        if (messageId != other.messageId) return false
        if (typeOfAnnounces != other.typeOfAnnounces) return false
        if (recommendChannels != other.recommendChannels) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + messageId.hashCode()
        result = 31 * result + typeOfAnnounces.hashCode()
        result = 31 * result + recommendChannels.hashCode()
        return result
    }
    //endregion
}

/**
 * [推荐子频道对象(RecommendChannel)](https://bot.q.qq.com/wiki/develop/api/openapi/announces/model.html#%E6%8E%A8%E8%8D%90%E5%AD%90%E9%A2%91%E9%81%93%E5%AF%B9%E8%B1%A1-recommendchannel)
 *
 * @see Announces.recommendChannels
 * @property channelId 子频道 ID。
 * @property introduce 推荐语。
 */
@ApiModel
@Serializable
public class RecommendChannel @ApiModelConstructor constructor(
    @SerialName("channel_id") public val channelId: String,
    public val introduce: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RecommendChannel) return false

        if (channelId != other.channelId) return false
        if (introduce != other.introduce) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channelId.hashCode()
        result = 31 * result + introduce.hashCode()
        return result
    }

    override fun toString(): String = "RecommendChannel(channelId='$channelId', introduce='$introduce')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component1(): String = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("introduce"))
    public operator fun component2(): String = introduce

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        channelId: String = this.channelId,
        introduce: String = this.introduce,
    ): RecommendChannel = RecommendChannel(channelId, introduce)
    //endregion
}

/*
 *     Copyright (c) 2021-2026. ForteScarlet.
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

package love.forte.simbot.kook.api.channel

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmStatic
import kotlin.time.Duration


/**
 * [编辑频道](https://developer.kookapp.cn/doc/http/channel#%E7%BC%96%E8%BE%91%E9%A2%91%E9%81%93)
 *
 * @since 4.3.0
 *
 * @author ForteScarlet
 */
public class UpdateChannelApi private constructor(public override val body: Body) : KookPostApi<ChannelView>() {
    /**
     * 用于请求频道更新的请求体。
     *
     * @since 4.3.0
     */
    @Serializable
    public class Body internal constructor(
        /** 服务器中频道的ID */
        @SerialName("channel_id")
        public val channelId: String,

        /** 频道名称 */
        public val name: String? = null,

        /** 频道排序 */
        public val level: Int? = null,

        /** 分组频道ID，设置为 "0" 为移出分组 */
        @SerialName("parent_id")
        public val parentId: String? = null,

        /** 频道简介，文字频道有效 */
        public val topic: String? = null,

        /** 慢速模式，单位ms，文字频道有效 */
        @SerialName("slow_mode")
        public val slowMode: Int? = null,

        /** 此频道最大能容纳的用户数量，最大值99，语音频道有效 */
        @SerialName("limit_amount")
        public val limitAmount: Int? = null,

        /** 声音质量，"1" 流畅，"2" 正常，"3" 高质量，语音频道有效 */
        @SerialName("voice_quality")
        public val voiceQuality: String? = null,

        /** 密码，语音频道有效 */
        public val password: String? = null,
    ) {
        override fun toString(): String {
            return "UpdateChannelApi.Body(" +
                "channelId='$channelId', " +
                "name=$name, " +
                "level=$level, " +
                "parentId=$parentId, " +
                "topic=$topic, " +
                "slowMode=$slowMode, " +
                "limitAmount=$limitAmount, " +
                "voiceQuality=$voiceQuality, " +
                "password=$password)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Body) return false

            if (level != other.level) return false
            if (slowMode != other.slowMode) return false
            if (limitAmount != other.limitAmount) return false
            if (channelId != other.channelId) return false
            if (name != other.name) return false
            if (parentId != other.parentId) return false
            if (topic != other.topic) return false
            if (voiceQuality != other.voiceQuality) return false
            if (password != other.password) return false

            return true
        }

        override fun hashCode(): Int {
            var result = level ?: 0
            result = 31 * result + (slowMode ?: 0)
            result = 31 * result + (limitAmount ?: 0)
            result = 31 * result + channelId.hashCode()
            result = 31 * result + (name?.hashCode() ?: 0)
            result = 31 * result + (parentId?.hashCode() ?: 0)
            result = 31 * result + (topic?.hashCode() ?: 0)
            result = 31 * result + (voiceQuality?.hashCode() ?: 0)
            result = 31 * result + (password?.hashCode() ?: 0)
            return result
        }
    }

    /**
     * 语音品质类型枚举。
     *
     * [UpdateChannelApi] 中会使用到的 `voiceQuality`。
     *
     * @see UpdateChannelApi.Body.voiceQuality
     * @see UpdateChannelApi.Builder.voiceQuality
     * @since 4.3.0
     */
    public enum class VoiceQuality(internal val value: String) {
        /**
         * 语音品质：流畅
         */
        SMOOTH("1"),

        /**
         * 语音品质：正常
         */
        NORMAL("2"),

        /**
         * 语音品质：高品质
         */
        HIGH("3")
    }

    /**
     * 慢速模式类型枚举。
     *
     * [UpdateChannelApi] 中会使用到的 `slowMode`。
     *
     * @see UpdateChannelApi.Body.slowMode
     * @see UpdateChannelApi.Builder.slowMode
     * @since 4.3.0
     */
    public enum class SlowMode(internal val value: Int) {
        /**
         * 无慢速模式
         */
        NONE(0),

        /**
         * 5秒
         */
        SECONDS_5(5000),

        /**
         * 10秒
         */
        SECONDS_10(10000),

        /**
         * 15秒
         */
        SECONDS_15(15000),

        /**
         * 30秒
         */
        SECONDS_30(30000),

        /**
         * 1分钟
         */
        MINUTES_1(60000),

        /**
         * 2分钟
         */
        MINUTES_2(120000),

        /**
         * 5分钟
         */
        MINUTES_5(300000),

        /**
         * 10分钟
         */
        MINUTES_10(600000),

        /**
         * 15分钟
         */
        MINUTES_15(900000),

        /**
         * 30分钟
         */
        MINUTES_30(1800000),

        /**
         * 1小时
         */
        HOURS_1(3600000),

        /**
         * 2小时
         */
        HOURS_2(7200000),

        /**
         * 6小时
         */
        HOURS_6(21600000)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    public companion object Factory {
        private val PATH = ApiPath.create("channel", "update")

        /**
         * 构建 [UpdateChannelApi].
         *
         * > Note: 属性说明中的各 "默认值" 的实际表现以服务器的处理结果为准。
         * 此处的默认代表实际请求API时不携带相关参数，而并非填充默认属性。
         *
         * @param channelId 服务器中频道的ID
         * @param name 频道名称
         * @param level 频道排序
         * @param parentId 分组频道ID，设置为 `"0"` 为移出分组
         * @param topic 频道简介，文字频道有效
         * @param slowMode 慢速模式，单位ms。支持的值：0, 5000, 10000, 15000, 30000, 60000, 120000, 300000, 600000, 900000, 1800000, 3600000, 7200000, 21600000
         * @param limitAmount 此频道最大能容纳的用户数量，最大值99，语音频道有效
         * @param voiceQuality 声音质量，`"1"` 流畅，`"2"` 正常，`"3"` 高质量，语音频道有效
         * @param password 密码，语音频道有效
         */
        @JvmStatic
        public fun create(
            channelId: String,
            name: String? = null,
            level: Int? = null,
            parentId: String? = null,
            topic: String? = null,
            slowMode: Int? = null,
            limitAmount: Int? = null,
            voiceQuality: String? = null,
            password: String? = null,
        ): UpdateChannelApi = UpdateChannelApi(
            Body(channelId, name, level, parentId, topic, slowMode, limitAmount, voiceQuality, password)
        )

        /**
         * 构建 [UpdateChannelApi].
         *
         * @param channelId 服务器中频道的ID
         */
        @JvmStatic
        public fun create(channelId: String): UpdateChannelApi = create(
            channelId,
            name = null,
            level = null,
            parentId = null,
            topic = null,
            slowMode = null,
            limitAmount = null,
            voiceQuality = null,
            password = null
        )

        /**
         * 得到一个 [UpdateChannelApi.Builder]，
         * 用于构建 [UpdateChannelApi]。
         *
         * @param channelId 服务器中频道的ID
         */
        @JvmStatic
        public fun builder(channelId: String): Builder = Builder(channelId)

    }

    /**
     * 用于构建 [UpdateChannelApi] 的构建器。
     *
     * [channelId] 是必需属性。
     *
     * @see UpdateChannelApi
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public class Builder internal constructor(
        /**
         * 服务器中频道的ID
         */
        public val channelId: String
    ) {
        /**
         * 频道名称
         */
        public var name: String? = null

        /**
         * 频道排序
         */
        public var level: Int? = null

        /**
         * 分组频道ID，设置为 `"0"` 为移出分组
         */
        public var parentId: String? = null

        /**
         * 频道简介，文字频道有效
         */
        public var topic: String? = null

        /**
         * 慢速模式，单位ms
         */
        public var slowModeValue: Int? = null

        /**
         * 此频道最大能容纳的用户数量，最大值99，语音频道有效
         */
        public var limitAmount: Int? = null

        /**
         * 声音质量值，`"1"` 流畅，`"2"` 正常，`"3"` 高质量，语音频道有效
         */
        public var voiceQualityValue: String? = null

        /**
         * 密码，语音频道有效
         */
        public var password: String? = null

        /**
         * 频道名称
         */
        public fun name(value: String): Builder = apply { name = value }

        /**
         * 频道排序
         */
        public fun level(value: Int): Builder = apply { level = value }

        /**
         * 分组频道ID，设置为 `"0"` 为移出分组
         */
        public fun parentId(value: String): Builder = apply { parentId = value }

        /**
         * 频道简介，文字频道有效
         */
        public fun topic(value: String): Builder = apply { topic = value }

        /**
         * 慢速模式，单位ms
         */
        public fun slowModeValue(value: Int): Builder = apply { slowModeValue = value }

        /**
         * 慢速模式
         */
        public fun slowMode(value: SlowMode): Builder = apply { slowModeValue = value.value }

        /**
         * 此频道最大能容纳的用户数量，最大值99，语音频道有效
         */
        public fun limitAmount(value: Int): Builder = apply { limitAmount = value }

        /**
         * 声音质量值，`"1"` 流畅，`"2"` 正常，`"3"` 高质量，语音频道有效
         */
        public fun voiceQualityValue(value: String): Builder = apply { voiceQualityValue = value }

        /**
         * 声音质量，`"1"` 流畅，`"2"` 正常，`"3"` 高质量，语音频道有效
         */
        public fun voiceQuality(value: VoiceQuality): Builder = apply { voiceQualityValue = value.value }

        /**
         * 密码，语音频道有效
         */
        public fun password(value: String): Builder = apply { password = value }

        /**
         * 根据当前属性得到 [UpdateChannelApi] 实例
         */
        public fun build(): UpdateChannelApi = UpdateChannelApi(
            Body(channelId, name, level, parentId, topic, slowModeValue, limitAmount, voiceQualityValue, password)
        )
    }


    override val resultDeserializationStrategy: DeserializationStrategy<ChannelView>
        get() = ChannelView.serializer()

    override val apiPath: ApiPath
        get() = PATH
}

/**
 * 使用 DSL 方式创建 [UpdateChannelApi].
 *
 * @param channelId 服务器中频道的ID
 * @param block DSL 构建器块
 * @return 构建的 [UpdateChannelApi] 实例
 */
public inline fun UpdateChannelApi.Factory.create(
    channelId: String,
    block: UpdateChannelApi.Builder.() -> Unit
): UpdateChannelApi =
    UpdateChannelApi.builder(channelId).apply(block).build()

/**
 * 慢速模式。
 * @see UpdateChannelApi.SlowMode
 * @see UpdateChannelApi.Builder.slowModeValue
 */
public fun UpdateChannelApi.Builder.slowMode(slowMode: Duration) {
    slowModeValue = slowMode.inWholeMilliseconds.toInt()
}

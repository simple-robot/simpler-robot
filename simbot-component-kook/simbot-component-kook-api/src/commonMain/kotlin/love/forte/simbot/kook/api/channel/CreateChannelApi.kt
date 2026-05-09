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
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic


/**
 *
 * [创建频道](https://developer.kookapp.cn/doc/http/channel#%E5%88%9B%E5%BB%BA%E9%A2%91%E9%81%93)
 *
 * @author ForteScarlet
 */
public class CreateChannelApi private constructor(
    private val guildId: String,
    private val name: String,
    private val type: Int?,
    private val parentId: String?,
    private val limitAmount: Int?,
    private val voiceQuality: Int?,
) : KookPostApi<ChannelView>() {
    @Suppress("MemberVisibilityCanBePrivate")
    public companion object Factory {
        private val PATH = ApiPath.create("channel", "create")

        /**
         * 语音品质类型枚举。
         *
         * [CreateChannelApi] 中会使用到的 `voiceQuality`。
         */
        public enum class VoiceQuality(internal val value: Int) {
            /**
             * 语音品质：流畅
             */
            SMOOTH(1),

            /**
             * 语音品质：正常
             */
            NORMAL(2),

            /**
             * 语音品质：高品质
             */
            HIGH(3)
        }


        /**
         * 构建 [CreateChannelApi].
         *
         * > Note: 属性说明中的各 “默认值” 的实际表现以服务器的处理结果为准。
         * 此处的默认代表实际请求API时不携带相关参数，而并非填充默认属性。
         *
         * @param guildId 服务器id
         * @param name 频道名称
         * @param type 频道类型，`1` 文字，`2` 语音，默认为文字
         * @param parentId 父分组id
         * @param limitAmount 语音频道人数限制，最大 `99`
         * @param voiceQuality 语音音质，默认为 `2`。
         * - 流畅: [VoiceQuality.SMOOTH.value]
         * - 正常: [VoiceQuality.NORMAL.value]
         * - 高质量: [VoiceQuality.HIGH.value]
         */
        @JvmOverloads
        @JvmStatic
        public fun create(
            guildId: String,
            name: String,
            type: Int? = null,
            parentId: String? = null,
            limitAmount: Int? = null,
            voiceQuality: Int? = null,
        ): CreateChannelApi = CreateChannelApi(guildId, name, type, parentId, limitAmount, voiceQuality)

        /**
         * 得到一个 [CreateChannelApi.Builder]，
         * 用于构建 [CreateChannelApi]。
         *
         * @param guildId 服务器id
         * @param name 频道名称
         */
        @JvmStatic
        public fun builder(guildId: String, name: String): Builder = Builder(guildId, name)

        /**
         * 使用 [Builder] 构建 [CreateChannelApi].
         *
         * @param guildId 服务器id
         * @param name 频道名称
         *
         * @see builder
         */
        @JvmSynthetic
        public inline fun create(guildId: String, name: String, block: Builder.() -> Unit): CreateChannelApi =
            builder(guildId, name).apply(block).build()
    }

    /**
     * 用于构建 [CreateChannelApi] 的构建器。
     *
     * [guildId] 与 [name] 是必需属性。
     *
     * @see CreateChannelApi
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public class Builder internal constructor(
        /**
         * 服务器id
         */
        public val guildId: String,
        /**
         * 频道名称
         */
        public val name: String
    ) {

        /**
         * 频道类型，`1` 文字，`2` 语音，默认为文字
         */
        public var type: Int? = null

        /**
         * 父分组id
         */
        public var parentId: String? = null

        /**
         * 语音频道人数限制，最大 `99`
         */
        public var limitAmount: Int? = null

        /**
         * 语音音质，默认为 `2`。
         */
        public var voiceQualityValue: Int? = null

        /**
         * 语音音质，默认为 `2`。
         */
        public var voiceQuality: VoiceQuality?
            get() = when (voiceQualityValue) {
                VoiceQuality.SMOOTH.value -> VoiceQuality.SMOOTH
                VoiceQuality.NORMAL.value -> VoiceQuality.NORMAL
                VoiceQuality.HIGH.value -> VoiceQuality.HIGH
                else -> null
            }
            set(value) {
                voiceQualityValue = value?.value
            }

        /**
         * 频道类型，`1` 文字，`2` 语音，默认为文字
         */
        public fun type(value: Int): Builder = apply { type = value }

        /**
         * 父分组id
         */
        public fun parentId(value: String): Builder = apply { parentId = value }

        /**
         * 语音频道人数限制，最大 `99`
         */
        public fun limitAmount(value: Int): Builder = apply { limitAmount = value }

        /**
         * 语音音质，默认为 `2`。
         */
        public fun voiceQualityValue(value: Int): Builder = apply { voiceQualityValue = value }

        /**
         * 语音音质，默认为 `2`。
         */
        public fun voiceQuality(value: VoiceQuality): Builder = apply { voiceQuality = value }

        /**
         * 根据当前属性得到 [CreateChannelApi] 实例
         */
        public fun build(): CreateChannelApi = CreateChannelApi(
            guildId = guildId,
            name = name,
            type = type,
            parentId = parentId,
            limitAmount = limitAmount,
            voiceQuality = voiceQualityValue,
        )
    }


    override val resultDeserializationStrategy: DeserializationStrategy<ChannelView>
        get() = ChannelView.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override fun createBody(): Any = Body(guildId, name, type, parentId, limitAmount, voiceQuality)

    @Serializable
    private data class Body(
        /** 是 服务器id */
        @SerialName("guild_id")
        val guildId: String,

        /** 是 频道名称 */
        val name: String,

        /** 否 频道类型，1 文字，2 语音，默认为文字 */
        val type: Int? = null,

        /** 否 父分组id */
        @SerialName("parent_id")
        val parentId: String? = null,

        /** 否 语音频道人数限制，最大99 */
        @SerialName("limit_amount")
        val limitAmount: Int? = null,

        /** 否 语音音质，默认为2。1流畅，2正常，3高质量 */
        @SerialName("voice_quality")
        val voiceQuality: Int? = null,
    )
}


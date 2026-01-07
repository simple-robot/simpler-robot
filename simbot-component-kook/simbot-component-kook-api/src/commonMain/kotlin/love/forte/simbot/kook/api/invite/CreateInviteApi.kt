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

package love.forte.simbot.kook.api.invite

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmStatic


/**
 * [创建邀请链接](https://developer.kookapp.cn/doc/http/invite#%E5%88%9B%E5%BB%BA%E9%82%80%E8%AF%B7%E9%93%BE%E6%8E%A5)
 *
 * @author ForteScarlet
 */
public class CreateInviteApi private constructor(
    private val guildId: String? = null,
    private val channelId: String? = null,
    private val duration: Int? = null,
    private val settingTimes: Int? = null,
) : KookPostApi<CreatedInvite>() {
    public companion object Factory {
        // /api/v3/invite/create	POST
        private val PATH = ApiPath.create("invite", "create")

        /**
         * 通过 [服务器id][guildId] 构建一个 [创建邀请链接][CreateInviteApi] 实例。
         */
        @JvmStatic
        public fun builderByGuildId(guildId: String): Builder = Builder(guildId = guildId)

        /**
         * 通过 [频道id][channelId] 构建一个 [创建邀请链接][CreateInviteApi] 实例。
         */
        @JvmStatic
        public fun builderByChannelId(channelId: String): Builder =
            Builder(channelId = channelId)

        /**
         * 通过 [服务器id][guildId] 构建一个 [创建邀请链接][CreateInviteApi] 实例。
         *
         * @param guildId 服务器id
         */
        @JvmStatic
        public fun createByGuildId(guildId: String): CreateInviteApi =
            CreateInviteApi(guildId = guildId)

        /**
         * 通过 [频道id][channelId] 构建一个 [创建邀请链接][CreateInviteApi] 实例。
         *
         * @param channelId 频道id
         */
        @JvmStatic
        public fun createByChannelId(channelId: String): CreateInviteApi =
            CreateInviteApi(channelId = channelId)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<CreatedInvite>
        get() = CreatedInvite.serializer()

    override fun createBody(): Any = Body(
        guildId = guildId,
        channelId = channelId,
        duration = duration,
        settingTimes = settingTimes,
    )

    @Serializable
    private data class Body(
        @SerialName("guild_id") val guildId: String? = null,
        @SerialName("channel_id") val channelId: String? = null,
        val duration: Int? = null,
        @SerialName("setting_times") val settingTimes: Int? = null,
    )

    /**
     * 一个用于 [创建邀请链接][CreateInviteApi] 的构建器。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public class Builder internal constructor(
        public val guildId: String? = null,
        public val channelId: String? = null
    ) {

        /**
         * 邀请链接有效时长（秒），默认 7 天。可选值：
         * - 0 => 永不；
         * - 1800 => 0.5 小时；
         * - 3600 => 1 个小时；
         * - 21600 => 6 个小时；
         * - 43200 => 12 个小时；
         * - 86400 => 1 天；
         * - 604800 => 7 天
         */
        public var duration: Int? = null

        /**
         * 设置的次数，默认-1。可选值：
         * - -1 => 无限制；
         * - 1 => 1 次使用；
         * - 5 => 5 次使用；
         * - 10 => 10 次使用 ；
         * - 25 => 25 次使用；
         * - 50 => 50 次使用；
         * - 100 => 100 次使用
         */
        public var settingTimes: Int? = null

        //region durations

        /**
         * 邀请链接有效时长（秒），默认 7 天。可选值：
         * - 0 => 永不；
         * - 1800 => 0.5 小时；
         * - 3600 => 1 个小时；
         * - 21600 => 6 个小时；
         * - 43200 => 12 个小时；
         * - 86400 => 1 天；
         * - 604800 => 7 天
         */
        public fun duration(duration: Int): Builder = apply { this.duration = duration }

        /**
         * 邀请链接有效时长 1800 秒 （0.5 小时）
         */
        public fun duration1800(): Builder = duration(1800)

        /**
         * 邀请链接有效时长 1800 秒 （0.5 小时）
         */
        public fun durationHalfHour(): Builder = duration1800()

        /**
         * 邀请链接有效时长 3600 秒 （1 小时）
         */
        public fun duration3600(): Builder = duration(3600)

        /**
         * 邀请链接有效时长 3600 秒 （1 小时）
         */
        public fun duration1Hour(): Builder = duration3600()


        /**
         * 邀请链接有效时长 21600 秒 （6 小时）
         */
        public fun duration21600(): Builder = duration(21600)

        /**
         * 邀请链接有效时长 21600 秒 （6 小时）
         */
        public fun duration6Hour(): Builder = duration21600()

        /**
         * 邀请链接有效时长 43200 秒 （12 小时）
         */
        public fun duration43200(): Builder = duration(43200)

        /**
         * 邀请链接有效时长 43200 秒 （12 小时）
         */
        public fun duration12Hour(): Builder = duration43200()

        /**
         * 邀请链接有效时长 86400 秒 （1 天）
         */
        public fun duration86400(): Builder = duration(86400)

        /**
         * 邀请链接有效时长 86400 秒 （1 天）
         */
        public fun duration1Day(): Builder = duration86400()

        /**
         * 邀请链接有效时长 604800 秒 （7 天）
         */
        public fun duration604800(): Builder = duration(604800)

        /**
         * 邀请链接有效时长 604800 秒 （7 天）
         */
        public fun duration7Day(): Builder = duration604800()
        //endregion

        //region settingTimes
        /**
         * 设置的次数，默认-1。可选值：
         * - -1 => 无限制；
         * - 1 => 1 次使用；
         * - 5 => 5 次使用；
         * - 10 => 10 次使用 ；
         * - 25 => 25 次使用；
         * - 50 => 50 次使用；
         * - 100 => 100 次使用
         */
        public fun settingTimes(settingTimes: Int): Builder = apply { this.settingTimes = settingTimes }

        /**
         * 设置的次数 无限制
         */
        public fun settingUnlimitedTimes(): Builder = settingTimes(-1)

        /**
         * 设置的次数 1 次使用
         */
        public fun setting1Times(): Builder = settingTimes(1)

        /**
         * 设置的次数 5 次使用
         */
        public fun setting5Times(): Builder = settingTimes(5)

        /**
         * 设置的次数 10 次使用
         */
        public fun setting10Times(): Builder = settingTimes(10)

        /**
         * 设置的次数 25 次使用
         */
        public fun setting25Times(): Builder = settingTimes(25)

        /**
         * 设置的次数 50 次使用
         */
        public fun setting50Times(): Builder = settingTimes(50)

        /**
         * 设置的次数 100 次使用
         */
        public fun setting100Times(): Builder = settingTimes(100)
        //endregion

        /**
         * 构建 [CreateInviteApi] 实例。
         *
         * @throws IllegalArgumentException 如果 [guildId] 和 [channelId] 都为 null
         */
        public fun build(): CreateInviteApi {
            // 服务器 id 或者频道 id 必须填一个
            require(guildId != null || channelId != null) { "A guildId or channelId must be exist" }

            return CreateInviteApi(guildId, channelId, duration, settingTimes)
        }
    }
}

/**
 * [CreateInviteApi] 的响应体：创建邀请链接返回值
 */
@Serializable
public data class CreatedInvite @ApiResultType constructor(val url: String)

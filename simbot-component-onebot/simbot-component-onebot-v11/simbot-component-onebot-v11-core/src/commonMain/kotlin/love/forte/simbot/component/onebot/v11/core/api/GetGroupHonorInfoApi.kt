/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.api

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.LongID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import love.forte.simbot.component.onebot.v11.common.utils.qqAvatar640
import kotlin.jvm.JvmStatic

/**
 * [`get_group_honor_info`-获取群荣誉信息](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_group_honor_info-获取群荣誉信息)
 *
 * @author ForteScarlet
 */
public class GetGroupHonorInfoApi private constructor(
    override val body: Any,
) : OneBotApi<GetGroupHonorInfoResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<GetGroupHonorInfoResult>
        get() = GetGroupHonorInfoResult.serializer()

    override val apiResultDeserializer:
        DeserializationStrategy<OneBotApiResult<GetGroupHonorInfoResult>>
        get() = RES_SER

    public companion object Factory {
        private const val ACTION: String = "get_group_honor_info"

        public const val TYPE_ALL: String = "all"
        public const val TYPE_TALKATIVE: String = "talkative"
        public const val TYPE_PERFORMER: String = "performer"
        public const val TYPE_LEGEND: String = "legend"
        public const val TYPE_STRONG_NEWBIE: String = "strong_newbie"
        public const val TYPE_EMOTION: String = "emotion"

        private val RES_SER: KSerializer<OneBotApiResult<GetGroupHonorInfoResult>> =
            OneBotApiResult.serializer(GetGroupHonorInfoResult.serializer())

        /**
         * 构建一个 [GetGroupHonorInfoApi].
         *
         * @param groupId 群号
         * @param type 要获取的群荣誉类型，可传入 `talkative` `performer` `legend` `strong_newbie` `emotion`
         * 以分别获取单个类型的群荣誉数据，或传入 `all` 获取所有数据
         */
        @JvmStatic
        public fun create(groupId: ID, type: String): GetGroupHonorInfoApi =
            GetGroupHonorInfoApi(Body(groupId, type))

        /**
         * 构建一个 [GetGroupHonorInfoApi].
         *
         * @param groupId 群号
         */
        @JvmStatic
        public fun createAll(groupId: ID): GetGroupHonorInfoApi =
            create(groupId, TYPE_ALL)
    }

    /**
     * @property groupId 群号
     * @property type 要获取的群荣誉类型，可传入 `talkative` `performer` `legend` `strong_newbie` `emotion`
     * 以分别获取单个类型的群荣誉数据，或传入 `all` 获取所有数据
     */
    @Serializable
    internal data class Body(
        @SerialName("group_id")
        internal val groupId: ID,
        internal val type: String,
    )
}

/**
 * [GetGroupHonorInfoApi] 的响应体。
 *
 * @property groupId 群号
 * @property currentTalkative 当前龙王，仅 `type` 为 `talkative` 或 `all` 时有数据
 * @property talkativeList 历史龙王，仅 `type` 为 `talkative` 或 `all` 时有数据
 * @property performerList 群聊之火，仅 `type` 为 `performer` 或 `all` 时有数据
 * @property legendList 群聊炽焰，仅 `type` 为 `legend` 或 `all` 时有数据
 * @property strongNewbieList 冒尖小春笋，仅 `type` 为 `strong_newbie` 或 `all` 时有数据
 * @property emotionList 快乐之源，仅 `type` 为 `emotion` 或 `all` 时有数据
 */
@Serializable
public data class GetGroupHonorInfoResult @ApiResultConstructor constructor(
    @SerialName("group_id")
    public val groupId: LongID,
    @SerialName("current_talkative")
    public val currentTalkative: CurrentTalkative? = null,
    @SerialName("talkative_list")
    public val talkativeList: List<HonorInfo>? = null,
    @SerialName("performer_list")
    public val performerList: List<HonorInfo>? = null,
    @SerialName("legend_list")
    public val legendList: List<HonorInfo>? = null,
    @SerialName("strong_newbie_list")
    public val strongNewbieList: List<HonorInfo>? = null,
    @SerialName("emotion_list")
    public val emotionList: List<HonorInfo>? = null,
) {

    /**
     * 当前龙王
     * @property userId QQ 号
     * @property nickname 昵称
     * @property avatar 头像 URL
     * @property dayCount 持续天数
     */
    @Serializable
    public data class CurrentTalkative(
        @SerialName("user_id")
        val userId: LongID,
        val nickname: String,
        val avatar: String = qqAvatar640(userId.literal),
        @SerialName("day_count")
        val dayCount: Int = 0,
    )

    /**
     * 龙王以外的荣耀信息内容。
     *
     * @property userId QQ 号
     * @property nickname 昵称
     * @property avatar 头像 URL
     * @property description 荣誉描述
     */
    @Serializable
    public data class HonorInfo(
        @SerialName("user_id")
        public val userId: LongID,
        public val nickname: String,
        public val avatar: String = qqAvatar640(userId.literal),
        public val description: String = "",
    )
}

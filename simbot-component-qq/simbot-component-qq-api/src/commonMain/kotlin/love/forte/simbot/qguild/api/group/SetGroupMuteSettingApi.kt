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

package love.forte.simbot.qguild.api.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimplePostApiDescription
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.time.Instant

/**
 * [设置群成员禁言](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_group_openid_restrict_chat_setting.post.html)。
 *
 * 机器人需要群管理员身份。单次最多操作 20 名普通成员，最长禁言 30 天。
 *
 * @since 5.0
 */
public class SetGroupMuteSettingApi private constructor(
    groupOpenid: String,
    override val body: Any,
) : PostQQGuildApi<Unit>(), QQGuildApiWithoutResult {
    public companion object Factory : SimplePostApiDescription("/v2/groups/{group_openid}/restrict_chat_setting") {
        /**
         * 构建群成员禁言请求。
         *
         * @param groupOpenid 群OpenID
         * @param members 用户禁言列表；每项通过 op 控制增/改/删， 单次设置不能超过 20 个
         */
        @JvmStatic
        public fun create(groupOpenid: String, members: Iterable<SetMemberMuteState>): SetGroupMuteSettingApi =
            SetGroupMuteSettingApi(groupOpenid, Body(members.toList()))

        /**
         * 构建群成员禁言请求。
         *
         * @param groupOpenid 群OpenID
         * @param members 用户禁言列表；每项通过 op 控制增/改/删， 单次设置不能超过 20 个
         */
        @JvmStatic
        public fun create(groupOpenid: String, vararg members: SetMemberMuteState): SetGroupMuteSettingApi =
            SetGroupMuteSettingApi(groupOpenid, Body(members.toList()))
    }

    override val path: Array<String> = arrayOf("v2", "groups", groupOpenid, "restrict_chat_setting")

    override fun createBody(): Any? = null

    /**
     * 成员禁言设置请求体。
     *
     * @since 5.0
     */
    @Serializable
    private class Body(
        /**
         * 用户禁言操作列表，可省略；单次最多 20 项。
         */
        public val members: List<SetMemberMuteState>? = null,
    ) {
        override fun toString(): String {
            return "Body(members=$members)"
        }
    }

}

/**
 * [SetGroupMuteSettingApi] 的操作类型。
 * @since 5.0
 */
public enum class SetGroupMuteSettingOp {
    /**
     * 增加禁言。
     */
    @SerialName("add")
    ADD,

    /**
     * 更新禁言到期时间。
     */
    @SerialName("update")
    UPDATE,

    /**
     * 解除禁言。
     */
    @SerialName("del")
    DEL

}

/**
 * 单个成员的禁言操作。
 *
 * @property op 操作：add、update 或 del。
 * @property memberOpenid 成员 OpenID。
 * @property muteExpireAt 到期时间，RFC3339 格式；del 时可传空串。
 *
 * @since 5.0
 */
@Serializable
public class SetMemberMuteState private constructor(
    public val op: SetGroupMuteSettingOp,
    @SerialName("member_openid") public val memberOpenid: String,
    @SerialName("mute_expire_at") public val muteExpireAt: Instant? = null,
) {
    public companion object {
        /**
         * 构造一个 [SetMemberMuteState] 实例。
         */
        @JvmStatic
        @JvmOverloads
        public fun of(
            op: SetGroupMuteSettingOp,
            memberOpenid: String,
            muteExpireAt: Instant? = null
        ): SetMemberMuteState = SetMemberMuteState(op, memberOpenid, muteExpireAt)

        /**
         * 构造一个 [SetMemberMuteState] 实例。
         *
         * @param muteExpireAtMilliseconds 到期时间，毫秒数；del 时可传空串。
         */
        @JvmStatic
        public fun of(
            op: SetGroupMuteSettingOp,
            memberOpenid: String,
            muteExpireAtMilliseconds: Long?
        ): SetMemberMuteState = of(
            op,
            memberOpenid,
            muteExpireAtMilliseconds?.let { Instant.fromEpochMilliseconds(it) }
        )
    }

    override fun toString(): String {
        return "SetMemberMuteState(op=$op, memberOpenid='$memberOpenid', muteExpireAt=$muteExpireAt)"
    }
}

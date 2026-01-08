/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel

/**
 * [用户对象](https://bot.q.qq.com/wiki/develop/api/openapi/user/model.html#user)
 *
 * 用户对象中所涉及的 ID 类数据，都仅在机器人场景流通，与真实的 ID 无关。请不要理解为真实的 ID
 *
 *
 * ### 提示
 * [unionOpenid] 与 [unionUserAccount] 只有在单独拉取 `member` 信息的时候才会提供，在其他的事件中所携带的 `user` 对象，均无这两个字段的内容。
 *
 */
@ApiModel
@Serializable
public data class User(
    /** 用户 id */
    val id: String,
    /** 用户名 */
    val username: String,
    /** 用户头像地址 */
    val avatar: String = "",
    /** 是否是机器人 */
    @SerialName("bot") val isBot: Boolean = false,
    /** 特殊关联应用的 `openid`，需要特殊申请并配置后才会返回。如需申请，请联系平台运营人员。 */
    @SerialName("union_openid") val unionOpenid: String? = null,
    /** 机器人关联的互联应用的用户信息，与 [unionOpenid] 关联的应用是同一个。如需申请，请联系平台运营人员。 */
    @SerialName("union_user_account") val unionUserAccount: String? = null,
)



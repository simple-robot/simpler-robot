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

package love.forte.simbot.kook.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *
 * [引用消息 Quote](https://developer.kookapp.cn/doc/objects#%E5%BC%95%E7%94%A8%E6%B6%88%E6%81%AF%20Quote)
 *
 * @author ForteScarlet
 */
@Serializable
public data class Quote(
    /**
     * 引用消息 id
     */
    val id: String,
    /**
     * 引用消息类型
     */
    val type: Int,
    /**
     * 引用消息内容
     */
    val content: String,
    /**
     * 引用消息创建时间（毫秒）
     */
    @SerialName("create_at") val createAt: Long,
    /**
     * 作者的用户信息
     */
    val author: SimpleUser
)


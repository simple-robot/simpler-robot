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

// @file:OptIn(ExperimentalJsExport::class)
// @file:JsExport

package love.forte.simbot.qguild.model

import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities

/**
 *
 * [表情对象](https://bot.q.qq.com/wiki/develop/api/openapi/emoji/model.html)
 *
 * @property id 表情ID，系统表情使用数字为ID，emoji使用emoji本身为id，参考 [Emoji 列表](https://bot.q.qq.com/wiki/develop/api/openapi/emoji/model.html#emoji-%E5%88%97%E8%A1%A8)
 * @property type 表情类型 [EmojiTypes]
 *
 * @author ForteScarlet
 */
@Serializable
public class Emoji @ApiModelConstructor constructor(
    public val id: String,
    public val type: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Emoji) return false

        if (id != other.id) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type
        return result
    }

    override fun toString(): String = "Emoji(id='$id', type=$type)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
    public operator fun component1(): String = id

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("type"))
    public operator fun component2(): Int = type

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        id: String = this.id,
        type: Int = this.type,
    ): Emoji = Emoji(id, type)
    //endregion
}

/**
 * [EmojiType](https://bot.q.qq.com/wiki/develop/api/openapi/emoji/model.html#emojitype)
 *
 * @author ForteScarlet
 */
public object EmojiTypes {
    /**
     * 系统表情
     */
    public const val SYSTEM: Int = 1

    /**
     * emoji表情
     */
    public const val EMOJI: Int = 2

}

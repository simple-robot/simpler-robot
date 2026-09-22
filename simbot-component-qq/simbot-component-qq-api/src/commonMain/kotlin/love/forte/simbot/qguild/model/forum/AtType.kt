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

package love.forte.simbot.qguild.model.forum

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

/**
 * 富文本 `@` 对象的类型。
 *
 * [AtInfo.type] 使用此类型表示 `@` 对象类型。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#attype)
 *
 * @since 5.0
 * @author ForteScarlet
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class AtType private constructor(public val value: Int) {
    public companion object {
        /**
         * 指定用户的原始值。
         */
        public const val EXPLICIT_USER_VALUE: Int = 1

        /**
         * 角色组所有人的原始值。
         */
        public const val ROLE_GROUP_VALUE: Int = 2

        /**
         * 频道所有人的原始值。
         */
        public const val GUILD_VALUE: Int = 3

        /**
         * 指定用户类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val ExplicitUser: AtType = AtType(EXPLICIT_USER_VALUE)

        /**
         * 角色组所有人类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val RoleGroup: AtType = AtType(ROLE_GROUP_VALUE)

        /**
         * 频道所有人类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Guild: AtType = AtType(GUILD_VALUE)

        /**
         * 根据任意值构建 [AtType]。
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun of(value: Int): AtType = AtType(value)
    }
}

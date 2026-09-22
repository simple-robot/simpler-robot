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
 * [审核的类型](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#audittype)
 *
 * | 字段名 | 值 | 描述 |
 * | --- | --- | --- |
 * | `PUBLISH_THREAD` | `1` | 帖子 |
 * | `PUBLISH_POST` | `2` | 评论 |
 * | `PUBLISH_REPLY` | `3` | 回复 |
 *
 * @since 5.0
 * @author ForteScarlet
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class AuditType private constructor(public val value: Int) {
    public companion object {
        /**
         * 帖子类型常量值
         */
        public const val PUBLISH_THREAD_VALUE: Int = 1

        /**
         * 评论类型常量值
         */
        public const val PUBLISH_POST_VALUE: Int = 2

        /**
         * 回复类型常量值
         */
        public const val PUBLISH_REPLY_VALUE: Int = 3

        /**
         * 帖子类型
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val PublishThread: AuditType = AuditType(PUBLISH_THREAD_VALUE)

        /**
         * 评论类型
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val PublishPost: AuditType = AuditType(PUBLISH_POST_VALUE)

        /**
         * 回复类型
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val PublishReply: AuditType = AuditType(PUBLISH_REPLY_VALUE)

        /**
         * 根据任意值构建 [AuditType]
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun of(value: Int): AuditType = AuditType(value)
    }
}

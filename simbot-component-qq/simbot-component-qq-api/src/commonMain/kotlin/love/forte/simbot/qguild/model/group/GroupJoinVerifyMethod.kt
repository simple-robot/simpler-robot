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

package love.forte.simbot.qguild.model.group

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

/**
 * 入群申请的验证方式。
 *
 * 已知取值为 [VerifyMessage] 与 [AdminReviewQa]；未知平台取值通过 [of] 保留。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class GroupJoinVerifyMethod private constructor(public val value: String) {
    @JvmExposeBoxed
    public companion object {
        /**
         * 验证消息的原始值。
         */
        public const val VERIFY_MESSAGE_VALUE: String = "verify_message"

        /**
         * 管理员审核问答的原始值。
         */
        public const val ADMIN_REVIEW_QA_VALUE: String = "admin_review_qa"

        /**
         * 验证消息。
         */
        @JvmStatic
        public val VerifyMessage: GroupJoinVerifyMethod = GroupJoinVerifyMethod(VERIFY_MESSAGE_VALUE)

        /**
         * 管理员审核问答。
         */
        @JvmStatic
        public val AdminReviewQa: GroupJoinVerifyMethod = GroupJoinVerifyMethod(ADMIN_REVIEW_QA_VALUE)

        /**
         * 根据平台原始值构造验证方式，保留未知值。
         */
        @JvmStatic
        public fun of(value: String): GroupJoinVerifyMethod = GroupJoinVerifyMethod(value)
    }
}

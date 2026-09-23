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

package love.forte.simbot.component.qguild.group

import love.forte.simbot.ability.RejectOption
import kotlin.jvm.JvmStatic

/**
 * 在 QQ 组件中对入群申请进行拒绝时的额外可选操作。
 *
 * @since 5.0
 * @author Forte Scarlet
 */
public sealed class QGGroupJoinRequestRejectOption : RejectOption {
    /**
     * 拒绝理由。会在 API 中作为 `reject_reason` 参数。
     *
     * 如果作为 options 参数时出现多个 [Reason]，理由会以换行符连接。
     * 例如 `Reason(reason="拒绝，")` 和 `Reason(rason="没有理由。")`
     * 最终会被合并为 `"拒绝，没有理由。"`。
     *
     * @property reason 拒绝理由。
     */
    public class Reason internal constructor(public val reason: String) : QGGroupJoinRequestRejectOption() {
        override fun toString(): String {
            return "Reason(reason='$reason')"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Reason) return false

            if (reason != other.reason) return false

            return true
        }

        override fun hashCode(): Int {
            return reason.hashCode()
        }
    }

    /**
     * 是否同时加入群黑名单。会在 API 中作为 `add_to_member_blacklist` 参数。
     */
    public data object AddToBlacklist : QGGroupJoinRequestRejectOption()

    public companion object {
        /**
         * 为 QQ 组件入群请求的拒绝行为提供一个拒绝理由。
         */
        @JvmStatic
        public fun reason(reason: String): Reason = Reason(reason)

        /**
         * 拒绝的同时加入群黑名单。
         */
        @JvmStatic
        public fun addToBlacklist(): AddToBlacklist = AddToBlacklist
    }

}

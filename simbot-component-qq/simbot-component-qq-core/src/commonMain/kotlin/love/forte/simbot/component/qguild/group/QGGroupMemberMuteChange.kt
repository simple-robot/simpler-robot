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

import love.forte.simbot.common.id.ID
import kotlin.jvm.JvmStatic
import kotlin.time.Instant

/**
 * 指定群成员的一次禁言变更。
 *
 * @since 5.0
 */
public sealed class QGGroupMemberMuteChange private constructor() {
    /**
     * 目标成员在所属群内的 OpenID。
     */
    public abstract val memberId: ID

    /**
     * 增加目标成员的单独禁言。
     */
    public class Add internal constructor(
        /**
         * 目标成员的群内 OpenID。
         */
        override val memberId: ID,
        /**
         * 禁言的绝对到期时间。
         */
        public val muteExpireAt: Instant,
    ) : QGGroupMemberMuteChange()

    /**
     * 更新目标成员已有禁言的到期时间。
     */
    public class Update internal constructor(
        /**
         * 目标成员的群内 OpenID。
         */
        override val memberId: ID,
        /**
         * 禁言的绝对到期时间。
         */
        public val muteExpireAt: Instant,
    ) : QGGroupMemberMuteChange()

    /**
     * 解除目标成员的单独禁言。
     */
    public class Delete internal constructor(
        /**
         * 目标成员的群内 OpenID。
         */
        override val memberId: ID,
    ) : QGGroupMemberMuteChange()

    /**
     * 构造保留明确平台操作语义的成员禁言变更。
     */
    public companion object {
        /**
         * 构造增加禁言的变更。
         */
        @JvmStatic
        public fun add(memberId: ID, expireAt: Instant): Add = Add(memberId, expireAt)

        /**
         * 构造更新禁言到期时间的变更。
         */
        @JvmStatic
        public fun update(memberId: ID, expireAt: Instant): Update = Update(memberId, expireAt)

        /**
         * 构造解除单独禁言的变更。
         */
        @JvmStatic
        public fun remove(memberId: ID): Delete = Delete(memberId)

        /**
         * 使用自 Unix epoch 起的毫秒数构造增加禁言的变更。
         */
        @JvmStatic
        public fun addAtMilliseconds(memberId: ID, expireAtMilliseconds: Long): Add =
            add(memberId, Instant.fromEpochMilliseconds(expireAtMilliseconds))

        /**
         * 使用自 Unix epoch 起的毫秒数构造更新禁言的变更。
         */
        @JvmStatic
        public fun updateAtMilliseconds(memberId: ID, expireAtMilliseconds: Long): Update =
            update(memberId, Instant.fromEpochMilliseconds(expireAtMilliseconds))
    }
}

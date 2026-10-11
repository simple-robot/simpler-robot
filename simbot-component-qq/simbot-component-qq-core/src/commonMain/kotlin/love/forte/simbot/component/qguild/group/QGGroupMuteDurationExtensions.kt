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
import love.forte.simbot.qguild.QQGuildApiException
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * 为此成员增加指定时长的禁言。
 *
 * @param duration 禁言时长，必须为有限的正值。
 * @param now 计算到期时间的起点，默认为当前时间。
 * @throws IllegalArgumentException 时长无效。
 * @throws QQGuildApiException API 调用失败。
 * @see QGGroupMember.addMute
 * @since 5.0
 */
public suspend fun QGGroupMember.addMuteFor(
    duration: Duration,
    now: Instant = Clock.System.now(),
) {
    addMute(muteExpireAt(duration, now))
}

/**
 * 为指定成员增加指定时长的禁言。
 *
 * @param memberId 群成员 ID，不能为空白。
 * @param duration 禁言时长，必须为有限的正值。
 * @param now 计算到期时间的起点，默认为当前时间。
 * @throws IllegalArgumentException 参数无效。
 * @throws QQGuildApiException API 调用失败。
 * @see QGGroup.addMemberMute
 * @since 5.0
 */
public suspend fun QGGroup.addMemberMuteFor(
    memberId: ID,
    duration: Duration,
    now: Instant = Clock.System.now(),
) {
    addMemberMute(memberId, muteExpireAt(duration, now))
}

/**
 * 为指定群的成员增加指定时长的禁言。
 *
 * @param groupId 群 ID，不能为空白。
 * @param memberId 群成员 ID，不能为空白。
 * @param duration 禁言时长，必须为有限的正值。
 * @param now 计算到期时间的起点，默认为当前时间。
 * @throws IllegalArgumentException 参数无效。
 * @throws QQGuildApiException API 调用失败。
 * @see QGGroupRelation.addMemberMute
 * @since 5.0
 */
public suspend fun QGGroupRelation.addMemberMuteFor(
    groupId: ID,
    memberId: ID,
    duration: Duration,
    now: Instant = Clock.System.now(),
) {
    addMemberMute(groupId, memberId, muteExpireAt(duration, now))
}

/**
 * 更新此成员已有禁言的时长。
 *
 * @param duration 禁言时长，必须为有限的正值。
 * @param now 计算到期时间的起点，默认为当前时间。
 * @throws IllegalArgumentException 时长无效。
 * @throws QQGuildApiException API 调用失败。
 * @see QGGroupMember.updateMute
 * @since 5.0
 */
public suspend fun QGGroupMember.updateMuteFor(
    duration: Duration,
    now: Instant = Clock.System.now(),
) {
    updateMute(muteExpireAt(duration, now))
}

/**
 * 更新指定成员已有禁言的时长。
 *
 * @param memberId 群成员 ID，不能为空白。
 * @param duration 禁言时长，必须为有限的正值。
 * @param now 计算到期时间的起点，默认为当前时间。
 * @throws IllegalArgumentException 参数无效。
 * @throws QQGuildApiException API 调用失败。
 * @see QGGroup.updateMemberMute
 * @since 5.0
 */
public suspend fun QGGroup.updateMemberMuteFor(
    memberId: ID,
    duration: Duration,
    now: Instant = Clock.System.now(),
) {
    updateMemberMute(memberId, muteExpireAt(duration, now))
}

/**
 * 更新指定群成员已有禁言的时长。
 *
 * @param groupId 群 ID，不能为空白。
 * @param memberId 群成员 ID，不能为空白。
 * @param duration 禁言时长，必须为有限的正值。
 * @param now 计算到期时间的起点，默认为当前时间。
 * @throws IllegalArgumentException 参数无效。
 * @throws QQGuildApiException API 调用失败。
 * @see QGGroupRelation.updateMemberMute
 * @since 5.0
 */
public suspend fun QGGroupRelation.updateMemberMuteFor(
    groupId: ID,
    memberId: ID,
    duration: Duration,
    now: Instant = Clock.System.now(),
) {
    updateMemberMute(groupId, memberId, muteExpireAt(duration, now))
}

/**
 * 计算禁言到期时间，拒绝无效时长。
 */
internal fun muteExpireAt(duration: Duration, now: Instant): Instant {
    require(duration.isFinite() && duration.isPositive()) { "Mute duration must be finite and positive" }
    return now + duration
}

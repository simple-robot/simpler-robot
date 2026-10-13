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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import kotlin.jvm.JvmExposeBoxed
import kotlin.time.Instant

/**
 * 群禁言状态。
 *
 * @property globalRule 全员禁言规则。
 * @property members 当前仍处于禁言状态的成员。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupMuteSetting @ApiModelConstructor internal constructor(
    @SerialName("global_rule")
    public val globalRule: GroupGlobalMuteRule,
    public val members: List<GroupMemberMuteState> = emptyList(),
) {
    override fun toString(): String = "GroupMuteSetting(globalRule=$globalRule, members=$members)"
}

/**
 * 群级禁言规则。
 *
 * @property mode 全员禁言模式。
 * @property scheduleRules 定时禁言规则。
 * @property recurringRules 周期禁言规则。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@ApiModel
@Serializable
public class GroupGlobalMuteRule @ApiModelConstructor internal constructor(
    @get:JvmExposeBoxed
    public val mode: GroupGlobalMuteMode,
    @SerialName("schedule_rules")
    public val scheduleRules: List<GroupMuteScheduleRule> = emptyList(),
    @SerialName("recurring_rules")
    public val recurringRules: List<GroupMuteRecurringRule> = emptyList(),
) {
    override fun toString(): String =
        "GroupGlobalMuteRule(mode=$mode, scheduleRules=$scheduleRules, recurringRules=$recurringRules)"
}

/**
 * 定时禁言规则。
 *
 * @property taskId 任务 ID。
 * @property startAt 禁言开始时间点。
 * @property endAt 禁言结束时间点。
 * @property enabled 是否启用。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupMuteScheduleRule @ApiModelConstructor internal constructor(
    @SerialName("task_id")
    public val taskId: String,
    @SerialName("start_at")
    public val startAt: Instant,
    @SerialName("end_at")
    public val endAt: Instant,
    public val enabled: Boolean,
) {
    override fun toString(): String =
        "GroupMuteScheduleRule(taskId='$taskId', startAt=$startAt, endAt=$endAt, enabled=$enabled)"
}

/**
 * 周期禁言规则；时间按北京时间解释。
 *
 * @property taskId 任务 ID。
 * @property weekdays 生效星期，1 至 7 分别为周一至周日。
 * @property startTime 时段开始时间，HH:mm。
 * @property endTime 时段结束时间，HH:mm；小于开始时间表示跨天。
 * @property enabled 是否启用。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupMuteRecurringRule @ApiModelConstructor internal constructor(
    @SerialName("task_id")
    public val taskId: String,
    public val weekdays: List<Int>,
    @SerialName("start_time")
    public val startTime: String,
    @SerialName("end_time")
    public val endTime: String,
    public val enabled: Boolean,
) {
    override fun toString(): String =
        "GroupMuteRecurringRule(taskId='$taskId', weekdays=$weekdays, " +
            "startTime='$startTime', endTime='$endTime', enabled=$enabled)"
}

/**
 * 成员禁言状态。
 *
 * @property memberOpenid 成员 OpenID。
 * @property muteExpireAt 禁言到期时间点。
 * @property username 成员昵称。
 * @property unionOpenid 应用或开放平台统一标识，如有。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupMemberMuteState @ApiModelConstructor internal constructor(
    @SerialName("member_openid")
    public val memberOpenid: String,
    @SerialName("mute_expire_at")
    public val muteExpireAt: Instant,
    public val username: String,
    @SerialName("union_openid")
    public val unionOpenid: String? = null,
) {
    override fun toString(): String =
        "GroupMemberMuteState(memberOpenid='$memberOpenid', muteExpireAt=$muteExpireAt, " +
            "username='$username', unionOpenid=$unionOpenid)"
}

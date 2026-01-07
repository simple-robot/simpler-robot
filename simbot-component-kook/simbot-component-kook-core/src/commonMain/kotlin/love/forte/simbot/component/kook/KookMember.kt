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

package love.forte.simbot.component.kook

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.ability.DeleteFailureException
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.TimeUnit
import love.forte.simbot.component.kook.message.KookMessageReceipt
import love.forte.simbot.component.kook.role.KookMemberRole
import love.forte.simbot.definition.Member
import love.forte.simbot.kook.api.ApiResponseException
import love.forte.simbot.kook.api.ApiResultException
import love.forte.simbot.kook.api.guild.CreateGuildMuteApi
import love.forte.simbot.kook.api.guild.MuteType
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.suspendrunner.ST
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmSynthetic
import kotlin.time.Duration
import love.forte.simbot.kook.objects.User as KUser


/**
 * 一个 KOOK 的频道成员。
 *
 * @author ForteScarlet
 */
public interface KookMember : Member, CoroutineScope, DeleteSupport {
    /**
     * 源于 bot 的上下文，但是没有 Job。
     */
    override val coroutineContext: CoroutineContext

    /**
     * 得到此 Member 内对应的 api 模块下的原始 user 信息。
     */
    public val source: KUser

    /**
     * 成员ID
     */
    override val id: ID
        get() = source.id.ID

    /**
     * 成员头像
     */
    override val avatar: String
        get() = source.avatar

    /**
     * 成员用户名
     */
    override val name: String
        get() = source.username

    /**
     * 成员昵称
     */
    override val nick: String?
        get() = source.nickname

    /**
     * 用户名的认证数字
     *
     * @see KUser.identifyNum
     */
    public val identifyNum: String
        get() = source.identifyNum

    /**
     * 此成员所属频道ID
     */
    public val guildId: ID

    /**
     * 向当前频道对象发起一个新的聊天会话（私聊）并发送消息。
     *
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     *
     */
    @ST
    override suspend fun send(message: Message): KookMessageReceipt

    /**
     * 向当前频道对象发起一个新的聊天会话（私聊）并发送消息。
     *
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     *
     */
    @ST
    override suspend fun send(messageContent: MessageContent): KookMessageReceipt

    /**
     * 向当前频道对象发起一个新的聊天会话（私聊）并发送消息。
     *
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     *
     */
    @ST
    override suspend fun send(text: String): KookMessageReceipt

    /**
     * 踢出此成员。bot 应当具有相应的权限。
     *
     * @throws NoSuchElementException 无法找到删除目标，例如此成员已经不在频道中
     * @throws DeleteFailureException 删除失败，
     * 例如因没有权限而产生 [ApiResultException] 或 [ApiResponseException]
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption)

    //region mute API

    /**
     * 设置频道成员的**静音**状态。
     *
     * [type] 的值可参考静音API [CreateGuildMuteApi] 的参数 `type` 或 [MuteType]。
     *
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     *
     */
    @ST
    public suspend fun mute(type: Int): Boolean

    /**
     * 设置频道成员的**静音**状态。
     *
     * [type] 的值可参考静音API [CreateGuildMuteApi] 的参数 `type` 或 [MuteType]。
     *
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     *
     */
    @ST
    public suspend fun mute(type: MuteType): Boolean = mute(type.value)

    /**
     * 设置频道成员的**静音**状态。
     *
     * [type] 的值可参考静音API [CreateGuildMuteApi] 的参数 `type` 或 [MuteType]。
     *
     * ### 有效期
     *
     * [duration] 可以在**程序内存**级别追加一个“有效期”，当 [duration] 实际持续时间的**毫秒**大于0时有效。
     *
     * [duration] 是在当前程序内通过内部延迟完成的。当周期时间到达时，会主动调用取消禁言的API来实现“有效期”功能。
     *
     * 因此需要注意：[duration] 提供的"有效期"能力 _**不可靠**_ ，它有可能会因为如下原因而导致无法正确取消指定用户的静音状态：
     * - 程序中断
     * - Bot终止
     * - 成员退出服务器（如果在有效期内再次返回频道，不会恢复静音状态，也不会有取消静音的任务被执行）
     * - 取消禁言时BOT已经不再拥有相关权限
     * - 其他预料之外的情况
     *
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     *
     */
    @JvmSynthetic
    @ExperimentalSimbotAPI
    public suspend fun mute(type: Int, duration: Duration): Boolean

    /**
     * 设置频道成员的**静音**状态。
     *
     * [type] 的值可参考静音API [CreateGuildMuteApi] 的参数 `type` 或 [MuteType]。
     *
     * ### 有效期
     *
     * [duration] 可以在**程序内存**级别追加一个“有效期”，当 [duration] 实际持续时间的**毫秒**大于0时有效。
     *
     * [duration] 是在当前程序内通过内部延迟完成的。当周期时间到达时，会主动调用取消禁言的API来实现“有效期”功能。
     *
     * 因此需要注意：[duration] 提供的"有效期"能力 _**不可靠**_ ，它有可能会因为如下原因而导致无法正确取消指定用户的静音状态：
     * - 程序中断
     * - Bot终止
     * - 成员退出服务器（如果在有效期内再次返回频道，不会恢复静音状态，也不会有取消静音的任务被执行）
     * - 取消禁言时BOT已经不再拥有相关权限
     * - 其他预料之外的情况
     *
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     *
     */
    @JvmSynthetic
    @ExperimentalSimbotAPI
    public suspend fun mute(type: MuteType, duration: Duration): Boolean = mute(type.value, duration)

    /**
     * 设置频道成员的**静音**状态。
     *
     * [type] 的值可参考静音API [CreateGuildMuteApi] 的参数 `type` 或 [MuteType]。
     *
     * ### 有效期
     *
     * [time] 和 [timeUnit] 可以在**程序内存**级别追加一个“有效期”，当 [time] 和 [timeUnit] 实际持续时间的**毫秒**大于0时有效。
     *
     * [time] 和 [timeUnit] 是在当前程序内通过内部延迟完成的。当周期时间到达时，会主动调用取消禁言的API来实现“有效期”功能。
     *
     * 因此需要注意：[time] 和 [timeUnit] 提供的"有效期"能力 _**不可靠**_ ，它有可能会因为如下原因而导致无法正确取消指定用户的静音状态：
     * - 程序中断
     * - Bot终止
     * - 成员退出服务器（如果在有效期内再次返回频道，不会恢复静音状态，也不会有取消静音的任务被执行）
     * - 取消禁言时BOT已经不再拥有相关权限
     * - 其他预料之外的情况
     *
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     *
     */
    @ST
    @ExperimentalSimbotAPI
    public suspend fun mute(type: Int, time: Long, timeUnit: TimeUnit): Boolean

    /**
     * 设置频道成员的**静音**状态。
     *
     * [type] 的值可参考静音API [CreateGuildMuteApi] 的参数 `type` 或 [MuteType]。
     *
     * ### 有效期
     *
     * [time] 和 [timeUnit] 可以在**程序内存**级别追加一个“有效期”，当 [time] 和 [timeUnit] 实际持续时间的**毫秒**大于0时有效。
     *
     * [time] 和 [timeUnit] 是在当前程序内通过内部延迟完成的。当周期时间到达时，会主动调用取消禁言的API来实现“有效期”功能。
     *
     * 因此需要注意：[time] 和 [timeUnit] 提供的"有效期"能力 _**不可靠**_ ，它有可能会因为如下原因而导致无法正确取消指定用户的静音状态：
     * - 程序中断
     * - Bot终止
     * - 成员退出服务器（如果在有效期内再次返回频道，不会恢复静音状态，也不会有取消静音的任务被执行）
     * - 取消禁言时BOT已经不再拥有相关权限
     * - 其他预料之外的情况
     *
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     *
     */
    @ST
    @ExperimentalSimbotAPI
    public suspend fun mute(type: MuteType, time: Long, timeUnit: TimeUnit): Boolean = mute(type.value, time, timeUnit)

    /**
     * 设置频道成员的**麦克风**静音状态。
     *
     * 更多详细描述参考存在 `type` 参数的 [mute] API。
     *
     * @see mute
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     */
    @JvmSynthetic
    @ExperimentalSimbotAPI
    public suspend fun mute(duration: Duration): Boolean = mute(DEFAULT_MUTE_TYPE, duration)

    /**
     * 设置频道成员的**麦克风**静音状态。
     *
     * 更多详细描述参考存在 `type` 参数的 [mute] API。
     *
     * @see mute
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     */
    @ST
    @ExperimentalSimbotAPI
    public suspend fun mute(time: Long, timeUnit: TimeUnit): Boolean = mute(DEFAULT_MUTE_TYPE, time, timeUnit)

    /**
     * 取消频道成员的静音状态。
     *
     * [type] 的值可参考静音API [CreateGuildMuteApi] 的参数 `type` 或 [MuteType]。
     *
     * 当取消一个成员的某类型静音状态时，也会同时清除可能由 [mute] 的"有效期"能力而产生的延时任务。
     *
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     */
    @ST
    public suspend fun unmute(type: Int): Boolean

    /**
     * 取消频道成员的静音状态。
     *
     * [type] 的值可参考静音API [CreateGuildMuteApi] 的参数 `type` 或 [MuteType]。
     *
     * 当取消一个成员的某类型静音状态时，也会同时清除可能由 [mute] 的"有效期"能力而产生的延时任务。
     *
     * @throws ApiResultException API 请求过程中产生的异常
     * @throws ApiResponseException API 请求过程中产生的异常
     */
    @ST
    public suspend fun unmute(type: MuteType): Boolean = unmute(type.value)

    /**
     * 取消频道成员的**麦克风**静音状态。
     *
     * 更多详细描述参考存在 `type` 参数的 [unmute] API。
     *
     */
    @ST
    public suspend fun unmute(): Boolean = unmute(DEFAULT_MUTE_TYPE)
    //endregion

    /**
     * 获取此成员所拥有的所有角色。
     *
     * _Note: [roles] 尚在实验阶段，可能会在未来做出变更。_
     */
    @ExperimentalSimbotAPI
    public val roles: Collectable<KookMemberRole>


    public companion object {
        /**
         * `mute` API 使用的默认 `type` 值。
         */
        public const val DEFAULT_MUTE_TYPE: Int = 1

    }
}

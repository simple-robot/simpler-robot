/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.actor

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.common.time.TimeUnit
import love.forte.simbot.component.onebot.common.annotations.OneBotInternalImplementationsOnly
import love.forte.simbot.component.onebot.v11.common.utils.qqAvatar640
import love.forte.simbot.component.onebot.v11.core.api.*
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt
import love.forte.simbot.definition.Member
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.suspendrunner.ST
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic
import kotlin.time.Duration


/**
 * OneBot中的 `member` 的 [Member] 实现。
 *
 * 群成员通常的来源：
 * - 来自事件
 * - 来自 Group API
 * ([OneBotGroup.members])
 *
 * ## DeleteSupport
 *
 * [OneBotMember] 实现 [DeleteSupport]，
 * [delete] 代表试着踢出这个群成员。
 *
 * @author ForteScarlet
 */
@OneBotInternalImplementationsOnly
public interface OneBotMember : Member, DeleteSupport, OneBotStrangerAware {
    /**
     * 协程上下文。源自 [OneBotBot], 但是不含 [Job][kotlinx.coroutines.Job]。
     */
    override val coroutineContext: CoroutineContext

    /**
     * 成员群号
     */
    override val id: ID

    /**
     * 此成员所属角色。
     * 如果无法获取（例如是在群临时会话的私聊中）则得到 `null`
     *
     * 值有可能被 [setAdmin] 所影响。
     *
     * @see setAdmin
     */
    public val role: OneBotMemberRole?

    /**
     * 成员QQ头像
     */
    override val avatar: String
        get() = qqAvatar640(id.literal)

    /**
     * 群成员的QQ名。
     */
    override val name: String

    /**
     * 群成员在群内的昵称，
     * 即群成员在群里的群备注，也就是 `card`。
     * 如果备注为空字符串，则会被视为 `null`，也就是没有备注。
     *
     * 不支持获取的情况下也会得到 `null`，例如在临时会话中。
     */
    override val nick: String?

    /**
     * 设置成员在此群内的群备注。
     *
     * 当 [setNick] 修改成功后会影响 [nick] 的值，
     * 但是仅会影响 **当前对象** 内的属性值。
     *
     * [setNick] 不保证并发安全也不会加锁，
     * 如果并发请求 [setNick]，无法保证 [nick] 的最终结果。
     *
     * @see nick
     *
     * @param newNick 要设置的新备注, `null` 或空字符串代表删除备注
     * @throws Throwable 任何在请求API过程中可能产生的异常
     */
    @ST
    public suspend fun setNick(newNick: String?)

    /**
     * 获取当前成员在API [GetGroupMemberInfoApi] 中的响应结果。
     * 根据 [OneBotMember] 具体实现的不同，可能会发起API请求
     * （例如 [OneBotMember] 来自事件），
     * 也可能会直接获取缓存信息（例如 [OneBotMember] 来自 [OneBotGroup.member]）。
     *
     * 如果会发起API请求，那么每次调用**都会**发起请求，不会进行缓存。
     *
     * @see GetGroupMemberInfoApi
     *
     * @throws Throwable 如果会发起请求，则任何可能在请求API时得到的异常
     */
    @ST
    public suspend fun getSourceMemberInfo(): GetGroupMemberInfoResult

    /**
     * 使用 [SetGroupSpecialTitleApi] 为群成员设置专属头衔。
     * 通常要求bot拥有群主权限。
     *
     * 如果想要获取当前成员的专属头衔，使用 [getSourceMemberInfo]
     * 后可通过 [GetGroupMemberInfoResult.title] 获取。
     *
     * @param specialTitle 要设置的新头衔。为空或为 `null` 则代表取消头衔。
     * @throws Throwable 任何可能在请求API过程中产生的异常。
     */
    @ST
    public suspend fun setSpecialTitle(specialTitle: String? = null)

    /**
     * 使用 [SetGroupSpecialTitleApi] 为群成员设置专属头衔。
     * 通常要求bot拥有群主权限。
     *
     * 如果想要获取当前成员的专属头衔，使用 [getSourceMemberInfo]
     * 后可通过 [GetGroupMemberInfoResult.title] 获取。
     *
     * @param specialTitle 要设置的新头衔。为空或为 `null` 则代表取消头衔。
     * @param duration 有效期，最终取秒值，如果小于0则会被忽略。
     * 不一定有效果，更多说明参考 [SetGroupSpecialTitleApi] 和 [SetGroupSpecialTitleApi.create]。
     * @param timeUnit [duration] 的时间单位。
     * @throws Throwable 任何可能在请求API过程中产生的异常。
     */
    @ST
    @JvmSynthetic
    public suspend fun setSpecialTitle(specialTitle: String, duration: Long, timeUnit: TimeUnit)

    /**
     * 使用 [SetGroupSpecialTitleApi] 为群成员设置专属头衔。
     * 通常要求bot拥有群主权限。
     *
     * 如果想要获取当前成员的专属头衔，使用 [getSourceMemberInfo]
     * 后可通过 [GetGroupMemberInfoResult.title] 获取。
     *
     * @param specialTitle 要设置的新头衔。为空或为 `null` 则代表取消头衔。
     * @param duration 有效期，最终取秒值，如果小于0则会被忽略。
     * 不一定有效果，更多说明参考 [SetGroupSpecialTitleApi] 和 [SetGroupSpecialTitleApi.create]。
     * @throws Throwable 任何可能在请求API过程中产生的异常。
     */
    @JvmSynthetic
    public suspend fun setSpecialTitle(specialTitle: String, duration: Duration)

    /**
     * 向此成员发送消息。
     *
     * @throws Throwable 任何可能在请求API时得到的异常
     */
    @ST
    override suspend fun send(text: String): OneBotMessageReceipt

    /**
     * 向此成员发送消息。
     *
     * @throws Throwable 任何可能在请求API时得到的异常
     */
    @ST
    override suspend fun send(message: Message): OneBotMessageReceipt

    /**
     * 向此成员发送消息。
     *
     * @throws Throwable 任何可能在请求API时得到的异常
     */
    @ST
    override suspend fun send(messageContent: MessageContent): OneBotMessageReceipt

    /**
     * 尝试踢出此群成员，类似于 `kick` 行为。
     *
     * @param options 删除时可提供的额外选项。
     * 可用：
     * - [StandardDeleteOption.IGNORE_ON_FAILURE]
     * - [OneBotMemberDeleteOption] 的所有实现
     *
     * @throws Throwable 任何可能在请求API时得到的异常
     * @throws IllegalStateException 某些无法得知群号的情况下抛出的异常
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption)

    /**
     * 禁言此成员。
     * 禁言时长范围应当在 1m ~ 30d 之间，
     * 不过 [duration] 最终会被转为秒值。
     * 如果值为 [Duration.ZERO] 或最终秒值为 `0` 则等同于 [unban]，
     * 如果转化后的秒值为负数，则会抛出 [IllegalArgumentException]。
     *
     * 不会在代码中校验时长的最大有效值，这会直接交给OneBot服务端处理。
     *
     * @param duration 禁言时长。
     * 通常来讲，时间范围应当在 1m ~ 30d 之间。
     * 如果值为 [Duration.ZERO] 则等同于 [unban]。
     *
     * @throws IllegalArgumentException 如果参数代表的秒值 **小于0**
     * @throws IllegalStateException 某些无法得知群号的情况下抛出的异常
     * @throws Throwable 任何请求API时可能得到的异常
     *
     * @see SetGroupBanApi
     *
     */
    @JvmSynthetic
    public suspend fun ban(duration: Duration)

    /**
     * 禁言此成员。
     * 禁言时长范围应当在 1m ~ 30d 之间，
     * 不过 [duration] 最终会根据 [unit] 被转为秒值。
     * 如果最终秒值为 `0` 则等同于 [unban]，
     * 如果 [duration] 为负数，则会抛出 [IllegalArgumentException]。
     *
     * 不会在代码中校验时长的最大有效值，这会直接交给OneBot服务端处理。
     *
     * @param duration 禁言时长
     * @param unit [duration] 的时间单位
     *
     * @throws IllegalArgumentException 如果参数代表的秒值 **小于0**
     * @throws IllegalStateException 某些无法得知群号的情况下抛出的异常
     * @throws Throwable 任何请求API时可能得到的异常
     *
     * @see SetGroupBanApi
     *
     */
    @ST
    @JvmSynthetic
    public suspend fun ban(duration: Long, unit: TimeUnit)

    /**
     * 取消此成员禁言。相当于 `ban(Duration.ZERO)`。
     *
     * @throws IllegalStateException 某些无法得知群号的情况下抛出的异常
     * @throws Throwable 任何请求API时可能得到的异常
     */
    @ST
    public suspend fun unban() {
        ban(Duration.ZERO)
    }

    /**
     * 设置当前群成员为管理员/撤销其管理员。
     *
     * 会根据 [enable] 在请求成功后影响 [role] 的值，
     * 但是仅会影响 **当前对象** 内的属性值。
     *
     * [setAdmin] 不保证并发安全也不会加锁，
     * 如果并发请求 [setAdmin]，无法保证 [role] 的最终结果。
     *
     * @see OneBotGroup.setAdmin
     * @see SetGroupAdminApi
     * @param enable 为 `true` 则为设置管理，`false` 则为取消管理。
     * @throws Throwable 任何在请求API过程中可能产生的异常
     * @throws IllegalStateException 无法获取到所属群时抛出，例如在临时会话中。
     */
    @ST
    public suspend fun setAdmin(enable: Boolean)
}

/**
 * 在 [OneBotMember.delete] 中可以使用的更多额外属性，
 * 大多与 [SetGroupKickApi] 中的属性相关。
 *
 * @see OneBotMember.delete
 */
public sealed class OneBotMemberDeleteOption : DeleteOption {

    /**
     * 拒绝此人的加群请求，也就是踢出后将其屏蔽。
     *
     * @see SetGroupKickApi
     */
    public data object RejectRequest : OneBotMemberDeleteOption()

    public companion object {
        /**
         * 拒绝此人的加群请求，也就是踢出后将其屏蔽。
         *
         * @see SetGroupKickApi
         * @see RejectRequest
         */
        @get:JvmStatic
        @get:JvmName("rejectRequest")
        public val rejectRequest: RejectRequest
            get() = RejectRequest

    }
}

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
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.onebot.common.annotations.OneBotInternalImplementationsOnly
import love.forte.simbot.component.onebot.v11.core.api.*
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt
import love.forte.simbot.definition.ChatGroup
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic


/**
 * OneBot中的 `group` 的 [ChatGroup] 实现。
 *
 * 群通常的来源：
 * - 来自事件
 * - 来自 Bot relation API
 * ([OneBotBot.groupRelation][love.forte.simbot.component.onebot.v11.core.bot.OneBotBot.groupRelation])
 *
 * ## DeleteSupport
 *
 * [OneBotGroup] 实现 [DeleteSupport]，
 * [delete] 代表使Bot退出此群或解散群聊。
 *
 *
 * @author ForteScarlet
 */
@OneBotInternalImplementationsOnly
public interface OneBotGroup : ChatGroup, DeleteSupport {
    /**
     * 协程上下文。源自 [OneBotBot], 但是不含 [Job][kotlinx.coroutines.Job]。
     */
    override val coroutineContext: CoroutineContext

    /**
     * 群号
     */
    override val id: ID

    /**
     * 群名称。
     *
     * 值可能会被 [setName] 所影响。
     *
     * @see setName
     */
    override val name: String

    /**
     * 设置群名。
     *
     * 当 [setName] 修改成功后会影响 [name] 的值，
     * 但是仅会影响 **当前对象** 内的属性值。
     *
     * [setName] 不保证并发安全也不会加锁，
     * 如果并发请求 [setName]，无法保证 [name] 的最终结果。
     *
     * @see name
     * @param newName 要设置的新群名
     *
     * @throws Throwable 任何在请求API过程中可能产生的异常
     */
    @ST
    public suspend fun setName(newName: String)

    /**
     * 群内的全部角色权限。
     * 即 [OneBotMemberRole]
     * 的枚举元素。
     *
     * @see OneBotMemberRole
     */
    override val roles: Collectable<OneBotMemberRole>
        get() = OneBotMemberRole.entries.asCollectable()

    /**
     * 获取群内所有成员集合。
     */
    override val members: Collectable<OneBotMember>

    /**
     * 根据ID寻找指定的成员。
     */
    @ST(blockingBaseName = "getMember", blockingSuffix = "", asyncBaseName = "getMember", reserveBaseName = "getMember")
    override suspend fun member(id: ID): OneBotMember?

    /**
     * 成员数。
     */
    public val memberCount: Int

    /**
     * 最大成员数（群容量）。
     */
    public val maxMemberCount: Int

    /**
     * 将当前所属Bot作为一个 [OneBotMember] 获取。
     *
     * @throws IllegalStateException 如果最终没有找到bot的user id的用户信息，
     * 例如可能bot已经离开了这个群
     * @throws Exception 请求API过程中可能产生的任何异常
     */
    @STP
    override suspend fun botAsMember(): OneBotMember

    /**
     * 使用 [SetGroupAnonymousApi] 设置群组匿名(的开关)。
     *
     * @param enable 是否开启(允许)匿名聊天。true为允许。
     * @throws Throwable 任何可能在请求API过程中产生的异常。
     */
    @ST
    public suspend fun setAnonymous(enable: Boolean)

    /**
     * 向此群内发送消息。
     *
     * @throws Throwable 任何可能在请求API时产生的异常
     */
    @ST
    override suspend fun send(message: Message): OneBotMessageReceipt

    /**
     * 向此群内发送消息。
     *
     * @throws Throwable 任何可能在请求API时产生的异常
     */
    @ST
    override suspend fun send(messageContent: MessageContent): OneBotMessageReceipt

    /**
     * 向此群内发送消息。
     *
     * @throws Throwable 任何可能在请求API时产生的异常
     */
    @ST
    override suspend fun send(text: String): OneBotMessageReceipt

    /**
     * 让当前bot退出/离开此群。
     *
     * @param options 进行删除行为时的额外属性。
     * 支持：
     * - [StandardDeleteOption.IGNORE_ON_FAILURE]
     * - [OneBotGroupDeleteOption] 的所有子实现
     *
     * @throws Throwable 任何可能在请求API时产生的异常
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption)

    /**
     * 设置全群禁言。
     *
     * @param enable 是开启还是关闭全群禁言
     *
     * @throws Throwable 任何请求API过程中可能产生的异常
     */
    @ST
    public suspend fun ban(enable: Boolean)

    /**
     * 设置 bot 在此群内的群备注。
     *
     * @see OneBotMember.nick
     *
     * @param newNick 要设置的新备注, `null` 或空字符串代表删除备注
     * @throws Throwable 任何在请求API过程中可能产生的异常
     */
    @ST
    public suspend fun setBotGroupNick(newNick: String?)

    /**
     * 设置指定的群成员为管理员/撤销其管理员。
     *
     * @see OneBotMember.setAdmin
     * @see SetGroupAdminApi
     * @param memberId 目标成员的ID
     * @param enable 为 `true` 则为设置管理，`false` 则为取消管理。
     * @throws Throwable 任何在请求API过程中可能产生的异常
     */
    @ST
    public suspend fun setAdmin(memberId: ID, enable: Boolean)

    /**
     * 通过 [GetGroupHonorInfoApi] 获取本群的荣誉信息。
     *
     * @throws Throwable 任何请求API过程可能产生的异常
     * @see getAllHonorInfo
     * @see GetGroupHonorInfoApi
     */
    @ST
    public suspend fun getHonorInfo(type: String): GetGroupHonorInfoResult

    /**
     * 通过 [GetGroupHonorInfoApi] 获取本群的所有荣誉信息 (即 `type="all"`)。
     *
     * @throws Throwable 任何请求API过程可能产生的异常
     * @see getHonorInfo
     * @see GetGroupHonorInfoApi
     */
    @ST
    public suspend fun getAllHonorInfo(): GetGroupHonorInfoResult =
        getHonorInfo(GetGroupHonorInfoApi.TYPE_ALL)

}

/**
 * 在 [OneBotGroup.delete] 中可选择使用的额外属性。
 */
public sealed class OneBotGroupDeleteOption : DeleteOption {
    /**
     * 是否为解散群。如果bot为群主，
     * 则需要提供此参数来使 [OneBotGroup.delete] 解散群，
     * 否则无法解散或退出。
     *
     * 更多参考：[SetGroupLeaveApi]
     *
     * @see SetGroupLeaveApi
     */
    public data object Dismiss : OneBotGroupDeleteOption()

    public companion object {
        /**
         * 是否为解散群。
         * 更多参考见 [Dismiss]。
         *
         * @see Dismiss
         */
        @get:JvmStatic
        @get:JvmName("dismiss")
        public val dismiss: Dismiss
            get() = Dismiss

    }
}

/**
 * 开启全群禁言。
 */
@JvmSynthetic
public suspend fun OneBotGroup.ban() {
    ban(true)
}

/**
 * 取消全群禁言。
 */
@JvmSynthetic
public suspend fun OneBotGroup.unban() {
    ban(false)
}

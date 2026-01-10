/*
 * Copyright (c) 2022-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.guild

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.TimeUnit
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.component.qguild.message.QGContentText
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.role.QGMemberRole
import love.forte.simbot.component.qguild.utils.toTimestamp
import love.forte.simbot.definition.Member
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.Text
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.MessageAuditedException
import love.forte.simbot.qguild.api.guild.mute.MuteMemberApi
import love.forte.simbot.qguild.api.role.GetGuildRoleListApi
import love.forte.simbot.qguild.model.Role
import love.forte.simbot.qguild.model.Role.Companion.isDefault
import love.forte.simbot.suspendrunner.ST
import kotlin.jvm.JvmSynthetic
import kotlin.time.Duration
import love.forte.simbot.qguild.model.Member as QGSourceMember

/**
 *
 * @author ForteScarlet
 */
public interface QGMember : Member, CoroutineScope, QGObjectiveContainer<QGSourceMember> {
    /**
     * 成员的用户ID
     */
    override val id: ID
        get() = source.user.id.ID

    /**
     * 成员的用户头像
     */
    override val avatar: String
        get() = source.user.avatar

    /**
     * 用户名
     */
    override val name: String
        get() = source.user.username

    /**
     * 成员在频道中的昵称
     */
    override val nick: String
        get() = source.nick

    /**
     * 用户加入频道的时间
     *
     */
    @OptIn(ExperimentalQGApi::class)
    public val joinTime: Timestamp
        get() = source.joinedAt.toTimestamp()

    /**
     * 成员 [source] 拥有的身分组ID集合。
     *
     * @see QGSourceMember.roles
     */
    public val roleIds: List<String>
        get() = source.roles

    /**
     * 成员拥有的角色，会通过 [GetGuildRoleListApi] 进行查询。
     * 如果不希望请求API以避免出现无访问权限的情况，可考虑使用离线的 [defaultRoles]。
     */
    @ExperimentalQGApi
    public val roles: Collectable<QGMemberRole>

    /**
     * 从 [source.roles][QGSourceMember.roles] 中直接构建的离线的默认 [QGMemberRole] 列表。
     * 这个过程不会发起API请求，但是得到的 [QGMemberRole] 中可能存在部分属性缺失 (例如 `color`)，
     * 这些缺失的属性会使用一个默认值填充。
     *
     * @see Role.isDefault
     * @see Role.defaultRole
     */
    @ExperimentalQGApi
    public val defaultRoles: List<QGMemberRole>

    /**
     * 向目标成员发送私聊消息。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws UnsupportedOperationException 如果当前子频道类型不是文字子频道
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     */
    @ST
    override suspend fun send(message: Message): QGMessageReceipt

    /**
     * 向目标成员发送纯文本的私聊消息。
     *
     * [text] 的内容会根据 [内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)
     * 自动转义，发送出去的内容将会是原本的纯文本字符串，不会触发内嵌格式的特殊格式。
     *
     * 如果希望允许内嵌格式，请使用 [QGContentText] 消息类型而不是纯文本或 [Text] 。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws UnsupportedOperationException 如果当前子频道类型不是文字子频道
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     *
     * @see QGContentText
     */
    @ST
    override suspend fun send(text: String): QGMessageReceipt

    /**
     * 向目标成员发送私聊消息。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws UnsupportedOperationException 如果当前子频道类型不是文字子频道
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     */
    @ST
    override suspend fun send(messageContent: MessageContent): QGMessageReceipt

    /**
     * 禁言频道服务器中的此成员。
     *
     * 如果 [duration]  所代表的秒值为 `0`
     * 则代表取消禁言，同 [unmute]。
     *
     * 如果 [duration] 小于0则会抛出 [IllegalArgumentException]。
     *
     * @param duration 持续时间
     *
     * @see MuteMemberApi
     *
     * @throws QQGuildApiException 请求产生的异常
     * @throws IllegalArgumentException 如果 [duration] 小于0
     */
    @JvmSynthetic
    public suspend fun mute(duration: Duration)

    /**
     * 禁言频道服务器中的此成员。
     *
     * 如果 [duration] 在单位 [unit]
     * 下所代表的秒值为 `0` 则代表取消禁言，同 [unmute]。
     *
     * 如果 [duration] 小于0则会抛出 [IllegalArgumentException]。
     *
     * @param duration 持续时间
     * @param unit [duration] 的时间单位
     *
     * @see MuteMemberApi
     *
     * @throws QQGuildApiException 请求产生的异常
     * @throws IllegalArgumentException 如果 [duration] 小于0
     */
    @ST
    public suspend fun mute(duration: Long, unit: TimeUnit)

    /**
     * 取消此成员在频道服务器中的禁言状态。
     *
     * @throws QQGuildApiException 请求产生的异常
     */
    @ST
    public suspend fun unmute()
}

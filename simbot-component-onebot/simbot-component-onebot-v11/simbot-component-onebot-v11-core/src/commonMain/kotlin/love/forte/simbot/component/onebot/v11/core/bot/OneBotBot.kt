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

package love.forte.simbot.component.onebot.v11.core.bot

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.serialization.json.Json
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.ContactRelation
import love.forte.simbot.bot.GroupRelation
import love.forte.simbot.bot.GuildRelation
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.onebot.common.annotations.ExperimentalOneBotAPI
import love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI
import love.forte.simbot.component.onebot.common.annotations.OneBotInternalImplementationsOnly
import love.forte.simbot.component.onebot.v11.core.actor.OneBotFriend
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
import love.forte.simbot.component.onebot.v11.core.actor.OneBotStranger
import love.forte.simbot.component.onebot.v11.core.api.*
import love.forte.simbot.component.onebot.v11.message.OneBotMessageContent
import love.forte.simbot.event.EventResult
import love.forte.simbot.message.MessageReference
import love.forte.simbot.suspendrunner.ST
import org.intellij.lang.annotations.Language
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmSynthetic


/**
 * 一个 OneBot 协议的 [Bot]。
 *
 * ### Bot 的终止
 * Bot的运行状态 ([isActive]) 可能会因为一些原因被更改：
 * - 手动终止 (使用 [cancel])。
 * - 父级Job或被关联的Job被终止 (可能来自 BotManager、Application等)。
 * - 会话连接失败且重试次数到达上限。比如 ws 的连接会话意外被中断，
 * 且在重新连接的过程中始终失败并达到了重试次数上限，此时会话中的任务会被视为因异常结束，
 * 并连带 [OneBotBot] 的任务一同终结。
 *
 * ### 日志
 *
 * [OneBotBot] 的内部会输出三种日志：
 * - `love.forte.simbot.component.onebot.v11.core.bot.OneBotBot.$uniqueId`
 *   bot的基本日志，例如连接信息、接收到的原始事件等debug日志。
 *
 * @author ForteScarlet
 */
@OneBotInternalImplementationsOnly
public interface OneBotBot : Bot, OneBotApiExecutable {
    override val coroutineContext: CoroutineContext

    /**
     * [OneBotBot] 会使用的 [Json]。
     * 需要在
     */
    public val decoderJson: Json

    /**
     * 当前Bot的配置类。
     *
     * 配置信息在 [initConfiguration] 或 [start] 调用后的修改无效。
     */
    public val configuration: OneBotBotConfiguration

    /**
     * 根据 [configuration] 初始化部分配置信息。
     *
     * 在一些特殊情况下(例如测试或仅需要一些序列器等)，
     * 如果希望在不 [启动][start] bot 就初始化配置信息则使用此函数。
     *
     * 初始化配置只能执行一次。如果 [isConfigurationInitialized] 为 `true` 则会返回 `false`。
     * 同一时间只会有一个 [initConfiguration] 被执行。如果出现竞争则会挂起，
     * 直到其他竞争者完成初始化或出现异常。
     * 如果其他竞争者完成初始化，则会直接返回 `false`，不会重复初始化。
     *
     * [start] 中也会使用此函数。
     *
     * NOTE: 未来标准库 [Bot] 中添加了 `init` 相关函数和属性后会被废弃。
     * 参考 [#1071](https://github.com/simple-robot/simpler-robot/issues/1071)
     *
     * ### status
     *
     * 初始化状态有三个阶段：未初始化、正在初始化和完成初始化。
     * 根据三个状态的不同，会影响 [isConfigurationInitialized]
     * 和 [isConfigurationInitializing] 的值。
     *
     * | status | [isConfigurationInitializing] | [isConfigurationInitialized] |
     * | --- | --- | --- |
     * | 未初始化 | `false` | `false` |
     * | 初始化中 | `true` | `false` |
     * | 已初始化 | `false` | `true` |
     *
     * @throws RuntimeException 初始化过程中出现的任何非预期异常。
     * @return 如果已经初始化过了，则不会重复初始化，直接返回 `false`。
     * 否则在成功初始化后返回 `true`。
     *
     * @since 1.8.1
     */
    @ExperimentalOneBotAPI
    public suspend fun initConfiguration(): Boolean

    /**
     * 获取是否已经执行过 [initConfiguration]。
     * 如果 [initConfiguration] 尚在执行，会返回 `false`,
     * 如果 [initConfiguration] 已经成功，会得到 `true`。
     *
     * NOTE: 未来标准库 [Bot] 中添加了 `init` 相关函数和属性后会被废弃。
     * 参考 [#1071](https://github.com/simple-robot/simpler-robot/issues/1071)
     *
     * @see initConfiguration
     *
     * @since 1.8.1
     */
    @ExperimentalOneBotAPI
    public val isConfigurationInitialized: Boolean

    /**
     * 获取 [initConfiguration] 是否正在处于初始化过程中。
     * 如果 [initConfiguration] 已经成功或尚未开始，会得到 `false`，
     * 如果 [initConfiguration] 尚在执行，会返回 `true`。
     *
     * NOTE: 未来标准库 [Bot] 中添加了 `init` 相关函数和属性后会被废弃。
     * 参考 [#1071](https://github.com/simple-robot/simpler-robot/issues/1071)
     *
     * @see initConfiguration
     *
     * @since 1.8.1
     */
    @ExperimentalOneBotAPI
    public val isConfigurationInitializing: Boolean

    /**
     * 由 [OneBotBot] 衍生出的 actor 使用的 [CoroutineContext]。
     * 源自 [coroutineContext], 但是不包含 [Job][kotlinx.coroutines.Job]。
     */
    @InternalOneBotAPI
    public val subContext: CoroutineContext

    /**
     * [OneBotBot] 用于请求API的 [HttpClient]。
     */
    public val apiClient: HttpClient

    /**
     * [OneBotBot] 用于请求 API 的目标服务器地址
     */
    public val apiHost: Url

    /**
     * @suppress 分开使用 [apiAccessToken] 或 [eventAccessToken]。
     */
    @Deprecated(
        "Use `apiAccessToken` or `eventAccessToken`",
        ReplaceWith("apiAccessToken"),
        level = DeprecationLevel.HIDDEN
    )
    public val accessToken: String?
        get() = apiAccessToken

    /**
     * [OneBotBotConfiguration] 中配置的 [apiAccessToken][OneBotBotConfiguration.apiAccessToken]。
     */
    public val apiAccessToken: String?

    /**
     * [OneBotBotConfiguration] 中配置的 [eventAccessToken][OneBotBotConfiguration.eventAccessToken]。
     */
    public val eventAccessToken: String?

    /**
     * 当前Bot的唯一标识。
     * ## 唯一标识
     * 在OneBot中，Bot在启动并可以获悉 `self_id` 或 `user_id`
     * 之前，是没有一个明确的“唯一标识”的。
     * 因此，OneBot组件要求注册 Bot 时必须指定一个可以用于区分不同 Bot 的
     * [OneBotBotConfiguration.botUniqueId]，也就是此 [id]。
     *
     * 当注册bot时，会以 [OneBotBotConfiguration.botUniqueId] 作为检测冲突的标准。
     * 当Bot的 [userId] 被初始化后，可以通过 [isMe] 同时检测 [id] 或 [userId] 是否相同。
     *
     * @see OneBotBotConfiguration.botUniqueId
     */
    override val id: ID

    /**
     * 检测给定的 [id] 是否与 [OneBotBot.id] 或 [userId] (如果已经初始化)
     * 相同。
     *
     * 如果 [userId] 尚未初始化，则只检测 [OneBotBot.id]。
     */
    override fun isMe(id: ID): Boolean

    /**
     * Bot自身的 ID，来自事件或 [GetLoginInfoApi] API.
     * @throws IllegalStateException Bot的selfId尚未初始化，即Bot尚未启动。
     */
    public val userId: ID

    /**
     * Bot的名称，来自 [GetLoginInfoApi] API.
     * [name] 是一个缓存值，不会实时查询或实时刷新。
     *
     * 如果希望通过API查询登录信息或刷新此缓存，参考 [queryLoginInfo]。
     * @throws IllegalStateException Bot的selfId尚未初始化，即Bot尚未启动。
     */
    override val name: String

    /**
     * 通过 [GetLoginInfoApi] 查询当前Bot的信息，
     * 并刷新 [userId] 和 [name] 的缓存值。
     */
    @ST
    public suspend fun queryLoginInfo(): GetLoginInfoResult

    /**
     * 启动Bot。
     * 启动过程中会通过 [GetLoginInfoApi] 初始化当前账户信息，
     * 并同时初始化 [userId]。
     *
     * 启动时，会开始建立与 [OneBotBotConfiguration.eventServerHost] 的 WS 连接。
     * 如果在已经启动的情况下再次启动，则会关闭原本的连接后重新连接。
     */
    @JvmSynthetic
    override suspend fun start()

    /**
     * 联系人相关操作，即好友相关的关系操作。
     */
    override val contactRelation: OneBotBotFriendRelation

    /**
     * 与群聊相关的操作
     */
    override val groupRelation: OneBotBotGroupRelation

    /**
     * 始终为 `null`。
     * OB11协议主要为普通群聊设计。
     */
    override val guildRelation: GuildRelation?
        get() = null

    /**
     * 直接推送一个外部的原始事件字符串。
     * 你需要收集结果的 [Flow] 事件才会真正的被处理，
     * 或者使用 [pushAndLaunch]。
     *
     * 事件处理结果如果出现了异常结果会同内部事件一样输出异常日志。
     *
     * @throws IllegalArgumentException 如果事件解析失败
     */
    public fun push(@Language("json") rawEvent: String): Flow<EventResult>

    /**
     * 直接推送一个外部的原始事件字符串，并在异步任务中处理事件。
     *
     * @throws IllegalArgumentException 如果事件解析失败
     */
    public fun pushAndLaunch(@Language("json") rawEvent: String): Job =
        push(rawEvent).launchIn(this)

    /**
     * 获取 Cookies
     * @see GetCookiesApi
     * @throws Throwable 任何API请求过程中可能产生的异常
     */
    @ST
    public suspend fun getCookies(domain: String? = null): GetCookiesResult

    /**
     * 获取 QQ 相关接口凭证
     * @see GetCredentialsApi
     * @throws Throwable 任何API请求过程中可能产生的异常
     */
    @ST
    public suspend fun getCredentials(domain: String? = null): GetCredentialsResult

    /**
     * 获取 CSRF Token
     * @see GetCsrfTokenApi
     * @throws Throwable 任何API请求过程中可能产生的异常
     */
    @ST
    public suspend fun getCsrfToken(): GetCsrfTokenResult

    /**
     * 根据 [messageId] 使用 [GetMsgApi] 查询消息内容，
     * 并得到对应的 [OneBotMessageContent]。
     *
     * 注意：此API是实验性的，未来可能会随时被变更或删除。
     *
     * @see GetMsgApi
     * @throws RuntimeException 任何API请求过程中可能产生的异常，
     * 例如消息不存在或反序列化错误。
     */
    @ST
    @ExperimentalOneBotAPI
    public suspend fun getMessageContent(messageId: ID): OneBotMessageContent

    /**
     * 根据 [id] 使用 [GetMsgApi] 查询消息内容，
     * 并得到对应的 [OneBotMessageContent]。
     *
     * 注意：此API是实验性的，未来可能会随时被变更或删除，
     * 同 [getMessageContent]。
     *
     * @see GetMsgApi
     * @see getMessageContent
     * @throws RuntimeException 任何API请求过程中可能产生的异常，
     * 例如消息不存在或反序列化错误。
     *
     * @since 1.3.0
     */
    @ST
    @ExperimentalOneBotAPI
    override suspend fun messageFromId(id: ID): OneBotMessageContent = getMessageContent(id)

    /**
     * 根据消息引用的id使用 [GetMsgApi] 查询消息内容，
     * 并得到对应的 [OneBotMessageContent]。
     *
     * 注意：此API是实验性的，未来可能会随时被变更或删除，
     * 同 [getMessageContent]。
     *
     * @see getMessageContent
     *
     * @see GetMsgApi
     * @throws RuntimeException 任何API请求过程中可能产生的异常，
     * 例如消息不存在或反序列化错误。
     *
     * @since 1.3.0
     */
    @ST
    @ExperimentalOneBotAPI
    override suspend fun messageFromReference(reference: MessageReference): OneBotMessageContent =
        getMessageContent(id)

}

/**
 * 联系人相关操作，即好友相关的关系操作。
 *
 * @see OneBotBot.contactRelation
 */
@OneBotInternalImplementationsOnly
public interface OneBotBotFriendRelation : ContactRelation {
    /**
     * 获取好友列表。
     */
    override val contacts: Collectable<OneBotFriend>

    /**
     * 根据ID查询某个指定的好友。
     *
     * OneBot11协议中没有直接根据ID查询好友的API，
     * 因此会直接通过 [contacts]
     * 查询好友列表，并从内存中筛选。
     */
    @ST(
        blockingBaseName = "getContact",
        blockingSuffix = "",
        asyncBaseName = "getContact",
        reserveBaseName = "getContact"
    )
    override suspend fun contact(id: ID): OneBotFriend? =
        contacts.asFlow().firstOrNull { it.id == id }

    /**
     * 获取好友的数量。
     *
     * OneBot11中没有直接获取的API，因此会通过 [contacts]
     * 获取全部列表并从内存中计数来达成此功能。
     */
    @JvmSynthetic
    override suspend fun contactCount(): Int =
        contacts.asFlow().count()

    /**
     * 根据 [id] 查询对应的陌生人信息。
     *
     * @throws Throwable 请求API可能抛出的任何异常，
     * 例如不存在对应的账户信息。
     */
    @ST(
        blockingBaseName = "getStranger",
        blockingSuffix = "",
        asyncBaseName = "getStranger",
        reserveBaseName = "getStranger"
    )
    public suspend fun stranger(id: ID): OneBotStranger
}


/**
 * 与群聊相关的操作
 *
 * @see OneBotBot.groupRelation
 */
@OneBotInternalImplementationsOnly
public interface OneBotBotGroupRelation : GroupRelation {
    /**
     * 获取群列表
     */
    override val groups: Collectable<OneBotGroup>

    /**
     * 根据ID查询某个指定的群。
     */
    @ST(
        blockingBaseName = "getGroup",
        blockingSuffix = "",
        asyncBaseName = "getGroup",
        reserveBaseName = "getGroup"
    )
    override suspend fun group(id: ID): OneBotGroup?

    /**
     * 获取群数量。
     *
     * OneBot11中没有直接获取的API，因此会通过 [groups]
     * 获取全部列表并从内存中计数来达成此功能。
     */
    @JvmSynthetic
    override suspend fun groupCount(): Int =
        groups.asFlow().count()

    /**
     * 根据ID查询某个群中的某个成员。
     */
    @ST(
        blockingBaseName = "getMember",
        blockingSuffix = "",
        asyncBaseName = "getMember",
        reserveBaseName = "getMember"
    )
    public suspend fun member(groupId: ID, memberId: ID): OneBotMember?

}



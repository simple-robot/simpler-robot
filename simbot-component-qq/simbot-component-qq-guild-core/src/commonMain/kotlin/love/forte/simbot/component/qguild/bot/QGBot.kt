/*
 * Copyright (c) 2021-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.bot

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import love.forte.simbot.ability.EventMentionAware
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.ContactRelation
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.dms.QGDmsContact
import love.forte.simbot.component.qguild.event.QGAtMessageCreateEvent
import love.forte.simbot.component.qguild.event.QGGroupAtMessageCreateEvent
import love.forte.simbot.component.qguild.friend.QGFriend
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.group.QGGroupRelation
import love.forte.simbot.component.qguild.guild.QGGuildRelation
import love.forte.simbot.component.qguild.internal.message.QGMessageContentImpl
import love.forte.simbot.component.qguild.message.*
import love.forte.simbot.event.Event
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReference
import love.forte.simbot.qguild.ExperimentalQGMediaApi
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.MessageAuditedException
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.qguild.api.files.UploadGroupFilesApi
import love.forte.simbot.qguild.api.files.UploadUserFilesApi
import love.forte.simbot.qguild.api.message.GetMessageApi
import love.forte.simbot.qguild.stdlib.*
import love.forte.simbot.resource.Resource
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP
import kotlin.jvm.JvmSynthetic
import love.forte.simbot.qguild.model.User as QGSourceUser
import love.forte.simbot.qguild.stdlib.Bot as QGSourceBot

/**
 * 一个 [QQ频道Bot][QGSourceBot] 的 simbot组件实现接口，
 * 是一个具有功能的 QQ频道组件Bot。
 *
 * ### AppID 与 用户ID
 * [QGBot] 的 [id] 是用于连接服务器的 `appId` 而并非系统用户ID。
 * 如果希望获取bot的 **用户ID**, 至少执行过一次 [QGBot.start] 后通过 [userId] 获取，
 * 或者通过 [me] 实时查询。
 *
 * _Note: QGBot仅由内部实现，对外不稳定_
 *
 * @author ForteScarlet
 */
public interface QGBot : Bot, EventMentionAware {
    /**
     * QQ频道的 [组件][QQGuildComponent] 对象实例。
     */
    override val component: QQGuildComponent

    /**
     * 在QQ频道标准库中的原始Bot类型。
     */
    public val source: QGSourceBot

    /**
     * 当前bot的 **appId** 。
     *
     * 如果希望获取当前bot的**用户**ID，
     * 至少执行一次 [start] 后使用 [userId]，
     * 或者通过 []
     */
    override val id: ID
        get() = source.ticket.appId.ID

    /**
     * 得到当前bot的用户ID。
     *
     * [userId] 是当前bot的用户id，需要至少执行一次 [start] 来初始化，
     * 否则会导致 [IllegalStateException] 异常。
     *
     * @throws IllegalStateException bot没有通过 [start] 初始化用户信息.
     */
    public val userId: ID

    /**
     * bot的用户名
     *
     * 需要至少执行一次 [start] 来初始化，
     * 否则会导致 [IllegalStateException] 异常。
     *
     * @throws IllegalStateException bot没有通过 [start] 初始化用户信息.
     */
    override val name: String

    /**
     * QQ机器人有两个可能的唯一标识：作为bot的 [app id][id] 以及在系统中作为用户的 [user id][userId].
     *
     * 需要注意的是，作为普通用户时的id必须在 [start] 之后才能被检测。在 [start] 之前，将会只通过 [id] 进行检测。
     *
     */
    override fun isMe(id: ID): Boolean

    /**
     * 启动当前bot, 并且初始化此bot的信息。
     *
     * 启动时会通过 [me] 查询当前bot的信息，并为 [isMe] 提供额外id的匹配支持。
     *
     */
    @JvmSynthetic
    override suspend fun start()

    /**
     * bot的头像
     */
    public val avatar: String

    /**
     * 如果事件类型为
     * - [QGGroupAtMessageCreateEvent]
     * - [QGAtMessageCreateEvent]
     * 则直接视为bot被at了，不论消息中是否真的有at元素。
     */
    override fun isMention(event: Event): Boolean {
        return when (event) {
            is QGGroupAtMessageCreateEvent -> true
            is QGAtMessageCreateEvent -> true
            else -> false
        }
    }

    /**
     * QQ群相关内容的操作。
     */
    override val groupRelation: QGGroupRelation

    /**
     * QQ好友、单聊相关内容没有能支持获取它们的API，
     * 始终得到 `null`。
     */
    override val contactRelation: ContactRelation?
        get() = null

    /**
     * 对频道服务器、以及频道、分组等相关内容的操作。
     *
     */
    override val guildRelation: QGGuildRelation

    /**
     * 直接向目标子频道发送消息。
     *
     * 此频道需要为文字子频道，否则会产生异常，但是此异常不会由程序检测，
     * 而是通过API的错误响应 [QQGuildApiException] 体现。
     *
     * [sendTo] 相对于 [QGTextChannel.send] 而言更加“不可靠”
     * —— 因为它跳过了对频道服务器和对子频道类型的校验，失去了在消息中自动填充 `msgId` 等透明行为，并且直接使用ID也会存在一些细微的隐患。
     * 但是这可以有效规避当没有获取频道服务器或子频道信息权限时候可能导致的问题。
     *
     * 如有必要，请不要忘记添加 [QGReplyTo] 来指定一个用于回复标记的 `msgId`。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     *
     * @return 消息发送回执
     */
    @ST
    public suspend fun sendTo(channelId: ID, text: String): QGMessageReceipt

    /**
     * 直接向目标子频道发送消息。
     *
     * 此频道需要为文字子频道，否则会产生异常，但是此异常不会由程序检测，
     * 而是通过API的错误响应 [QQGuildApiException] 体现。
     *
     * [sendTo] 相对于 [QGTextChannel.send] 而言更加“不可靠”
     * —— 因为它跳过了对频道服务器和对子频道类型的校验，失去了在消息中自动填充 `msgId` 等透明行为，并且直接使用ID也会存在一些细微的隐患。
     * 但是这可以有效规避当没有获取频道服务器或子频道信息权限时候可能导致的问题。
     *
     * 如有必要，请不要忘记添加 [QGReplyTo] 来指定一个用于回复标记的 `msgId`。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     *
     * @return 消息发送回执
     */
    @ST
    public suspend fun sendTo(channelId: ID, message: Message): QGMessageReceipt

    /**
     * 直接向目标子频道发送消息。
     *
     * 此频道需要为文字子频道，否则会产生异常，但是此异常不会由程序检测，
     * 而是通过API的错误响应 [QQGuildApiException] 体现。
     *
     * [sendTo] 相对于 [QGTextChannel.send] 而言更加“不可靠”
     * —— 因为它跳过了对频道服务器和对子频道类型的校验，失去了在消息中自动填充 `msgId` 等透明行为，
     * 并且直接使用ID也会存在一些细微的隐患。
     * 但是这可以有效规避当没有获取频道服务器或子频道信息权限时候可能导致的问题。
     *
     * 如有必要，请不要忘记添加 [QGReplyTo] 来指定一个用于回复标记的 `msgId`。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     *
     * @return 消息发送回执
     */
    @ST
    public suspend fun sendTo(channelId: ID, message: MessageContent): QGMessageReceipt

    /**
     * 直接向目标DMS(频道私聊会话)发送消息。
     *
     * [sendDmsTo] 相对于 [QGDmsContact.send] 而言更加“不可靠”
     * —— 因为它失去了在消息中自动填充 `msgId` 等透明行为，
     * 且直接使用ID也会存在一些细微的隐患。
     *
     * 如有必要，请不要忘记添加 [QGReplyTo] 来指定一个用于回复标记的 `msgId`。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     *
     * @return 消息发送回执
     */
    @ST
    public suspend fun sendDmsTo(id: ID, text: String): QGMessageReceipt

    /**
     * 直接向目标DMS(频道私聊会话)发送消息。
     *
     * [sendDmsTo] 相对于 [QGDmsContact.send] 而言更加“不可靠”
     * —— 因为它失去了在消息中自动填充 `msgId` 等透明行为，
     * 且直接使用ID也会存在一些细微的隐患。
     *
     * 如有必要，请不要忘记添加 [QGReplyTo] 来指定一个用于回复标记的 `msgId`。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     *
     * @return 消息发送回执
     */
    @ST
    public suspend fun sendDmsTo(id: ID, message: Message): QGMessageReceipt

    /**
     * 直接向目标DMS(频道私聊会话)发送消息。
     *
     * [sendDmsTo] 相对于 [QGDmsContact.send] 而言更加“不可靠”
     * —— 因为它失去了在消息中自动填充 `msgId` 等透明行为，
     * 且直接使用ID也会存在一些细微的隐患。
     *
     * 如有必要，请不要忘记添加 [QGReplyTo] 来指定一个用于回复标记的 `msgId`。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     *
     * @return 消息发送回执
     */
    @ST
    public suspend fun sendDmsTo(id: ID, message: MessageContent): QGMessageReceipt


    /**
     * 获取当前bot对应的用户信息。
     *
     * 如果 [withCache] 为 `true` 且当前bot内存在上一次查询的结果缓存，
     * 则直接得到缓存，否则通过API查询。
     *
     * 通过API查询的结果会刷新至当前的内部缓存。
     *
     * @return API得到的用户信息结果
     */
    @ST(blockingBaseName = "getMe", blockingSuffix = "", asyncBaseName = "getMe", reserveBaseName = "getMe")
    public suspend fun me(withCache: Boolean): QGSourceUser

    /**
     * 通过API实时查询当前bot对应的用户信息。
     *
     * 通过API查询的结果会刷新至当前的内部缓存。
     *
     * @return API得到的用户信息结果
     */
    @STP
    public suspend fun me(): QGSourceUser = me(false)

    /**
     * 上传一个资源为用于向QQ群发送的 [QGMedia], 可用于后续的发送。
     *
     * 目前上传仅支持使用链接，QQ平台会对此链接进行转存。
     *
     * @param target 目标群的ID
     * @param url 目标链接
     * @param type 媒体类型。
     *
     * > 1 图片，2 视频，3 语音，4 文件（暂不开放） 资源格式要求: 图片：png/ jpg，视频：mp4，语音：silk
     *
     * 参考 [UploadGroupFilesApi]。
     *
     * @see QGGroup.uploadMedia
     */
    @ST
    public suspend fun uploadGroupMedia(
        target: ID,
        url: String,
        type: Int,
    ): QGMedia

    /**
     * 上传一个资源为用于向QQ单聊发送的 [QGMedia], 可用于后续的发送。
     *
     * 目前上传仅支持使用链接，QQ平台会对此链接进行转存。
     *
     * @param target 目标用户的ID
     * @param url 目标链接
     * @param type 媒体类型。
     *
     * > 1 图片，2 视频，3 语音，4 文件（暂不开放） 资源格式要求: 图片：png/ jpg，视频：mp4，语音：silk
     *
     * 参考 [UploadUserFilesApi]。
     *
     * @see QGFriend.uploadMedia
     */
    @ST
    public suspend fun uploadUserMedia(
        target: ID,
        url: String,
        type: Int,
    ): QGMedia

    /**
     * 上传一个资源为用于向QQ群发送的 [QGMedia], 可用于后续的发送。
     *
     * @param target 目标群的ID
     * @param resource 目标数据。如果是 `URIResource` 则会使用 `url`，否则通过 `file_data` 上传。
     * @param type 媒体类型。
     *
     * > 1 图片，2 视频，3 语音，4 文件（暂不开放） 资源格式要求: 图片：png/ jpg，视频：mp4，语音：silk
     *
     * 参考 [UploadGroupFilesApi]。
     *
     * @see QGGroup.uploadMedia
     *
     * @throws IllegalStateException 如果无法从 [resource] 中读取数据，异常会被包装在 [IllegalStateException] 中。
     * @throws Exception 任何可能在发送API时产生的异常
     *
     * @since 4.1.1
     */
    @ST
    @ExperimentalQGMediaApi
    public suspend fun uploadGroupMedia(
        target: ID,
        resource: Resource,
        type: Int,
    ): QGMedia

    /**
     * 上传一个资源为用于向QQ单聊发送的 [QGMedia], 可用于后续的发送。
     *
     * @param target 目标用户的ID
     * @param resource 目标数据。如果是 `URIResource` 则会使用 `url`，否则通过 `file_data` 上传。
     * @param type 媒体类型。
     *
     * > 1 图片，2 视频，3 语音，4 文件（暂不开放） 资源格式要求: 图片：png/ jpg，视频：mp4，语音：silk
     *
     * 参考 [UploadUserFilesApi]。
     *
     * @see QGFriend.uploadMedia
     *
     * @throws IllegalStateException 如果无法从 [resource] 中读取数据，异常会被包装在 [IllegalStateException] 中。
     * @throws Exception 任何可能在发送API时产生的异常
     *
     * @since 4.1.1
     */
    @ST
    @ExperimentalQGMediaApi
    public suspend fun uploadUserMedia(
        target: ID,
        resource: Resource,
        type: Int,
    ): QGMedia

    /**
     * 执行(请求) [api] 并得到原始的响应 [HttpResponse].
     */
    @ST
    @ExperimentalQGApi
    public suspend fun execute(api: QQGuildApi<*>): HttpResponse =
        api.requestBy(source)

    /**
     * 执行(请求) [api] 并得到响应体字符串.
     *
     * @throws RuntimeException 通过 [HttpResponse] 获取响应体时可能出现的异常
     */
    @ST
    @ExperimentalQGApi
    public suspend fun executeText(api: QQGuildApi<*>): String =
        api.requestTextBy(source)

    /**
     * 执行(请求) [api] 并得到响应体 [R].
     *
     * @throws RuntimeException 通过 [HttpResponse] 获取响应体时可能出现的异常或解析响应体时可能产生的异常。
     */
    @ST
    @ExperimentalQGApi
    public suspend fun <R : Any> executeData(api: QQGuildApi<R>): R =
        api.requestDataBy(source)

    /**
     * 无法仅根据消息id查询引用，将始终抛出 [UnsupportedOperationException]。
     * 考虑使用 [messageFromReference] 或可提供 `channelId` 信息的 [messageFromId]。
     */
    @ST
    override suspend fun messageFromId(id: ID): QGMessageContent {
        throw UnsupportedOperationException(
            "Cannot query message from `messageId` only. " +
                    "Use the QGBot.messageFromId(channelId, messageId) or QGBot.messageFromReference(QGReference) plz."
        )
    }

    /**
     * 根据引用信息 [reference] 查询指定的**文字子频道**消息正文。
     * [reference] 必须是 [QGReference] 类型，并且不能缺失 [QGReference.channelId]，
     * 因为查询消息内容除了 `messageId` 以外还需要 `channelId` 信息。
     *
     * @throws UnsupportedOperationException 如果 [reference] 类型不是 [QGReference]
     * @throws IllegalArgumentException 如果 [reference] 的 [channelId][QGReference.channelId] 为 `null`
     */
    @ST
    @ExperimentalQGApi
    override suspend fun messageFromReference(reference: MessageReference): QGMessageContent {
        if (reference !is QGReference) {
            throw UnsupportedOperationException(
                "Cannot query message use a reference that type is not QGReference. " +
                        "Use `reference` type of QGReference or use messageFromId(channelId, messageId) plz."
            )
        }

        val cid = reference.channelId ?: throw IllegalArgumentException("`reference.channelId` must not be null")

        return messageFromId(cid, reference.messageId)
    }

    /**
     * 根据子频道ID和消息ID查询指定的**文字子频道**消息内容。
     *
     * @throws RuntimeException 任何可能在 [请求API][executeData] 过程中产生的异常
     */
    @ST
    @ExperimentalQGApi
    public suspend fun messageFromId(channelId: ID, messageId: ID): QGMessageContent {
        val api = GetMessageApi.create(channelId.literal, messageId.literal)
        val data = executeData(api)
        return QGMessageContentImpl(this, data)
    }


    /**
     * 主动推送一个事件原文。
     * 可用于在 webhook 模式下推送事件。
     *
     * @param payload 接收到的事件推送的JSON格式正文字符串。
     * @param options 额外提供的属性或配置。默认为 `null`。
     *
     * @throws IllegalArgumentException 参考:
     * - [EmitEventOptions.ignoreUnknownOpcode]
     * - [EmitEventOptions.ignoreMissingOpcode]
     *
     * @see QGSourceBot.emitEvent
     *
     * @since 4.1.0
     */
    @ST
    public suspend fun emitEvent(
        payload: String,
        options: EmitEventOptions? = null,
    ): EmitResult {
        return source.emitEvent(payload, options)
    }

    /**
     * 主动推送一个事件原文。
     * 可用于在 webhook 模式下推送事件。
     *
     * @param payload 接收到的事件推送的JSON格式正文字符串。
     *
     * @see QGSourceBot.emitEvent
     * @since 4.1.0
     */
    @ST
    public suspend fun emitEvent(payload: String): EmitResult {
        return source.emitEvent(payload)
    }
}

/**
 * 使用 [QGBot.emitEvent] 推送一个外部事件，并且在 [block] 中配置 [EmitEventOptions]。
 * @see QGBot.emitEvent
 * @since 4.1.0
 */
public suspend inline fun QGBot.emitEvent(
    payload: String,
    block: EmitEventOptions.() -> Unit
): EmitResult {
    return source.emitEvent(payload, block)
}

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

package love.forte.simbot.component.kook.bot

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.bot.*
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.KookComponent
import love.forte.simbot.component.kook.KookGuild
import love.forte.simbot.component.kook.KookUserChat
import love.forte.simbot.component.kook.message.KookAsset
import love.forte.simbot.component.kook.message.KookAssetImage
import love.forte.simbot.component.kook.message.KookChannelMessageDetailsContent.Companion.toContent
import love.forte.simbot.component.kook.message.KookMessageContent
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.kook.api.ApiResponseException
import love.forte.simbot.kook.api.ApiResultException
import love.forte.simbot.kook.api.asset.Asset
import love.forte.simbot.kook.api.asset.CreateAssetApi
import love.forte.simbot.kook.api.message.GetChannelMessageViewApi
import love.forte.simbot.kook.api.userchat.GetUserChatListApi
import love.forte.simbot.kook.messages.MessageType
import love.forte.simbot.kook.stdlib.Ticket
import love.forte.simbot.logger.Logger
import love.forte.simbot.message.MessageReference
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmSynthetic
import love.forte.simbot.kook.stdlib.Bot as KBot


/**
 * simbot组件针对 Kook bot 的 [Bot] 实现。
 *
 * 注意：[KookBot] 对第三方实现不保证兼容与稳定
 *
 * @author ForteScarlet
 */
@OptIn(InheritanceBotApi::class)
@SubclassOptInRequired(InternalForInheritanceKookBotApi::class)
public interface KookBot : Bot, CoroutineScope {
    /**
     * 源自 [sourceBot] 的 [CoroutineContext]
     */
    override val coroutineContext: CoroutineContext

    /**
     * 得到标准库中的 [Kook Bot][KBot] 源对象。
     */
    public val sourceBot: KBot

    /**
     * botID。即 [Ticket.clientId].
     */
    override val id: ID
        get() = sourceBot.ticket.clientId.ID

    public val logger: Logger

    /**
     * 判断此 ID 是否代表当前 bot。可以代表 bot 的 id 可能是 [clientId][Ticket.clientId],
     * 也有可能是此 bot 在系统中作为 User 时候的 `user id`。
     *
     * 对于 `user id` 的判断，只有当至少执行过一次 [start] 来启动 bot 的时候才会生效匹配，在那之前将只会通过 `clientId` 进行匹配。
     *
     */
    override fun isMe(id: ID): Boolean

    /**
     * 得到对应的组件实例。
     */
    override val component: KookComponent

    /**
     * 头像信息
     *
     * 需要至少启动过一次（执行过 [start]）后才可获取。
     *
     * @throws IllegalStateException 尚未启动过时
     */
    public val avatar: String
        get() = sourceBot.botUserInfo.avatar

    /**
     * 用户名称
     *
     * 需要至少启动过一次（执行过 [start]）后才可获取。
     *
     * @throws IllegalStateException 尚未启动过时
     */
    override val name: String
        get() = sourceBot.botUserInfo.username

    /**
     * 提供一个 [CreateAssetApi], 通过此BOT上传将得到的 [Asset] 结果包装为 [KookAsset] .
     *
     * @param api 上传资源的请求
     * @param type 构建的 [KookAsset] 的类型
     */
    @ST
    public suspend fun uploadAsset(api: CreateAssetApi, type: MessageType): KookAsset =
        KookAsset(api.requestDataBy(this), type)

    /**
     * 提供一个 [CreateAssetApi], 通过此BOT上传将得到的 [Asset] 结果包装为 [KookAssetImage] .
     *
     * @param api 上传资源的请求
     */
    @ST
    public suspend fun uploadAssetImage(api: CreateAssetApi): KookAssetImage =
        KookAssetImage(api.requestDataBy(this))

    override fun cancel(reason: Throwable?) {
        sourceBot.cancel(reason)
    }

    /**
     * 与 [KookGuild] 相关的行为关系操作。
     */
    override val guildRelation: KookGuildRelation

    /**
     * 与 [KookUserChat] 相关的行为关系操作。
     */
    override val contactRelation: KookContactRelation

    /**
     * Deprecated: KOOK 中没有群的概念。
     */
    @Deprecated("Unsupported in KOOK", ReplaceWith("null"))
    override val groupRelation: GroupRelation?
        get() = null

    /**
     * 根据引用ID查询对应的 **频道消息** 的 [KookMessageContent]。
     */
    @ST
    override suspend fun messageFromId(id: ID): KookMessageContent {
        val view = GetChannelMessageViewApi.create(id.literal).requestDataBy(this)
        return view.toContent(this)
    }

    /**
     * 根据引用查询对应的 **频道消息** 的 [KookMessageContent]。
     */
    @ST
    override suspend fun messageFromReference(reference: MessageReference): KookMessageContent {
        return messageFromId(reference.id)
    }
}

/**
 * [KookBot] 中与频道相关的关系操作。
 * @see KookBot.guildRelation
 */
public interface KookGuildRelation : GuildRelation {
    /**
     * 获取所有的频道服务器序列
     */
    override val guilds: Collectable<KookGuild>

    /**
     * 根据ID寻找指定频道。如果找不到则返回 null。
     */
    @ST(blockingBaseName = "getGuild", blockingSuffix = "", asyncBaseName = "getGuild", reserveBaseName = "getGuild")
    override suspend fun guild(id: ID): KookGuild?

    /**
     * 获取当前bot所处的频道服务器数量。
     */
    @STP
    override suspend fun guildCount(): Int
}

/**
 * [KookBot] 中与聊天会话相关的关系操作。
 * @see KookBot.contactRelation
 */
public interface KookContactRelation : ContactRelation {
    /**
     * 查询并获取**聊天会话**序列。
     *
     * @throws ApiResponseException API 请求过程中产生的异常
     *  @throws ApiResultException API 请求过程中产生的异常
     */
    override val contacts: Collectable<KookUserChat>
        get() = getContacts(null)

    /**
     * 查询并获取**聊天会话**序列。
     *
     * @param size 每个页码的元素数量
     * @throws ApiResponseException API 请求过程中产生的异常
     * @throws ApiResultException API 请求过程中产生的异常
     */
    public fun getContacts(size: Int?): Collectable<KookUserChat>

    /**
     * 通过 [id] 寻找（创建）一个 [KookUserChat]。
     * 只要提供的 [id] 与当前 Bot 能够建立联系便一定会得到 [KookUserChat] 实例，
     * 否则在API请求过程中可能产生异常。
     *
     * @throws ApiResponseException API 请求过程中产生的异常
     * @throws ApiResultException API 请求过程中产生的异常
     */
    @ST(
        blockingBaseName = "getContact",
        blockingSuffix = "",
        asyncBaseName = "getContact",
        reserveBaseName = "getContact"
    )
    override suspend fun contact(id: ID): KookUserChat

    /**
     * 查询并获取**聊天会话**的数量。
     *
     * 会通过[聊天会话列表查询API][GetUserChatListApi]查询当前聊天会话的数量。
     *
     * @throws ApiResponseException API 请求过程中产生的异常
     * @throws ApiResultException API 请求过程中产生的异常
     */
    @JvmSynthetic
    override suspend fun contactCount(): Int

}

/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.message

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.launch
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.component.kook.message.KookAggregatedMessageReceipt.Companion.merge
import love.forte.simbot.component.kook.message.KookMessageCreatedReceipt.Companion.asReceipt
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.kook.ExperimentalKookApi
import love.forte.simbot.kook.InternalKookApi
import love.forte.simbot.kook.api.KookApi
import love.forte.simbot.kook.api.asset.CreateAssetApi
import love.forte.simbot.kook.api.message.SendChannelMessageApi
import love.forte.simbot.kook.api.message.SendDirectMessageApi
import love.forte.simbot.kook.api.message.SendMessageResult
import love.forte.simbot.kook.messages.MessageType
import love.forte.simbot.kook.objects.Attachments
import love.forte.simbot.kook.objects.kmd.AtTarget
import love.forte.simbot.kook.objects.kmd.KMarkdownBuilder
import love.forte.simbot.kook.stdlib.Bot
import love.forte.simbot.message.*
import love.forte.simbot.resource.ByteArrayResource
import love.forte.simbot.resource.Resource


private data class QuoteRef(var quote: String?) {
    // 是否要让只有第一个消息有引用效果？
//    fun take(): String? {
//        val q = quote
//        if (q != null) {
//            quote = null
//        }
//        return q
//    }
}
private data class TempTargetIdRef(var tempTargetId: String?)

private const val NOT_DIRECT = 0
private const val DIRECT_TYPE_BY_TARGET = 1
private const val DIRECT_TYPE_BY_CODE = 2

/**
 * 将消息发送给目标。此消息如果是个消息链，则有可能会被拆分为多条消息发送，
 * 届时将会返回 [KookAggregatedMessageReceipt].
 *
 * 消息的发送会更**倾向于**整合为一条或较少条消息，如果出现多条消息，
 * [quote]、[nonce] 和 [tempTargetId] 会被 **所有** 可能产生的消息重复使用。
 *
 * 其中，部分消息元素可能会覆盖默认值：
 * - [MessageReference] 会覆盖 [quote]
 * - [KookTempTarget] 会覆盖 [tempTargetId]
 *
 * @return 消息最终地发送结果回执。如果为 `null` 则代表没有有效消息发送。
 */
public suspend fun Message.sendToChannel(
    bot: KookBot,
    targetId: String,
    quote: String? = null,
    nonce: String? = null,
    tempTargetId: String? = null,
    defaultTempTargetId: String? = null,
): KookMessageReceipt? = send0(bot, targetId, NOT_DIRECT, quote, nonce, tempTargetId, defaultTempTargetId)


/**
 * 将消息发送给目标。此消息如果是个消息链，则有可能会被拆分为多条消息发送，
 * 届时将会返回 [KookAggregatedMessageReceipt].
 *
 * 消息的发送会更**倾向于**整合为一条或较少条消息，如果出现多条消息，
 * [quote]、[nonce] 和 [tempTargetId] 会被 **所有** 可能产生的消息重复使用。
 *
 * 其中，部分消息元素可能会覆盖默认值：
 * - [MessageReference] 会覆盖 [quote]
 * - [KookTempTarget] 会覆盖 [tempTargetId]
 *
 * @return 消息最终地发送结果回执。如果为 `null` 则代表没有有效消息发送。
 */
public suspend fun Message.sendToDirectByTargetId(
    bot: KookBot,
    targetId: String,
    quote: String? = null,
    nonce: String? = null,
    tempTargetId: String? = null,
): KookMessageReceipt? = send0(bot, targetId, DIRECT_TYPE_BY_TARGET, quote, nonce, tempTargetId)

/**
 * 将消息发送给目标。此消息如果是个消息链，则有可能会被拆分为多条消息发送，
 * 届时将会返回 [KookAggregatedMessageReceipt].
 *
 * 消息的发送会更**倾向于**整合为一条或较少条消息，如果出现多条消息，
 * [quote]、[nonce] 和 [tempTargetId] 会被 **所有** 可能产生的消息重复使用。 *
 *
 * 其中，部分消息元素可能会覆盖默认值：
 * - [MessageReference] 会覆盖 [quote]
 * - [KookTempTarget] 会覆盖 [tempTargetId]
 *
 * @return 消息最终地发送结果回执。如果为 `null` 则代表没有有效消息发送。
 */
public suspend fun Message.sendToDirectByChatCode(
    bot: KookBot,
    chatCode: String,
    quote: String? = null,
    nonce: String? = null,
    tempTargetId: String? = null,
): KookMessageReceipt? = send0(bot, chatCode, DIRECT_TYPE_BY_CODE, quote, nonce, tempTargetId)


/**
 * 将消息发送给目标。此消息如果是个消息链，则有可能会被拆分为多条消息发送，
 * 届时将会返回 [KookAggregatedMessageReceipt].
 *
 * 消息的发送会更**倾向于**整合为一条或较少条消息，如果出现多条消息，
 * [quote]、[nonce] 和 [tempTargetId] 会被 **所有** 可能产生的消息重复使用。
 *
 * 其中，部分消息元素可能会覆盖默认值：
 * - [MessageReference] 会覆盖 [quote]
 * - [KookTempTarget] 会覆盖 [tempTargetId]
 *
 * @param defaultTempTargetId 如果存在 [KookTempTarget]
 *
 * @return 消息最终地发送结果回执。如果为 `null` 则代表没有有效消息发送。
 */
@OptIn(ExperimentalSimbotAPI::class)
private suspend fun Message.send0(
    bot: KookBot,
    targetId: String,
    directType: Int,
    quote: String? = null,
    nonce: String? = null,
    tempTargetId: String? = null,
    defaultTempTargetId: String? = null,
): KookMessageReceipt? {
    fun doRequest(type: Int, content: String, nonce: String?, quote: String?, tempTargetId: String?): KookApi<*> {
        return when (directType) {
            NOT_DIRECT -> SendChannelMessageApi.create(
                type = type,
                targetId = targetId,
                content = content,
                quote = quote,
                nonce = nonce,
                tempTargetId = tempTargetId
            )

            DIRECT_TYPE_BY_TARGET -> SendDirectMessageApi.createByTargetId(targetId, content, type, quote, nonce)
            DIRECT_TYPE_BY_CODE -> SendDirectMessageApi.createByChatCode(targetId, content, type, quote, nonce)
            else -> throw IllegalArgumentException("Unknown direct type: $directType")
        }
    }

    val message = this
    var kMarkdownBuilder: KMarkdownBuilder? = null
    val quoteRef = QuoteRef(quote)
    val tempRef = TempTargetIdRef(tempTargetId)

//    val requests: List<KookApiRequest<*>> = buildList(if (this is Message.Element<*>) 1 else (this as Messages).size) {
    val requests: List<() -> KookApi<*>> =
        buildList(if (this is Message.Element) 1 else (this as Messages).size) {
            // 清算 kmd
            fun liquidationKmd() {
                kMarkdownBuilder?.let { kmb ->
                    add {
                        doRequest(
                            MessageType.KMARKDOWN.type,
                            kmb.buildRaw(),
                            nonce,
                            quoteRef.quote,
                            tempRef.tempTargetId
                        )
                    }
                    kMarkdownBuilder = null
                }
            }

            suspend fun process(isSingle: Boolean, message: Message.Element) {
                when (message) {
                    is KookApiMessage -> {
                        liquidationKmd()
                        add { message.api }
                    }

                    is KookTempTarget -> when (message) {
                        is KookTempTarget.Target -> tempRef.tempTargetId = message.id.literal
                        is KookTempTarget.Current -> tempRef.tempTargetId = defaultTempTargetId
                    }
                    // 任意的 MessageReference 类型元素
                    // 覆盖当前消息元素
                    is MessageReference -> {
                        quoteRef.quote = message.id.literal
                    }

                    else -> {
                        message.elementToRequest(bot, isSingle, { type, content ->
                            liquidationKmd()
                            add { doRequest(type, content, nonce, quoteRef.quote, tempRef.tempTargetId) }
                        }) { block ->
                            block(kMarkdownBuilder ?: KMarkdownBuilder().also { kMarkdownBuilder = it })
                        }
                    }
                }
            }

            when (message) {
                is Message.Element -> process(true, message)

                is Messages -> {
                    if (!message.isEmpty()) {
                        val isSingle = message.size == 1
                        message.forEach { m ->
                            process(isSingle, m)
                        }
                    }
                }
            }

            liquidationKmd()
        }


    /*
        return if (result is MessageCreated) {
                result.asReceipt(false, baseBot)
            } else {
                KookApiRequestedReceipt(result, false, baseBot)
            }
     */

    fun Any?.toReceipt(): SingleKookMessageReceipt {
        return if (this is SendMessageResult) {
            this.asReceipt(false, bot)
        } else {
            KookApiRequestedReceipt(this, false)
        }
    }

    return when {
        requests.isEmpty() -> {
            null
        }

        requests.size == 1 -> {
            requests.first()().requestDataBy(bot).toReceipt()
        }

        else -> {
            requests.map { req -> req().requestDataBy(bot).toReceipt() }.merge()
        }
    }
}


/**
 * 尝试将一个单独的消息元素转化为用于发送消息的请求。
 *
 * 如果 [isSingleElement] 为 `true`，则消息元素类型为 [PlainText] 时将会使用 `doRequest(MessageType.TEXT.type, message.text)` 发送而不使用KMarkdown。
 *
 * 不需要处理 [KookApiMessage], 外层自行处理。
 *
 */
@OptIn(ExperimentalSimbotAPI::class, ExperimentalKookApi::class)
private suspend inline fun Message.Element.elementToRequest(
    bot: KookBot,

    /**
     * 实际上的消息元素数量。如果需要发送的消息只有一个，那么处理时可能会选择直接使用 doRequest 而不是拼接内容到 kmd 中。
     *
     * 至少为1。
     */
    isSingleElement: Boolean,

    /**
     * 得到（进行）一次请求
     */
    doRequest: (type: Int, content: String) -> Unit,

    /**
     * 将信息填充到 kmd 中。kmd的发送是自动的，其产生于：
     * 执行中途的 doRequest 之前和全部元素遍历之后。
     *
     * 当发生过一次请求的生成后，withinKmd中的构建器将会是一个新的构建器。
     *
     */
    withinKmd: (KMarkdownBuilder.() -> Unit) -> Unit,
) {
    when (val message = this) {
        // 文本消息
        is PlainText -> {
            if (isSingleElement) {
                doRequest(MessageType.TEXT.type, message.text)
            } else {
                withinKmd {
                    text(message.text)
                }
            }
        }

        is KookMessageElement -> when (message) {
            // 媒体资源
            is KookAssetMessage -> doRequest(message.type, message.asset.url)
            // KMarkdown
            is KookKMarkdownMessage -> doRequest(MessageType.KMARKDOWN.type, message.kMarkdown.rawContent)
            // card message
            is KookCardMessage -> doRequest(MessageType.CARD.type, message.cards.encode())

            is KookAtAllHere -> {
                withinKmd {
                    at(AtTarget.Here)
                }
            }

            is KookAttachmentMessage -> {
                val type: MessageType = when (message) {
                    is KookAttachment -> when (val attType = message.attachment.type.lowercase()) {
                        "file" -> MessageType.FILE
                        "image" -> MessageType.IMAGE
                        "video" -> MessageType.VIDEO
                        else -> throw IllegalArgumentException("Unknown attachment type: $attType")
                    }

                    is KookAttachmentFile -> MessageType.FILE
                    is KookAttachmentImage -> MessageType.IMAGE
                    is KookAttachmentVideo -> MessageType.VIDEO
                }

                // Just re-upload and send, waiting for fix.
                //  See https://github.com/simple-robot/simbot-component-kook/issues/75
                val createRequest = createReUploadAssetApi(bot, message.attachment)

                val asset = createRequest.requestDataBy(bot)

                doRequest(type.type, asset.url)
            }

            // asset Image
            is KookAssetImage -> doRequest(MessageType.IMAGE.type, message.assetUrl)

            else -> {
                // other, ignore.
            }
        }

        is AtAll -> {
            withinKmd {
                at(AtTarget.All)
            }
        }

        // 需要上传/发送的图片
        is Image -> when (message) {
            is OfflineImage -> {
                val assetApi: CreateAssetApi = when (message) {
                    is OfflineResourceImage -> when (val resource = message.resource) {
                        is ByteArrayResource -> CreateAssetApi.create(resource.data())
                        else -> {
                            platformCreateAssetApi(resource) ?: CreateAssetApi.create(resource.data())
                        }
                    }

                    else -> CreateAssetApi.create(message.data())
                }

                val asset = assetApi.requestDataBy(bot)
                doRequest(MessageType.IMAGE.type, asset.url)
            }

            // 其他任意 RemoteImage 图片类型, 直接使用id
            is RemoteImage -> when (message) {
                is RemoteUrlAwareImage -> doRequest(MessageType.IMAGE.type, message.url())
                else -> doRequest(MessageType.IMAGE.type, message.id.literal)
            }

            // Unknown Image type
            else -> {
                bot.logger.warn("Unknown image type: {} ({})", message, message::class)
            }
        }

        // std at
        is At -> {
            withinKmd {
                when (message.type) {
                    KookMessages.AT_TYPE_CHANNEL -> channel(message.target.literal)
                    KookMessages.AT_TYPE_ROLE -> role(message.target.literal)
                    KookMessages.AT_TYPE_USER -> at(message.target.literal)
                    else -> at(message.target.literal)
                }
            }
        }

        is Face -> {
            withinKmd {
                val id = message.id.literal
                // 名字怎么来？
                serverEmoticons(id, id)
            }
        }

        is Emoji -> {
            withinKmd {
                emoji(message.id.literal)
            }
        }

        else -> {
            // other..?
        }
    }
}


/**
 * 获取一个根据 [Attachments.url] 重新上传此图片的 [CreateAssetApi]。
 * 会通过 [KookBot.sourceBot.apiClient][Bot.apiClient] 请求 url
 * 并将结果“转录”至 [CreateAssetApi]。
 */
@Suppress("DEPRECATION")
@InternalKookApi
public fun createReUploadAssetApi(bot: KookBot, attachment: Attachments): CreateAssetApi {
    val client = bot.sourceBot.apiClient

    return CreateAssetApi.create(
        ChannelProvider {
            val channel = ByteChannel(true) // TODO Deprecated
            bot.launch {
                client.get(attachment.url).bodyAsChannel().copyAndClose(channel)
            }.apply {
                invokeOnCompletion { e ->
                    if (!channel.isClosedForWrite) {
                        channel.close(e?.takeIf { it !is CancellationException }?.let { IllegalStateException(e) })
                    }
                }
            }

            channel
        },
        filename = attachment.name
    )
}

internal expect fun platformCreateAssetApi(resource: Resource): CreateAssetApi?

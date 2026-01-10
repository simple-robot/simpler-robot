/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.qguild.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.ErrInfo
import love.forte.simbot.qguild.QGInternalApi
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.api.message.direct.DmsSendApi


/**
 * 消息被审核' 异常。
 * 当推送、回复消息时响应的 `code` 错误码为 `304023`、`304024` 时会在响应数据包
 * `data` 中返回 `MessageAudit` 审核消息的信息，
 * 例如：
 * ```json
 * {
 *      "code": 304023,
 *      "message": "push message is waiting for audit now",
 *      "data": {
 *         "message_audit": {
 *             "audit_id": "50db3d4b-9589-4497-9a1e-75e5532262ba"
 *         }
 *     }
 * }
 * ```
 *
 * 此 `data` 则会被包装为 [MessageAuditedId] 并通过 [MessageAuditedException] 抛出。
 *
 * 详见文档 [发送消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html) 中的相关描述。
 *
 * @see MessageSendApi
 * @see DmsSendApi
 *
 * @author ForteScarlet
 */
public class MessageAuditedException : QQGuildApiException {

    /**
     * 响应中得到的消息审核信息。
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public val messageAuditedId: MessageAuditedId

    @Suppress("unused")
    internal constructor(messageAuditedId: MessageAuditedId, value: Int, description: String) : super(
        value,
        description
    ) {
        this.messageAuditedId = messageAuditedId
    }

    internal constructor(
        messageAuditedId: MessageAuditedId,
        info: ErrInfo?,
        value: Int,
        description: String
    ) : super(info, value, description) {
        this.messageAuditedId = messageAuditedId
    }


    public companion object {
        private val AUDIT_ERROR_CODES = setOf(304023, 304024)

        @QGInternalApi
        internal fun isAuditResultCode(code: Int): Boolean = code in AUDIT_ERROR_CODES
    }
}


// see https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html
// 详见错误码。
//
//其中推送、回复消息的 code 错误码 304023、304024 会在 响应数据包 data 中返回 MessageAudit 审核消息的信息

/**
 * 推送、回复消息时响应的 '消息被审核' 数据信息。
 *
 * @see MessageAuditedException
 */
@ApiModel
@Serializable
internal data class MessageAudit(@SerialName("message_audit") val messageAudit: MessageAuditedId)


/**
 * 推送、回复消息时响应的 '消息被审核' 数据信息。
 *
 * @see MessageAudit
 * @see MessageAuditedException
 */
@ApiModel
@Serializable
public data class MessageAuditedId(@SerialName("audit_id") val auditId: String)

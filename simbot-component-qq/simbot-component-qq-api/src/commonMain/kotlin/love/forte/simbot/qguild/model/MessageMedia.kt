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

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities


/**
 * [富媒体消息](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/rich-media.html)
 *
 * @property fileUuid 文件 ID
 * @property fileInfo 文件信息，用于发消息接口的 media 字段使用
 * @property ttl 有效期，表示剩余多少秒到期，到期后 file_info 失效，当等于 0 时，表示可长期使用
 * @property id 发送消息的唯一ID，当srv_send_msg设置为true时返回
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class MessageMedia @ApiModelConstructor constructor(
    @SerialName("file_uuid")
    public val fileUuid: String,
    @SerialName("file_info")
    public val fileInfo: String,
    public val ttl: Int,
    public val id: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageMedia) return false

        if (fileUuid != other.fileUuid) return false
        if (fileInfo != other.fileInfo) return false
        if (ttl != other.ttl) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileUuid.hashCode()
        result = 31 * result + fileInfo.hashCode()
        result = 31 * result + ttl
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "MessageMedia(fileUuid='$fileUuid', fileInfo='$fileInfo', ttl=$ttl, id=$id)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("fileUuid"))
    public operator fun component1(): String = fileUuid

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("fileInfo"))
    public operator fun component2(): String = fileInfo

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("ttl"))
    public operator fun component3(): Int = ttl

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
    public operator fun component4(): String? = id

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        fileUuid: String = this.fileUuid,
        fileInfo: String = this.fileInfo,
        ttl: Int = this.ttl,
        id: String? = this.id,
    ): MessageMedia = MessageMedia(fileUuid, fileInfo, ttl, id)
    //endregion
}

/**
 * [富媒体消息](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/rich-media.html)
 *
 * @property fileInfo 文件信息，用于发消息接口的 media 字段使用
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class SendMessageMedia @ApiModelConstructor constructor(
    @SerialName("file_info")
    public val fileInfo: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SendMessageMedia) return false
        return fileInfo == other.fileInfo
    }

    override fun hashCode(): Int = fileInfo.hashCode()

    override fun toString(): String = "SendMessageMedia(fileInfo='$fileInfo')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("fileInfo"))
    public operator fun component1(): String = fileInfo

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(fileInfo: String = this.fileInfo): SendMessageMedia = SendMessageMedia(fileInfo)
    //endregion
}

/**
 * [MessageMedia] to [SendMessageMedia]
 */
public fun MessageMedia.forSend(): SendMessageMedia = SendMessageMedia(fileInfo)

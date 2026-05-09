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

package love.forte.simbot.component.onebot.v11.message.segment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import love.forte.simbot.component.onebot.v11.message.Base64Encoder
import love.forte.simbot.component.onebot.v11.message.segment.OneBotRecord.Factory.create
import love.forte.simbot.component.onebot.v11.message.standardEncoderByName
import love.forte.simbot.component.onebot.v11.message.standardName
import love.forte.simbot.resource.ByteArrayResource
import love.forte.simbot.resource.Resource
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [语音](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E8%AF%AD%E9%9F%B3)
 *
 * 可用于发送和接收。
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(OneBotRecord.TYPE)
public class OneBotRecord private constructor(
    override val data: Data,
    @Transient
    private val resource0: Resource? = null,
    private val base64Encoder: String? = null,
    @Transient
    private val base64EncoderValue: Base64Encoder =
        base64Encoder?.let { standardEncoderByName(it) } ?: Base64Encoder.Default
) : OneBotMessageSegment {
    /**
     * 当前 [OneBotRecord] 中的资源信息。
     * 如果 [Data.url] 不为 `null`(即此消息是接收到的)
     * 则 [resource] 代表接收到的资源信息，否则代表用于发送的
     * [Data.file] 的资源信息。
     */
    public val resource: Resource by lazy {
        resource0 ?: data.resolveToResource(base64EncoderValue)
    }

    public companion object Factory {
        public const val TYPE: String = "record"

        private fun Data.resolveToResource(encoder: Base64Encoder): Resource {
            return resolveUrlOrFileToResource(url, file, encoder)
        }

        /**
         * 直接使用 [Data] 和 [Resource] 构建 [OneBotRecord].
         * - 如果 [Resource] 为 `null`，则会尝试解析 [data]:
         * - 如果 [Data.url] 不为 `null`，使用它（仅支持 JVM 平台）
         * - 如果 [Data.file] 是 base64 格式，解析为 [ByteArrayResource]。
         * - 如果 [Data.file] 是链接或者文件路径，
         * 解析为 `URIResource` 或 `PathResource` （仅支持 JVM 平台）
         *
         * 如果需要指定 [Base64Encoder] (since 1.6.1),
         * 选择使用 [create] 的另一个可以提供 [AdditionalParams] 的重载。
         *
         * @throws UnsupportedOperationException 如果解析 [resource] 时平台不支持
         */
        @JvmStatic
        @JvmOverloads
        public fun create(data: Data, resource: Resource? = null): OneBotRecord =
            OneBotRecord(data, resource)

        /**
         * 只通过 [Data.file] 属性构建 [OneBotRecord].
         */
        @JvmStatic
        public fun create(file: String): OneBotRecord =
            create(Data(file))

        /**
         * 直接使用 [Resource] 构建一个**用于发送**的 [OneBotRecord]。
         * 其中，[Data.file] 会根据 [Resource] 的类型计算为一个对应的值，
         * 大概规则为：
         * - 如果为 (JVM平台的) `URIResource`, 且代表的是一个网络路径，则直接使用此网络路径。
         * - 如果可以解析为本地文件（例如JVM平台的 `FileResource`，`PathResource`
         * 或 `URI.scheme` == `"file"` 的 `URIResource`，
         * 则会根据 [AdditionalParams.localFileToBase64] 的与否解析为 base64
         * 或本地文件路径。
         * （注意！一些尚且不支持读取本地文件的平台，可能会直接抛出 [UnsupportedOperationException]）。
         * - 如果为可以直接读取字节数据的类型，例如 [ByteArrayResource]，
         * 则计算并使用 `base64` 格式。
         * - 其他无法判明类型的情况，尝试使用 `base64` 格式。
         *
         * @throws UnsupportedOperationException 如果解析 [resource] 时平台不支持
         */
        @JvmStatic
        @JvmOverloads
        public fun create(resource: Resource, additional: AdditionalParams? = null): OneBotRecord {
            val base64Encoder = additional.base64EncoderOrDefault

            val file = resolveResourceToFileValue(
                resource,
                additional?.localFileToBase64 == true,
                base64Encoder
            )

            val data = Data(
                file = file,
                magic = additional?.magic,
                cache = additional?.cache,
                proxy = additional?.proxy,
                timeout = additional?.timeout,
            )

            return OneBotRecord(data, resource, base64Encoder.standardName(), base64Encoder)
        }
    }

    /**
     * @property file 语音文件名
     * @property magic 发送时可选，默认 0，设置为 1 表示变声
     * @property url (receive only) 语音 URL
     * @property cache (send only) 只在通过网络 URL 发送时有效，表示是否使用已缓存的文件，默认 1
     * @property proxy (send only) 只在通过网络 URL 发送时有效，
     * 表示是否通过代理下载文件（需通过环境变量或配置文件配置代理），默认 1
     * @property timeout (send only) 只在通过网络 URL 发送时有效，单位秒，表示下载网络文件的超时时间，默认不超时
     */
    @Serializable
    public data class Data internal constructor(
        val file: String,
        val magic: String? = null,
        val url: String? = null, // receive only
        val cache: Boolean? = null, // send only
        val proxy: Boolean? = null, // send only
        val timeout: Int? = null, // send only
    )

    /**
     * 用于 [OneBotRecord] 提供的部分工厂函数中，
     * 指定 [Data] 中部分属性。
     */
    public class AdditionalParams {
        public var localFileToBase64: Boolean = false

        /**
         * 当需要对资源进行 base64 编码时使用的编码器。
         * 如果未配置则默认为 [Base64Encoder.Default]。
         * @since 1.6.1
         */
        public var base64Encoder: Base64Encoder? = null
        public var magic: String? = null
        public var cache: Boolean? = null
        public var proxy: Boolean? = null
        public var timeout: Int? = null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OneBotRecord) return false

        if (data != other.data) return false
        if (resource0 != other.resource0) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + (resource0?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "OneBotRecord(data=$data)"
    }
}

private inline val OneBotRecord.AdditionalParams?.base64EncoderOrDefault: Base64Encoder
    get() = this?.base64Encoder ?: Base64Encoder.Default

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

package love.forte.simbot.kook.api.asset

import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.io.Source
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [上传文件/图片](https://developer.kookapp.cn/doc/http/asset#%E4%B8%8A%E4%BC%A0%E5%AA%92%E4%BD%93%E6%96%87%E4%BB%B6)
 *
 * 与其他 API 实现不太一样的是, [CreateAssetApi.body] 每次获取都会构建一个新的实例。
 *
 * > `Header` 中 `Content-Type` 必须为 `form-data`
 *
 * 在 JVM 平台中，还可以通过 `AssetApis.xxx` 使用更多平台特定的构建方式，
 * 例如使用 `File` 或 `Path`。
 *
 * @author ForteScarlet
 */
public class CreateAssetApi private constructor(
    private val formDataContentProvider: () -> MultiPartFormDataContent
) : KookPostApi<Asset>() {
    public companion object Factory {
        private val PATH = ApiPath.create("asset", "create")
        private const val DEFAULT_FILENAME = "unknown-file"
        private const val ASSET_API_FORM_PROPERTY_NAME = "file"
        private val HEADERS = Headers.build {
            append(HttpHeaders.ContentType, ContentType.MultiPart.FormData)
        }

        /**
         * 提供文件字节数据作为上传API。
         *
         * @param fileBytes 文件数据
         * @param filename 使用在表单数据中 [HttpHeaders.ContentDisposition] 的 `filename` 属性，
         * 如果为 `null` 则会提供一个默认的文件名称 `unknown-file`。
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            fileBytes: ByteArray,
            filename: String? = null
        ): CreateAssetApi = CreateAssetApi {
            MultiPartFormDataContent(
                formData {
                    append(key = ASSET_API_FORM_PROPERTY_NAME, fileBytes, fileHeaders(filename))
                }
            )
        }

        /**
         * 提供文件数据的 [InputProvider] 作为上传API。
         *
         * @param fileProvider 文件数据 provider
         * @param filename 使用在表单数据中 [HttpHeaders.ContentDisposition] 的 `filename` 属性，
         * 如果为 `null` 则会提供一个默认的文件名称 `unknown-file`。
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            fileProvider: InputProvider,
            filename: String? = null
        ): CreateAssetApi = CreateAssetApi {
            MultiPartFormDataContent(
                formData {
                    append(key = ASSET_API_FORM_PROPERTY_NAME, fileProvider, fileHeaders(filename))
                }
            )
        }

        /**
         * 提供文件数据的 [ByteReadPacket] 作为上传API。
         *
         * *Note: 需要注意 [ByteReadPacket] 中的数据只能被使用一次。*
         *
         * @param source 文件数据 [ByteReadPacket]
         * @param filename 使用在表单数据中 [HttpHeaders.ContentDisposition] 的 `filename` 属性，
         * 如果为 `null` 则会提供一个默认的文件名称 `unknown-file`。
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            source: Source,
            filename: String? = null
        ): CreateAssetApi = CreateAssetApi {
            MultiPartFormDataContent(
                formData {
                    append(key = ASSET_API_FORM_PROPERTY_NAME, source, fileHeaders(filename))
                }
            )
        }

        /**
         * 提供文件数据的 [ChannelProvider] 作为上传API。
         *
         * @param fileChannelProvider 文件数据 [ChannelProvider]
         * @param filename 使用在表单数据中 [HttpHeaders.ContentDisposition] 的 `filename` 属性，
         * 如果为 `null` 则会提供一个默认的文件名称 `unknown-file`。
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            fileChannelProvider: ChannelProvider,
            filename: String? = null
        ): CreateAssetApi = CreateAssetApi {
            MultiPartFormDataContent(
                formData {
                    append(key = ASSET_API_FORM_PROPERTY_NAME, fileChannelProvider, fileHeaders(filename))
                }
            )
        }

        private fun fileHeaders(filename: String?): Headers = Headers.build {
            append(HttpHeaders.ContentDisposition, "filename=\"${filename ?: DEFAULT_FILENAME}\"")
        }
    }

    override val headers: Headers
        get() = HEADERS

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<Asset>
        get() = Asset.serializer()

    override val body: Any
        get() = formDataContentProvider()
}

/**
 * [CreateAssetApi] 创建资源后的响应结果
 *
 * @property url 资源的 url
 */
@Serializable
public data class Asset @ApiResultType constructor(val url: String)

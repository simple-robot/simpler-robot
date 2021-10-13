/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.kaiheila.api.v3.asset

import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.streams.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.api.*
import love.forte.simbot.kaiheila.api.v3.asset.AssetCreateReq.Key.byBytes
import love.forte.simbot.kaiheila.api.v3.asset.AssetCreateReq.Key.byPath
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.inputStream


/**
 *
 * [上传媒体文件](https://developer.kaiheila.cn/doc/http/asset#%E4%B8%8A%E4%BC%A0%E5%AA%92%E4%BD%93%E6%96%87%E4%BB%B6)
 *
 * 通过 [fileInputSupplier] 来指定上传内容的流获取函数，并可以通过 [fileName] 来指定文件名称（虽然没什么用大概）
 *
 * *目前支持 图片, 视频(.mp4 .mov), 文件*
 * @see byPath
 * @see byBytes
 *
 * @author ForteScarlet
 *
 */
public class AssetCreateReq(
    private val fileInputSupplier: () -> InputStream,
    private val fileName: String?,
    private val fileContentType: ContentType,
) : PostAssetApiReq<ObjectResp<AssetCreateResp>> {
    companion object Key : BaseApiDataKey("asset", "create") {

        @JvmStatic
        @JvmOverloads
        @JvmName("getInstanceByPath")
        fun byPath(path: Path, fileName: String? = null, fileContentType: ContentType? = null): AssetCreateReq {
            val realFileName = fileName ?: path.fileName.toString()
            val realFileContentType: ContentType = fileContentType ?: run {
                val split = realFileName.split('.')
                if (split.size > 1) {
                    when (split.last().lowercase()) {
                        "mov" -> ContentType.Video.QuickTime
                        "mp4" -> ContentType.Video.MP4
                        "gif" -> ContentType.Image.GIF
                        "jpeg", "jpg" -> ContentType.Image.JPEG
                        "png" -> ContentType.Image.PNG
                        "ico" -> ContentType.Image.XIcon
                        else -> ContentType.Any
                    }
                } else {
                    ContentType.Any
                }
            }

            return AssetCreateReq(
                { path.inputStream(StandardOpenOption.READ) },
                realFileName,
                realFileContentType
            )
        }

        @JvmStatic
        @JvmOverloads
        @JvmName("getInstanceByBytes")
        fun byBytes(
            bytes: ByteArray,
            fileName: String? = null,
            fileContentType: ContentType = ContentType.Any,
        ): AssetCreateReq = AssetCreateReq(
            { bytes.inputStream() },
            fileName,
            fileContentType
        )

    }

    override val key: ApiData.Req.Key
        get() = Key

    override val dataSerializer: DeserializationStrategy<ObjectResp<AssetCreateResp>>
        get() = AssetCreateResp.objectResp

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = route
        builder.contentType = null // [Content-Type] are controlled by the engine and cannot be set explicitly
    }

    override val body: Any
        get() = MultiPartFormDataContent(
            formData {

                val headers = if (fileName != null) {
                    headersOf(
                        // HttpHeaders.ContentType to listOf(fileContentType.toString()),
                        HttpHeaders.ContentDisposition to listOf("filename=$fileName")
                    )
                } else {
                    headersOf(
                        // HttpHeaders.ContentType to listOf(fileContentType.toString()),
                    )
                }


                append("file",
                    InputProvider { fileInputSupplier().asInput() },
                    headers
                )
            }
        )
}


@Serializable
public data class AssetCreateResp(val url: String) : AssetApiRespData() {
    companion object {
        val objectResp = objectResp<AssetCreateResp>()
    }
}



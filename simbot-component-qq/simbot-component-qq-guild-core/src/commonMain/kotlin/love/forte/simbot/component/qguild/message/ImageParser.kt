/*
 * Copyright (c) 2023-2025. ForteScarlet.
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

package love.forte.simbot.component.qguild.message

import io.ktor.client.request.forms.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.io.readByteArray
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.message.SendingMessageParser.GroupBuilderType.C2C
import love.forte.simbot.component.qguild.message.SendingMessageParser.GroupBuilderType.GROUP
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.message.*
import love.forte.simbot.qguild.ExperimentalQGMediaApi
import love.forte.simbot.qguild.api.files.UploadGroupFilesApi
import love.forte.simbot.qguild.api.files.UploadUserFilesApi
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.resource.ByteArrayResource
import love.forte.simbot.resource.Resource
import love.forte.simbot.resource.SourceResource
import love.forte.simbot.resource.toResource
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmStatic

internal const val JVM_DISABLE_BASE64_UPLOAD_WARN = "simbot.qqguild.media.disableBase64UploadWarn"
internal expect val base64UploadWarnInitialValue: Boolean

/**
 *
 * @author ForteScarlet
 */
public object ImageParser : SendingMessageParser {
    internal val logger = LoggerFactory.getLogger("love.forte.simbot.component.qguild.message.ImageParser")

    @Volatile
    internal var base64UploadWarn = base64UploadWarnInitialValue

    /**
     * 关闭针对 base64 上传文件的警告。
     *
     * 在 JVM 中，也可以通过JVM参数 `-Dsimbot.qqguild.media.disableBase64UploadWarn=true`
     * 来关闭。
     *
     * @since 4.1.1
     */
    @JvmStatic
    public fun disableBase64UploadWarn() {
        base64UploadWarn = false
    }

    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        // TODO attachment?

        when (element) {
            is Image -> {
                if (element is OfflineImage) {
                    processOfflineImage(index, element, messages, builderContext)
                }
            }

            // TODO more image type support for file_image
        }
    }

    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.GroupAndC2CBuilderContext
    ) {
        // TODO attachment?

        when (element) {
            is Image -> {
                if (element is OfflineImage) {
                    processOfflineImage(index, element, messages, builderContext)
                }
            }

            // TODO more image type support for file_image
        }
    }
}

internal fun processOfflineImage(
    index: Int,
    element: OfflineImage,
    messages: Messages?,
    builderContext: SendingMessageParser.BuilderContext
) {
    if (processOfflineImage0(index, element, messages, builderContext)) {
        return
    }

    when (element) {
        is OfflineResourceImage -> {
            when (val resource = element.resource) {
                is ByteArrayResource -> {
                    builderContext.builderOrNew { it.fileImage == null }.setFileImage(resource.data())
                }

                is SourceResource -> {
                    builderContext.builderOrNew { it.fileImage == null }.setFileImage(
                        ChannelProvider {
                            // TODO 等待更新到 Ktor 3.x，现在性能略差
                            resource.source().use { ByteReadChannel(it.readByteArray()) }
                        }
                    )
                }
            }
        }

        else -> {
            builderContext.builderOrNew { it.fileImage == null }
                .setFileImage(ByteReadPacket(element.data()))
        }
    }
}

internal expect fun processOfflineImage0(
    index: Int,
    element: OfflineImage,
    messages: Messages?,
    builderContext: SendingMessageParser.BuilderContext
): Boolean

internal suspend fun processOfflineImage(
    index: Int,
    element: OfflineImage,
    messages: Messages?,
    builderContext: SendingMessageParser.GroupAndC2CBuilderContext
) {
    processOfflineImage0(index, element, messages, builderContext)
}

/**
 * MEDIA 可以和文字类型相融合，
 * 因此也接受文字类型，事后将其类型直接修改为 MEDIA
 */
internal fun isTextOrMedia(type: Int) = when (type) {
    GroupAndC2CSendBody.MSG_TYPE_TEXT,
    GroupAndC2CSendBody.MSG_TYPE_MEDIA -> true

    else -> false
}

internal expect suspend fun processOfflineImage0(
    index: Int,
    element: OfflineImage,
    messages: Messages?,
    builderContext: SendingMessageParser.GroupAndC2CBuilderContext
): Boolean

@OptIn(ExperimentalQGMediaApi::class)
internal suspend fun processBase64OfflineImage(
    index: Int,
    element: OfflineImage,
    resource: Resource?,
    data: ByteArray,
    builderContext: SendingMessageParser.GroupAndC2CBuilderContext
): Boolean {
    fun builder() = builderContext.builderOrNew {
        isTextOrMedia(it.msgType) && it.media == null
    }.also {
        it.msgType = GroupAndC2CSendBody.MSG_TYPE_MEDIA
    }

    val type = builderContext.type
    ImageParser.logger.debug(
        "Uploading offline image via base64 to {} with target {}",
        type,
        builderContext.targetOpenid
    )

    if (ImageParser.base64UploadWarn) {
        ImageParser.logger.warn(
            "Uploading media to QQGroup or C2C using base64 is still experimental now. " +
                    "(for index={}, type={}, element={})" +
                    "The official documentation does not describe this capability and is therefore unstable. " +
                    "Please give preference to using `URIResource`, `OfflineURIImage` " +
                    "or `QGMedia` instance " +
                    "or see `ImageParser.disableBase64UploadWarn()` to disable this warn log.",
            element::class,
            index,
            element,
        )
    }

    val uploadedMedia = when (type) {
        GROUP -> {
            builderContext.bot.uploadGroupMedia(
                target = builderContext.targetOpenid.ID,
                resource = resource ?: data.toResource(),
                type = UploadGroupFilesApi.FILE_TYPE_IMAGE,
            )
        }

        C2C -> {
            builderContext.bot.uploadUserMedia(
                target = builderContext.targetOpenid.ID,
                resource = resource ?: data.toResource(),
                type = UploadUserFilesApi.FILE_TYPE_IMAGE,
            )
        }
    }

    ImageParser.logger.debug(
        "Uploaded offline image via base64 to media {}",
        uploadedMedia
    )

    val builder = builder()
    builder.media = uploadedMedia.media
    if (builder.content.isEmpty()) {
        builder.content = " "
    }

    return true
}

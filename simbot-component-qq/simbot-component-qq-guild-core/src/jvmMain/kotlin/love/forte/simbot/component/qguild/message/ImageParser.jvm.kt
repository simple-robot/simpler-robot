/*
 * Copyright (c) 2024-2025. ForteScarlet.
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

import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.message.SendingMessageParser.GroupBuilderType.C2C
import love.forte.simbot.component.qguild.message.SendingMessageParser.GroupBuilderType.GROUP
import love.forte.simbot.message.Messages
import love.forte.simbot.message.OfflineImage
import love.forte.simbot.message.OfflineResourceImage
import love.forte.simbot.qguild.api.files.UploadGroupFilesApi
import love.forte.simbot.qguild.api.files.UploadUserFilesApi
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.resource.FileResource
import love.forte.simbot.resource.PathResource
import love.forte.simbot.resource.URIResource

internal actual fun processOfflineImage0(
    index: Int,
    element: OfflineImage,
    messages: Messages?,
    builderContext: SendingMessageParser.BuilderContext
): Boolean {
    if (element is OfflineResourceImage) {
        when (val resource = element.resource) {
            is FileResource -> {
                builderContext.builderOrNew { it.fileImage == null }.fileImage = resource.file
                return true
            }

            is PathResource -> {
                builderContext.builderOrNew { it.fileImage == null }.fileImage = resource.path
                return true
            }

            is URIResource -> {
                builderContext.builderOrNew { it.fileImage == null }.fileImage = resource.uri
                return true
            }

            else -> return false
        }
    }

    return false
}

internal actual suspend fun processOfflineImage0(
    index: Int,
    element: OfflineImage,
    messages: Messages?,
    builderContext: SendingMessageParser.GroupAndC2CBuilderContext
): Boolean {
    // TODO Upload 目前只支持 URL 链接的格式
    //   2024/10/31 update: 群聊可以用 `file_data` 放 base64 数据

    fun builder() = builderContext.builderOrNew {
        isTextOrMedia(it.msgType) && it.media == null
    }.also {
        it.msgType = GroupAndC2CSendBody.MSG_TYPE_MEDIA
    }

    val url = when (element) {
        is OfflineResourceImage -> {
            val resource = element.resource
            if (resource is URIResource) {
                resource.uri.toASCIIString()
            } else {
                return processBase64OfflineImage(
                    index,
                    element,
                    resource,
                    runCatching {
                        resource.data()
                    }.getOrElse { e ->
                        throw IllegalStateException("Failed to read data from resource $resource", e)
                    },
                    builderContext
                )
            }
        }

        else ->
            return processBase64OfflineImage(
                index,
                element,
                null,
                runCatching {
                    element.data()
                }.getOrElse { e ->
                    throw IllegalStateException("Failed to read data from offlineImage $element", e)
                },
                builderContext
            )
    }

    val type = builderContext.type
    ImageParser.logger.debug("Uploading offline image to {} with target {}", type, builderContext.targetOpenid)

    val uploadedMedia = when (type) {
        GROUP -> {
            builderContext.bot.uploadGroupMedia(
                target = builderContext.targetOpenid.ID,
                url = url,
                type = UploadGroupFilesApi.FILE_TYPE_IMAGE,
            )
        }

        C2C -> {
            builderContext.bot.uploadUserMedia(
                target = builderContext.targetOpenid.ID,
                url = url,
                type = UploadUserFilesApi.FILE_TYPE_IMAGE,
            )
        }
    }

    ImageParser.logger.debug("Uploaded offline image to media {}", uploadedMedia)

    val builder = builder()
    builder.media = uploadedMedia.media
    if (builder.content.isEmpty()) {
        builder.content = " "
    }

    return true
}

internal actual val base64UploadWarnInitialValue: Boolean by lazy {
    !System.getProperty(JVM_DISABLE_BASE64_UPLOAD_WARN).toBoolean()
}

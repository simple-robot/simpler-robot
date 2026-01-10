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

@file:JvmName("MessageElementResolvers")

package love.forte.simbot.component.onebot.v11.message

import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI
import love.forte.simbot.component.onebot.v11.message.segment.*
import love.forte.simbot.message.*
import love.forte.simbot.resource.Resource
import love.forte.simbot.resource.toResource
import org.jetbrains.annotations.ApiStatus
import kotlin.jvm.JvmName

/**
 * 将事件中接收到的 [OneBotMessageSegment] 解析为 [Message.Element]。
 *
 * | 原类型 | 转化目标 |
 * | ---- | ---- |
 * | [OneBotAt] | [At] 或 [AtAll] |
 * | [OneBotFace] | [Face] |
 * | 其他 | 使用 [toElement] 转化 |
 */
@InternalOneBotAPI
@ApiStatus.Internal // since 1.6.0
public fun OneBotMessageSegment.resolveToMessageElement(): Message.Element {
    return when (this) {
        is OneBotAt -> if (isAll) AtAll else At(data.qq.ID)
        is OneBotFace -> Face(data.id)
        else -> toElement()
    }
}

/**
 * @since 1.9.0
 */
@InternalOneBotAPI
public typealias DefaultImageAdditionalParams = ((Resource) -> OneBotImage.AdditionalParams?)

/**
 * 将 [Message] 解析为一用于API请求的 [OneBotMessageSegment] 列表。
 *
 * @see resolveToOneBotSegment
 */
@InternalOneBotAPI
@ApiStatus.Internal // since 1.6.0
// @JvmSynthetic // since 1.6.0 不再向Java隐藏，且不再挂起
public fun Message.resolveToOneBotSegmentList(
    defaultImageAdditionalParams: DefaultImageAdditionalParams? = null,
): List<OneBotMessageSegment> {
    return when (this) {
        is Message.Element -> resolveToOneBotSegment(defaultImageAdditionalParams)
            ?.let { listOf(it) }
            ?: emptyList()

        is Messages -> mapNotNull { it.resolveToOneBotSegment(defaultImageAdditionalParams) }
    }
}

/**
 * 将一个 [Message.Element] 转化为用于API请求的 [OneBotMessageSegment]。
 */
@InternalOneBotAPI
@ApiStatus.Internal // since 1.6.0
// @JvmSynthetic // since 1.6.0 不再向Java隐藏，且不再挂起
public fun Message.Element.resolveToOneBotSegment(
    defaultImageAdditionalParams: DefaultImageAdditionalParams? = null,
): OneBotMessageSegment? {
    return when (this) {
        // OB组件的 segment 类型，直接使用
        is OneBotMessageSegmentElement -> segment
        // stand messages
        is Text -> OneBotText.create(text)
        is Face -> OneBotFace.create(id)
        is At -> OneBotAt.create(target)
        is AtAll -> OneBotAt.createAtAll()
        is Image -> when (this) {
            // offline image
            is OfflineImage -> resolveOfflineImage(this, defaultImageAdditionalParams)

            // remote images, OneBot组件中实际上没有此类型的实现
            // 将它的 id 直接视为 file
            is RemoteImage -> OneBotImage.create(id.literal)

            // 其他未知类型，不管
            else -> null
        }

        // since 1.2.0
        is MessageReference -> OneBotReply.create(id)

        // 其他未知类型，不管
        else -> null
    }
}

private fun resolveOfflineImage(
    image: OfflineImage,
    defaultImageAdditionalParams: DefaultImageAdditionalParams?
): OneBotMessageSegment? {
    val resource = when (image) {
        is OfflineByteArrayImage -> image.data().toResource()
        is OfflineResourceImage -> image.resource
    }

    val additional = defaultImageAdditionalParams?.invoke(resource)
    return OneBotImage.create(resource, additional)
}

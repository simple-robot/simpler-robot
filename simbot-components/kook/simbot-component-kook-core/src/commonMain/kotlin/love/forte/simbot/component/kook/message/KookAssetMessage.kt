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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.kook.api.asset.Asset
import love.forte.simbot.kook.api.asset.CreateAssetApi
import love.forte.simbot.kook.messages.MessageType
import love.forte.simbot.message.Image
import love.forte.simbot.message.RemoteImage
import love.forte.simbot.message.RemoteUrlAwareImage
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * 与上传后的媒体资源相关的消息类型。
 *
 * @see KookAsset
 * @see KookAssetImage
 *
 */
@Serializable
public sealed class KookAssetMessage : KookMessageElement {
    /**
     * 创建的文件资源。
     */
    public abstract val asset: Asset

    /**
     * 在发送时所需要使用的消息类型。通常选择为 [MessageType.IMAGE]、[MessageType.FILE]、[MessageType.VIDEO] 中的值，
     * 即 `2`、`3`、`4`。
     *
     * @see MessageType
     */
    public abstract val type: Int

    public companion object {
        /**
         * 使用当前的 [Asset] 构建一个 [KookAssetMessage] 实例。
         * @param type 在发送时所需要使用的消息类型。通常选择为 [MessageType.IMAGE]、[MessageType.FILE]、[MessageType.VIDEO] 中的值，即 `2`、`3`、`4`。
         */
        @JvmStatic
        public fun Asset.asMessage(type: Int): KookAsset = KookAsset(this, type)

        /**
         * 使用当前的 [Asset] 构建一个 [KookAssetMessage] 实例。
         * @param type 在发送时所需要使用的消息类型。通常选择为 [MessageType.IMAGE]、[MessageType.FILE]、[MessageType.VIDEO] 中的值。
         */
        @JvmStatic
        public fun Asset.asMessage(type: MessageType): KookAsset = KookAsset(this, type)


        /**
         * 使用当前的 [Asset] 构建一个 [KookAssetImage] 实例。
         */
        @JvmStatic
        public fun Asset.asImage(): KookAssetImage = KookAssetImage(this)
    }

    override fun toString(): String {
        return "KookAssetMessage(asset=$asset, type=$type)"
    }
}

/**
 * Kook 组件中针对 [CreateAssetApi] api 的请求响应的消息封装。
 *
 * @author ForteScarlet
 */
@SerialName("kook.asset.std")
@Serializable
public data class KookAsset(
    /**
     * 创建的文件资源。
     */
    override val asset: Asset,

    /**
     * 在发送时所需要使用的消息类型。通常选择为 [MessageType.IMAGE]、[MessageType.FILE]、[MessageType.VIDEO] 中的值，
     * 即 `2`、`3`、`4`。
     *
     * @see MessageType
     */
    @SerialName("assetType")
    override val type: Int
) : KookAssetMessage() {
    public constructor(asset: Asset, type: MessageType) : this(asset, type.type)
}


/**
 * 使用 [Asset] 作为一个 [Image] 消息类型。
 * [Asset] 是上传后的产物，因此 [KookAssetImage] 可以被视为 [RemoteImage]。
 */
@SerialName("kook.asset.img")
@Serializable
public data class KookAssetImage(override val asset: Asset) : KookAssetMessage(), RemoteUrlAwareImage {
    override val type: Int
        get() = MessageType.IMAGE.type

    /**
     * @see Asset.url
     */
    override val id: ID
        get() = assetUrl.ID

    /**
     * @see Asset.url
     */
    public val assetUrl: String
        get() = asset.url

    @JvmSynthetic
    override suspend fun url(): String = assetUrl
}



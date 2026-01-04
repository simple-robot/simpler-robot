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
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [音乐分享](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E9%9F%B3%E4%B9%90%E5%88%86%E4%BA%AB-)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(OneBotMusic.TYPE)
public class OneBotMusic private constructor(
    override val data: Data
) : OneBotMessageSegment {

    /**
     * @see Data.type
     */
    public val musicType: String
        get() = data.type

    public companion object Factory {
        public const val TYPE: String = "music"

        /**
         * 当构建用于发送的 `custom` 类型内容时使用的 [Data.type] 值。
         */
        @Suppress("MemberVisibilityCanBePrivate")
        public const val CUSTOM_DATA_TYPE: String = "custom"

        /**
         * Create [OneBotMusic].
         */
        @JvmStatic
        public fun create(type: String, id: String): OneBotMusic =
            OneBotMusic(Data(type, id))

        /**
         * Create a `custom` [OneBotMusic].
         * @param url 点击后跳转目标 URL
         * @param audio 音乐 URL
         * @param title 标题
         * @param content 发送时可选，内容描述
         * @param image 发送时可选，图片 URL
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            url: String,
            audio: String,
            title: String,
            content: String? = null,
            image: String? = null,
        ): OneBotMusic =
            OneBotMusic(
                Data(
                    type = CUSTOM_DATA_TYPE,
                    id = null,
                    url = url,
                    audio = audio,
                    title = title,
                    content = content,
                    image = image,
                )
            )
    }

    /**
     * 参考 [音乐分享](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E9%9F%B3%E4%B9%90%E5%88%86%E4%BA%AB-)
     *
     * @property type qq 163 xm	分别表示使用 QQ 音乐、网易云音乐、虾米音乐
     * @property id 歌曲 ID
     * @property url (自定义歌曲发送时) 点击后跳转目标 URL
     * @property audio (自定义歌曲发送时) 音乐 URL
     * @property title (自定义歌曲发送时) 标题
     * @property content (自定义歌曲发送时) 发送时可选，内容描述
     * @property image (自定义歌曲发送时) 发送时可选，图片 URL
     */
    @Serializable
    public data class Data(
        val type: String,
        val id: String? = null,
        val url: String? = null,
        val audio: String? = null,
        val title: String? = null,
        val content: String? = null,
        val image: String? = null,
    )
}


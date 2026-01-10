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

package love.forte.simbot.kook.objects

import kotlinx.serialization.Serializable

/**
 *
 * [附加的多媒体数据 Attachments](https://developer.kaiheila.cn/doc/objects#%E9%99%84%E5%8A%A0%E7%9A%84%E5%A4%9A%E5%AA%92%E4%BD%93%E6%95%B0%E6%8D%AEAttachments)
 *
 * @author ForteScarlet
 */
public interface Attachments {
    /**
     * Type 多媒体类型.
     * - `file` -> 文件/附件
     * - `image` -> 图片
     * - `video` -> 视频
     * - 可能还有其他？
     */
    public val type: String

    /**
     * Url 多媒体地址
     */
    public val url: String

    /**
     * Name 多媒体名
     */
    public val name: String

    /**
     * Size 大小 单位（B）
     * 假如无法获取，得到-1.
     */
    public val size: Long
}

/**
 * [Attachments] 的最基础实现。
 *
 * @see Attachments
 */
@Serializable
public data class SimpleAttachments(
    override val type: String = "",
    override val url: String = "",
    override val name: String = "",
    override val size: Long = -1
) : Attachments

/*
        // 文件
        //                "attachments": {
        //                    "type": "file",
        //                    "url": "https://xxx.txt",
        //                    "name": "***.txt",
        //                    "file_type": "text/plain",
        //                    "size": 540579
        //                },
        // 视频
        // "attachments": {
        //     "type": "video",
        //     "url": "https://xxxxx.mp4",
        //     "name": "***.mp4",
        //     "duration": 15.472,
        //     "size": 2575670,
        //     "width": 480,
        //     "height": 960
        // },
 */

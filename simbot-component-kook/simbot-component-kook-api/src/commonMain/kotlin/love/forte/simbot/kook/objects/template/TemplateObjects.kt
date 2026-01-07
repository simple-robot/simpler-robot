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

package love.forte.simbot.kook.objects.template

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.template.ExperimentalTemplateApi

/**
 * 消息模板对象，表示一个消息模板。
 *
 * [消息模板](https://developer.kookapp.cn/doc/http/template)
 *
 * @author ForteScarlet
 * @since 4.3.0
 */
@ExperimentalTemplateApi
public interface Template {
    /**
     * 模板的id,最长16
     */
    public val id: String

    /**
     * 模型的标题，最长64
     */
    public val title: String

    /**
     * 目前固定为0，代表模型使用twig渲染
     */
    public val type: Int

    /**
     * 1代表kmd消息，2代表通过json发卡片消息，3代表通过yaml发卡片消息
     */
    public val msgtype: Int

    /**
     * 0代表未审核，1代表审核中，2代表审核通过，3代表审核拒绝，当前没有开发审核，都为0
     */
    public val status: Int

    /**
     * 测试数据， 主要用于界面上的便利测试
     */
    public val testData: String

    /**
     * 测试的频道，最长64。 主要用于界面上的便利测试
     */
    public val testChannel: String

    /**
     * 模板内容
     */
    public val content: String
}

/**
 * [Template] 的简单实现。
 *
 * @since 4.3.0
 */
@Serializable
@ExperimentalTemplateApi
public data class SimpleTemplate @ApiResultType constructor(
    override val id: String,
    override val title: String,
    override val type: Int = 0,
    override val msgtype: Int = 1,
    override val status: Int = 0,
    @SerialName("test_data")
    override val testData: String = "",
    @SerialName("test_channel")
    override val testChannel: String = "",
    override val content: String = ""
) : Template

/*
 * Copyright (c) 2022-2023. ForteScarlet.
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

package love.forte.simbot.qguild.api.message

import love.forte.simbot.qguild.message.buildArk
import love.forte.simbot.qguild.model.Message


/**
 * 官方提供的ark消息模板。
 *
 * [参考文档](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_template.html)
 *
 */
public sealed class ArkMessageTemplates {

    public abstract val ark: Message.Ark

    /**
     * ## 23 链接+文本列表模板
     *
     * see [23 链接+文本列表模板](https://bot.q.qq.com/wiki/develop/api/openapi/message/template/template_23.html)
     *
     * @param desc
     * @param prompt
     * @param list
     */
    public class TextLinkList(
        desc: String,
        prompt: String,
        list: List<Desc> = emptyList()
    ) : ArkMessageTemplates() {
        private companion object {
            const val ID = "23"
        }

        override val ark: Message.Ark = buildArk(ID) {
            kvs {
                kv("#DESC#", desc)
                kv("#PROMPT#", prompt)
                if (list.isNotEmpty()) {
                    kv("#LIST#") {
                        list.forEach { desc ->
                            obj {
                                kv("desc", desc.desc)
                                desc.link?.also { link ->
                                    kv("link", link)
                                }
                            }
                        }
                    }
                }
            }
        }

        public data class Desc(public val desc: String, public val link: String? = null)
    }


    /**
     * ## 24 文本+缩略图模板
     *
     * see [24 文本+缩略图模板](https://bot.q.qq.com/wiki/develop/api/openapi/message/template/template_24.html)
     *
     * @param desc 描述
     * @param prompt 提示文本
     * @param title 标题
     * @param metaDesc 详情描述
     * @param img 图片链接
     * @param link 跳转链接
     * @param subtitle 来源
     */
    public class TextThumbnail(
        desc: String,
        prompt: String,
        title: String,
        metaDesc: String,
        img: String,
        link: String,
        subtitle: String,
    ) : ArkMessageTemplates() {
        private companion object {
            const val ID = "24"
        }

        override val ark: Message.Ark = buildArk(ID) {
            kv("#DESC#", desc)
            kv("#PROMPT#", prompt)
            kv("#TITLE#", title)
            kv("#METADESC#", metaDesc)
            kv("#IMG#", img)
            kv("#LINK#", link)
            kv("#SUBTITLE#", subtitle)
        }
    }


    /**
     * ## 37 大图模板
     *
     * see [37 大图模板](https://bot.q.qq.com/wiki/develop/api/openapi/message/template/template_37.html)
     *
     * @param prompt 提示消息
     * @param metaTitle 标题
     * @param metaSubtitle 子标题
     * @param metaCover 大图，尺寸为 975*540
     * @param metaUrl 跳转链接
     */
    public class BigImg(
        prompt: String,
        metaTitle: String,
        metaSubtitle: String,
        metaCover: String,
        metaUrl: String,
    ) : ArkMessageTemplates() {
        private companion object {
            const val ID = "37"
        }

        override val ark: Message.Ark = buildArk(ID) {
            kv("#PROMPT#", prompt)
            kv("#METATITLE#", metaTitle)
            kv("#METASUBTITLE#", metaSubtitle)
            kv("#METACOVER#", metaCover)
            kv("#METAURL#", metaUrl)
        }
    }


}





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

package love.forte.simbot.qguild.api.message

import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.message.buildArk
import love.forte.simbot.qguild.model.Message
import kotlin.jvm.JvmStatic


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

        /**
         * 模板中的文本链接条目。
         *
         * @property desc 条目描述。
         * @property link 条目链接。
         */
        public class Desc @ApiModelConstructor public constructor(
            public val desc: String,
            public val link: String? = null,
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Desc) return false

                if (desc != other.desc) return false
                if (link != other.link) return false

                return true
            }

            override fun hashCode(): Int {
                var result = desc.hashCode()
                result = 31 * result + link.hashCode()
                return result
            }

            override fun toString(): String = "Desc(desc=$desc, link=$link)"

            //region data-class 兼容
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("desc"))
            public operator fun component1(): String = desc

            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("link"))
            public operator fun component2(): String? = link

            @Suppress("DeprecatedCallableAddReplaceWith")
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
            public fun copy(
                desc: String = this.desc,
                link: String? = this.link,
            ): Desc = Desc(desc, link)
            //endregion

            public companion object {
                /**
                 * 构造一个 [Desc]。
                 *
                 * @since 5.0
                 */
                @JvmStatic
                public fun of(desc: String, link: String? = null): Desc = Desc(desc, link)
            }
        }
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




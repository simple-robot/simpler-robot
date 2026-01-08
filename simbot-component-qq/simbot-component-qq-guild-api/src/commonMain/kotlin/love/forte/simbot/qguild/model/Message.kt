/*
 * Copyright (c) 2022-2025. ForteScarlet.
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

package love.forte.simbot.qguild.model

import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.message.EmbedBuilder
import love.forte.simbot.qguild.time.ZERO_ISO_INSTANT
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * [消息对象(Message)](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html)
 *
 * 消息对象的完整定义。
 *
 */
@ApiModel
@Serializable
public data class Message(
    /**
     * 消息 id
     */
    public val id: String,

    /**
     * 子频道 id
     */
    @SerialName("channel_id") public val channelId: String,

    /**
     * 频道 id
     */
    @SerialName("guild_id") public val guildId: String,

    /**
     * 消息内容
     */
    public val content: String,

    /**
     * ISO8601 timestamp	消息创建时间
     */
    public val timestamp: String,

    /**
     * ISO8601 timestamp	消息编辑时间
     */
    @SerialName("edited_timestamp") public val editedTimestamp: String? = null,

    /**
     * 是否是@全员消息
     */
    @SerialName("mention_everyone") public val mentionEveryone: Boolean = false,

    /**
     * 消息创建者
     */
    public val author: User,

    /**
     * MessageAttachment 对象数组	附件
     */
    public val attachments: List<Attachment> = emptyList(),

    /*
        图片：
        "attachments": [{
			"content_type": "image/png",
			"filename": "{FF889886-687E-8027-14D8-9A5BC6620CF8}.png",
			"height": 187,
			"id": "2649174084",
			"size": 44182,
			"url": "gchat.qpic.cn/qmeetpic/47750961638939775/1701030-2649174084-FF889886687E802714D89A5BC6620CF8/0",
			"width": 236
		}],

		"attachments": [{
			"content_type": "image/gif",
			"filename": "{59CA8472-3FBB-98E9-9284-FB9D3D3148BC}.jpg",
			"height": 240,
			"id": "2249362854",
			"size": 499562,
			"url": "gchat.qpic.cn/qmeetpic/47750961638939775/1701030-2249362854-59CA84723FBB98E99284FB9D3D3148BC/0",
			"width": 240
		}],
     */

    /**
     * embeds	MessageEmbed 对象数组	embed
     */
    public val embeds: List<Embed> = emptyList(),

    /**
     * User 对象数组	消息中@的人
     */
    public val mentions: List<User> = emptyList(),

    /**
     * Member 对象	消息创建者的member信息
     *
     * 发送消息的回执中不存在此 member
     */
    public val member: MessageMember? = null,

    /**
     * ark消息对象	ark消息
     */
    public val ark: Ark? = null,

    /**
     * `seq_in_channel` string
     *
     * 子频道消息 seq，用于消息间的排序，seq 在同一子频道中按从先到后的顺序递增，不同的子频道之间消息无法排序
     */
    @SerialName("seq_in_channel") public val seqInChannel: String,

    /**
     * 引用消息对象
     */
    @SerialName("message_reference") public val messageReference: Reference? = null,

    /**
     * 用于私信场景下识别真实的来源频道id
     */
    @SerialName("src_guild_id") val srcGuildId: String? = null

) {
    init {
        member?.user = author
    }

    /**
     * [embed 消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/template/embed_message.html)
     *
     * @see EmbedBuilder
     */
    @Serializable
    public data class Embed(
        /**
         * 标题
         */
        val title: String,

        /**
         * 消息弹窗内容
         */
        val prompt: String,

        /**
         * 缩略图，选填
         */
        val thumbnail: Thumbnail? = null,

        /**
         * 字段信息
         */
        val fields: List<Field> = emptyList()
    ) {

        /**
         * [缩略图](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageembedthumbnail)
         *
         */
        @Serializable
        public data class Thumbnail(
            /**
             * 图片地址
             */
            val url: String
        )

        /**
         * [MessageEmbedField](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageembedfield)
         */
        @Serializable
        public data class Field(
            /**
             * 字段名
             */
            public val name: String,

            /**
             * 字段值
             *
             * _Note: 似乎已经不存在了_
             */
            @Deprecated("not exists", level = DeprecationLevel.HIDDEN)
            @Transient
            public val value: String? = null,
        )
    }


    /**
     * [MessageAttachment](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageattachment)
     *
     * 在接收的时候似乎会针对不同类型存在额外的 [properties]，
     * 例如对于图片文件接收到的信息实际为：
     *
     * ```json
     * {
     *    "content_type": "image/gif",
     *    "filename": "{59CA8472-3FBB-98E9-9284-FB9D3D3148BC}.jpg",
     *    "height": 240,
     *    "id": "2249362854",
     *    "size": 499562,
     *    "url": "gchat.qpic.cn/qmeetpic/47750961638939775/1701030-2249362854-59CA84723FBB98E99284FB9D3D3148BC/0",
     *    "width": 240
     * }
     * ```
     * 因此 [Attachment] 的实际序列化借助 k/v 均为 [String] 的 [Map] 进行，并尝试从其中提取 `url` 属性赋于 [url] 中。
     *
     */
    @Serializable(MessageAttachmentSerializer::class)
    public data class Attachment(
        /**
         * 下载地址
         */
        public val url: String,

        /**
         * 此 attachment 中接收到的所有属性。
         */
        public val properties: Map<String, String> = emptyMap()
    )


    /**
     * [MessageArk](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageark)
     */
    @Serializable
    public data class Ark(
        /**
         * ark模板id（需要先申请）
         */
        @SerialName("template_id") public val templateId: String,

        /**
         * kv值列表
         */
        public val kv: List<Kv> = emptyList()
    ) {

        /**
         * [MessageArkKv](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagearkkv)
         */
        @Serializable
        public data class Kv(
            public val key: String, public val value: String? = null, public val obj: List<Obj> = emptyList()
        )

        /**
         * [MessageArkObj](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagearkobj)
         */
        @Serializable
        public data class Obj(
            /** ark obj kv列表 */
            @SerialName("obj_kv") public val objKv: List<Kv> = emptyList()
        ) {

            /**
             * [MessageArkObjKv](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagearkobjkv)
             */
            @Serializable
            public data class Kv(public val key: String, public val value: String)
        }


    }

    /**
     * [MessageReference](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagereference)
     */
    @Serializable
    public data class Reference(
        /**
         * 需要引用回复的消息 id
         */
        @SerialName("message_id") val messageId: String,

        /**
         * 是否忽略获取引用消息详情错误，默认否
         */
        @SerialName("ignore_get_message_error") val ignoreGetMessageError: Boolean = false
    )

    /**
     * [MessageMarkdown](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagemarkdown)
     */
    @Serializable
    public data class Markdown(
        /**
         * markdown 模板 id
         */
        @SerialName("template_id") val templateId: Int? = null,

        /**
         * markdown 自定义模板 id
         */
        @SerialName("custom_template_id") val customTemplateId: String? = null,

        /**
         * markdown 模板模板参数
         *
         * Note: 在 4.1.4 中修改为了列表类型 `List<Param>` ，在那之前是单个元素类型 `Params?`，
         * 但是这似乎是错误的，因此我们修正了它。而由于他作为一个公开暴露的 `data class`, 对他的修改可能产生的兼容性问题是被默许的，
         * 这也是我们提供了工厂API而不建议你直接通过构造函数创建它的原因，如果你原本使用工厂函数构建，则不会存在兼容性问题。
         * 同时，我们保留了原本的的 [Params] 并标记废弃，用于警告原本API的使用者。
         */
        val params: List<Param>? = null,

        /**
         * 原生 markdown 内容,与上面三个参数互斥,参数都传值将报错。
         */
        val content: String? = null,
    ) {

        /**
         * [MessageMarkdownParams](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#MessageMarkdownParams)
         *
         * Deprecated: 在 4.1.4 中 [Markdown.params] 被修改以修复原本错误的定义，
         * 并由此提供了更符合语义的 [Param]。
         *
         * @see Param
         */
        @Serializable
        @Deprecated("在 4.1.4 中 [Markdown.params] 被修改以修复原本错误的定义，" +
                    "并由此提供了更符合语义的 [Param]。",
            replaceWith = ReplaceWith(
                "Param(key, values)",
                "love.forte.simbot.qguild.model.Message.Markdown.Param"
            )
        )
        public data class Params(
            /**
             * markdown 模版 key
             */
            val key: String,
            /**
             * markdown 模版 [key] 对应的 `values` .
             *
             * > 列表长度大小为 1，传入多个会报错
             *
             * _但代码层面暂时不做验证限制。_
             *
             */
            val values: List<String>
        )

        /**
         * [MessageMarkdownParams](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/type/markdown.html#%E5%8F%91%E9%80%81%E6%96%B9%E5%BC%8F)
         */
        @Serializable
        public data class Param(
            /**
             * markdown 模版 key
             */
            val key: String,
            /**
             * markdown 模版 [key] 对应的 `values` .
             *
             * > 列表长度大小为 1，传入多个会报错
             *
             * _但代码层面暂时不做验证限制。_
             *
             */
            val values: List<String>
        )

        public companion object {
            @JvmStatic
            public fun createByContent(content: String): Markdown {
                return Markdown(content = content)
            }

            @JvmStatic
            @Deprecated(
                "Use createByTemplateId(templateId, Param(...))",
                replaceWith = ReplaceWith(
                    "createByTemplateId(templateId, params?.let { Param(it.key, it.values) })",
                    "love.forte.simbot.qguild.model.Message.Markdown.Param"
                )
            )
            @Suppress("DEPRECATION")
            public fun createByTemplateId(templateId: Int, params: Params? = null): Markdown {
                return createByTemplateId(
                    templateId = templateId,
                    param = params?.let { Param(it.key, it.values) }
                )
            }

            @JvmStatic
            @JvmOverloads
            public fun createByTemplateId(templateId: Int, param: Param? = null): Markdown {
                return createByTemplateId(templateId = templateId, params = param?.let { listOf(it) })
            }

            @JvmStatic
            public fun createByTemplateId(templateId: Int, params: List<Param>? = null): Markdown {
                return Markdown(templateId = templateId, params = params)
            }

            @JvmStatic
            @Deprecated(
                "Use createByCustomTemplateId(customTemplateId, Param(...))",
                replaceWith = ReplaceWith(
                    "createByCustomTemplateId(customTemplateId, params?.let { Param(it.key, it.values) })",
                )
            )
            @Suppress("DEPRECATION")
            public fun createByCustomTemplateId(customTemplateId: String, params: Params? = null): Markdown {
                return createByCustomTemplateId(
                    customTemplateId = customTemplateId,
                    param = params?.let { Param(it.key, it.values) }
                )
            }

            @JvmStatic
            @JvmOverloads
            public fun createByCustomTemplateId(customTemplateId: String, param: Param? = null): Markdown {
                return Markdown(customTemplateId = customTemplateId, params = param?.let { listOf(it) })
            }

            @JvmStatic
            public fun createByCustomTemplateId(customTemplateId: String, params: List<Param>? = null): Markdown {
                return Markdown(customTemplateId = customTemplateId, params = params)
            }
        }
    }
}

/**
 * 在 [MessageMember] 中使用的 [Member] 实现。
 * 其中的 [user] 来自 [Message.author]
 */
@ApiModel
@Serializable
public data class MessageMember(
    override val nick: String = "",
    override val roles: List<String> = emptyList(),
    @SerialName("joined_at")
    override val joinedAt: String = ZERO_ISO_INSTANT
) : Member {

    @Transient
    @set:JvmSynthetic // hide setter
    override lateinit var user: User
        internal set

    public companion object {

        /**
         * 将任意 [Member] 类型转化为 [MessageMember].
         * 如果类型本身即为 [MessageMember] 则返回自身，否则得到新的实例。
         */
        @JvmStatic
        public fun Member.asMessageMember(): MessageMember =
            if (this is MessageMember) this else MessageMember(nick, roles, joinedAt).also { it.user = user }
    }
}


internal object MessageAttachmentSerializer : KSerializer<Message.Attachment> {
    private val serializer = MapSerializer(String.serializer(), String.serializer())

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): Message.Attachment {
        val properties = serializer.deserialize(decoder)
        val url = properties["url"] ?: throw MissingFieldException("url", "Message.Attachment")
        return Message.Attachment(url, properties)
    }

    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun serialize(encoder: Encoder, value: Message.Attachment) {
        var map = value.properties
        if ("url" !in map) {
            map = buildMap(map.size) {
                putAll(map)
                put("url", value.url)
            }
        }

        serializer.serialize(encoder, map)
    }
}

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

package love.forte.simbot.qguild.model

import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.common.QQ
import love.forte.simbot.qguild.message.EmbedBuilder
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * [消息对象(Message)](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html)
 *
 * 消息对象的完整定义。
 *
 * @property id 消息 ID。
 * @property channelId 子频道 ID。
 * @property guildId 频道 ID。
 * @property content 消息内容。
 * @property timestamp 消息创建时间。
 * @property editedTimestamp 消息编辑时间。
 * @property mentionEveryone 是否是 `@` 全员消息。
 * @property author 消息创建者。
 * @property attachments 附件。
 * @property embeds Embed 对象数组。
 * @property mentions 消息中 `@` 的用户。
 * @property member 消息创建者的成员信息。
 * @property ark Ark 消息对象。
 * @property seqInChannel 子频道消息序号。
 * @property messageReference 引用消息对象。
 * @property srcGuildId 私信场景下真实的来源频道 ID。
 */
@ApiModel
@Serializable
public class Message @ApiModelConstructor constructor(
    public val id: String,
    @SerialName("channel_id") public val channelId: String,
    @SerialName("guild_id") public val guildId: String,
    public val content: String,
    public val timestamp: String,
    @SerialName("edited_timestamp") public val editedTimestamp: String? = null,
    @SerialName("mention_everyone") public val mentionEveryone: Boolean = false,
    public val author: User,
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

    public val embeds: List<Embed> = emptyList(),
    public val mentions: List<User> = emptyList(),
    public val member: MessageMember? = null,
    public val ark: Ark? = null,
    @SerialName("seq_in_channel") public val seqInChannel: String,
    @SerialName("message_reference") public val messageReference: Reference? = null,
    @SerialName("src_guild_id") public val srcGuildId: String? = null

) {
    init {
        member?.user = author
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Message) return false

        if (id != other.id) return false
        if (channelId != other.channelId) return false
        if (guildId != other.guildId) return false
        if (content != other.content) return false
        if (timestamp != other.timestamp) return false
        if (editedTimestamp != other.editedTimestamp) return false
        if (mentionEveryone != other.mentionEveryone) return false
        if (author != other.author) return false
        if (attachments != other.attachments) return false
        if (embeds != other.embeds) return false
        if (mentions != other.mentions) return false
        if (member != other.member) return false
        if (ark != other.ark) return false
        if (seqInChannel != other.seqInChannel) return false
        if (messageReference != other.messageReference) return false
        if (srcGuildId != other.srcGuildId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + guildId.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + editedTimestamp.hashCode()
        result = 31 * result + mentionEveryone.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + attachments.hashCode()
        result = 31 * result + embeds.hashCode()
        result = 31 * result + mentions.hashCode()
        result = 31 * result + member.hashCode()
        result = 31 * result + ark.hashCode()
        result = 31 * result + seqInChannel.hashCode()
        result = 31 * result + messageReference.hashCode()
        result = 31 * result + srcGuildId.hashCode()
        return result
    }

    override fun toString(): String {
        return "Message(" +
            "id='$id', channelId='$channelId', guildId='$guildId', content='$content', " +
            "timestamp='$timestamp', editedTimestamp=$editedTimestamp, mentionEveryone=$mentionEveryone, " +
            "author=$author, attachments=$attachments, embeds=$embeds, mentions=$mentions, member=$member, " +
            "ark=$ark, seqInChannel='$seqInChannel', messageReference=$messageReference, srcGuildId=$srcGuildId)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
    public operator fun component1(): String = id

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component2(): String = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component3(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("content"))
    public operator fun component4(): String = content

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("timestamp"))
    public operator fun component5(): String = timestamp

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("editedTimestamp"))
    public operator fun component6(): String? = editedTimestamp

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("mentionEveryone"))
    public operator fun component7(): Boolean = mentionEveryone

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("author"))
    public operator fun component8(): User = author

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("attachments"))
    public operator fun component9(): List<Attachment> = attachments

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("embeds"))
    public operator fun component10(): List<Embed> = embeds

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("mentions"))
    public operator fun component11(): List<User> = mentions

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("member"))
    public operator fun component12(): MessageMember? = member

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("ark"))
    public operator fun component13(): Ark? = ark

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("seqInChannel"))
    public operator fun component14(): String = seqInChannel

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("messageReference"))
    public operator fun component15(): Reference? = messageReference

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("srcGuildId"))
    public operator fun component16(): String? = srcGuildId

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        id: String = this.id,
        channelId: String = this.channelId,
        guildId: String = this.guildId,
        content: String = this.content,
        timestamp: String = this.timestamp,
        editedTimestamp: String? = this.editedTimestamp,
        mentionEveryone: Boolean = this.mentionEveryone,
        author: User = this.author,
        attachments: List<Attachment> = this.attachments,
        embeds: List<Embed> = this.embeds,
        mentions: List<User> = this.mentions,
        member: MessageMember? = this.member,
        ark: Ark? = this.ark,
        seqInChannel: String = this.seqInChannel,
        messageReference: Reference? = this.messageReference,
        srcGuildId: String? = this.srcGuildId,
    ): Message = Message(
        id, channelId, guildId, content, timestamp, editedTimestamp, mentionEveryone,
        author, attachments, embeds, mentions, member, ark, seqInChannel, messageReference, srcGuildId
    )
    //endregion

    /**
     * [embed 消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/template/embed_message.html)
     *
     * @see EmbedBuilder
     * @property title 标题。
     * @property prompt 消息弹窗内容。
     * @property thumbnail 缩略图。
     * @property fields 字段信息。
     */
    @Serializable
    public class Embed(
        public val title: String,
        public val prompt: String,
        public val thumbnail: Thumbnail? = null,
        public val fields: List<Field> = emptyList()
    ) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Embed) return false

            if (title != other.title) return false
            if (prompt != other.prompt) return false
            if (thumbnail != other.thumbnail) return false
            if (fields != other.fields) return false

            return true
        }

        override fun hashCode(): Int {
            var result = title.hashCode()
            result = 31 * result + prompt.hashCode()
            result = 31 * result + thumbnail.hashCode()
            result = 31 * result + fields.hashCode()
            return result
        }

        override fun toString(): String {
            return "Embed(title='$title', prompt='$prompt', " +
                "thumbnail=$thumbnail, fields=$fields)"
        }

        //region data-class 兼容
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("title"))
        public operator fun component1(): String = title

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("prompt"))
        public operator fun component2(): String = prompt

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("thumbnail"))
        public operator fun component3(): Thumbnail? = thumbnail

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("fields"))
        public operator fun component4(): List<Field> = fields

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            title: String = this.title,
            prompt: String = this.prompt,
            thumbnail: Thumbnail? = this.thumbnail,
            fields: List<Field> = this.fields,
        ): Embed = Embed(title, prompt, thumbnail, fields)
        //endregion

        /**
         * [缩略图](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageembedthumbnail)
         *
         * @property url 图片地址。
         */
        @Serializable
        public class Thumbnail(
            public val url: String
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Thumbnail) return false
                return url == other.url
            }

            override fun hashCode(): Int = url.hashCode()

            override fun toString(): String = "Thumbnail(url='$url')"

            //region data-class 兼容
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("url"))
            public operator fun component1(): String = url

            @Suppress("DeprecatedCallableAddReplaceWith")
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
            public fun copy(url: String = this.url): Thumbnail = Thumbnail(url)
            //endregion
        }

        /**
         * [MessageEmbedField](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageembedfield)
         *
         * @property name 字段名。
         */
        @Serializable//(MessageEmbedFieldSerializer::class)
        public class Field(
            public val name: String,
            @EncodeDefault(EncodeDefault.Mode.NEVER)
            public val value: String? = null,
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Field) return false

                if (name != other.name) return false
                if (value != other.value) return false

                return true
            }

            override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + value.hashCode()
                return result
            }

            override fun toString(): String = "Field(name='$name', value=$value)"

            //region data-class 兼容
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("name"))
            public operator fun component1(): String = name

            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("value"))
            public operator fun component2(): String? = value

            @Suppress("DeprecatedCallableAddReplaceWith")
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
            public fun copy(
                name: String = this.name,
                value: String? = this.value,
            ): Field = Field(name, value)
            //endregion
        }
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
     *
     * @property url 下载地址。
     * @property properties 此 attachment 中接收到的所有属性。
     */
    @Serializable(MessageAttachmentSerializer::class)
    public class Attachment(
        public val url: String,
        public val properties: Map<String, String> = emptyMap()
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Attachment) return false

            if (url != other.url) return false
            if (properties != other.properties) return false

            return true
        }

        override fun hashCode(): Int {
            var result = url.hashCode()
            result = 31 * result + properties.hashCode()
            return result
        }

        override fun toString(): String = "Attachment(url='$url', properties=$properties)"

        //region data-class 兼容
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("url"))
        public operator fun component1(): String = url

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("properties"))
        public operator fun component2(): Map<String, String> = properties

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            url: String = this.url,
            properties: Map<String, String> = this.properties,
        ): Attachment = Attachment(url, properties)
        //endregion
    }


    /**
     * [MessageArk](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageark)
     *
     * @property templateId Ark 模板 ID。
     * @property kv KV 值列表。
     */
    @Serializable
    public class Ark(
        @SerialName("template_id") public val templateId: String,
        public val kv: List<Kv> = emptyList()
    ) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Ark) return false

            if (templateId != other.templateId) return false
            if (kv != other.kv) return false

            return true
        }

        override fun hashCode(): Int {
            var result = templateId.hashCode()
            result = 31 * result + kv.hashCode()
            return result
        }

        override fun toString(): String = "Ark(templateId='$templateId', kv=$kv)"

        //region data-class 兼容
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("templateId"))
        public operator fun component1(): String = templateId

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("kv"))
        public operator fun component2(): List<Kv> = kv

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            templateId: String = this.templateId,
            kv: List<Kv> = this.kv,
        ): Ark = Ark(templateId, kv)
        //endregion

        /**
         * [MessageArkKv](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagearkkv)
         *
         * @property key KV 键。
         * @property value KV 值。
         * @property obj KV 对象列表。
         */
        @Serializable
        public class Kv(
            public val key: String,
            public val value: String? = null,
            public val obj: List<Obj> = emptyList(),
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Kv) return false

                if (key != other.key) return false
                if (value != other.value) return false
                if (obj != other.obj) return false

                return true
            }

            override fun hashCode(): Int {
                var result = key.hashCode()
                result = 31 * result + value.hashCode()
                result = 31 * result + obj.hashCode()
                return result
            }

            override fun toString(): String = "Kv(key='$key', value=$value, obj=$obj)"

            //region data-class 兼容
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("key"))
            public operator fun component1(): String = key

            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("value"))
            public operator fun component2(): String? = value

            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("obj"))
            public operator fun component3(): List<Obj> = obj

            @Suppress("DeprecatedCallableAddReplaceWith")
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
            public fun copy(
                key: String = this.key,
                value: String? = this.value,
                obj: List<Obj> = this.obj,
            ): Kv = Kv(key, value, obj)
            //endregion
        }

        /**
         * [MessageArkObj](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagearkobj)
         *
         * @property objKv Ark object KV 列表。
         */
        @Serializable
        public class Obj(
            @SerialName("obj_kv") public val objKv: List<Kv> = emptyList()
        ) {

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Obj) return false
                return objKv == other.objKv
            }

            override fun hashCode(): Int = objKv.hashCode()

            override fun toString(): String = "Obj(objKv=$objKv)"

            //region data-class 兼容
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("objKv"))
            public operator fun component1(): List<Kv> = objKv

            @Suppress("DeprecatedCallableAddReplaceWith")
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
            public fun copy(objKv: List<Kv> = this.objKv): Obj = Obj(objKv)
            //endregion

            /**
             * [MessageArkObjKv](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagearkobjkv)
             *
             * @property key KV 键。
             * @property value KV 值。
             */
            @Serializable
            public class Kv(public val key: String, public val value: String) {
                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other !is Kv) return false

                    if (key != other.key) return false
                    if (value != other.value) return false

                    return true
                }

                override fun hashCode(): Int {
                    var result = key.hashCode()
                    result = 31 * result + value.hashCode()
                    return result
                }

                override fun toString(): String = "Kv(key='$key', value='$value')"

                //region data-class 兼容
                @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("key"))
                public operator fun component1(): String = key

                @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("value"))
                public operator fun component2(): String = value

                @Suppress("DeprecatedCallableAddReplaceWith")
                @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
                public fun copy(
                    key: String = this.key,
                    value: String = this.value,
                ): Kv = Kv(key, value)
                //endregion
            }
        }


    }

    /**
     * [MessageReference](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagereference)
     *
     * @property messageId 需要引用回复的消息 ID。
     * @property ignoreGetMessageError 是否忽略获取引用消息详情错误。
     */
    @Serializable
    public class Reference(
        @SerialName("message_id") public val messageId: String,
        @SerialName("ignore_get_message_error") public val ignoreGetMessageError: Boolean = false
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Reference) return false

            if (messageId != other.messageId) return false
            if (ignoreGetMessageError != other.ignoreGetMessageError) return false

            return true
        }

        override fun hashCode(): Int {
            var result = messageId.hashCode()
            result = 31 * result + ignoreGetMessageError.hashCode()
            return result
        }

        override fun toString(): String {
            return "Reference(messageId='$messageId', " +
                "ignoreGetMessageError=$ignoreGetMessageError)"
        }

        //region data-class 兼容
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("messageId"))
        public operator fun component1(): String = messageId

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("ignoreGetMessageError"))
        public operator fun component2(): Boolean = ignoreGetMessageError

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            messageId: String = this.messageId,
            ignoreGetMessageError: Boolean = this.ignoreGetMessageError,
        ): Reference = Reference(messageId, ignoreGetMessageError)
        //endregion
    }

    /**
     * [MessageMarkdown](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagemarkdown)
     *
     * @property templateId Markdown 模板 ID。
     * @property customTemplateId Markdown 自定义模板 ID。
     * @property params Markdown 模板参数。
     * @property content 原生 Markdown 内容。
     */
    @Serializable
    public class Markdown(
        @SerialName("template_id") public val templateId: Int? = null,
        @SerialName("custom_template_id") public val customTemplateId: String? = null,
        public val params: List<Param>? = null,
        public val content: String? = null,
    ) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Markdown) return false

            if (templateId != other.templateId) return false
            if (customTemplateId != other.customTemplateId) return false
            if (params != other.params) return false
            if (content != other.content) return false

            return true
        }

        override fun hashCode(): Int {
            var result = templateId ?: 0
            result = 31 * result + customTemplateId.hashCode()
            result = 31 * result + params.hashCode()
            result = 31 * result + content.hashCode()
            return result
        }

        override fun toString(): String {
            return "Markdown(" +
                "templateId=$templateId, " +
                "customTemplateId=$customTemplateId, " +
                "params=$params, " +
                "content=$content)"
        }

        //region data-class 兼容
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("templateId"))
        public operator fun component1(): Int? = templateId

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("customTemplateId"))
        public operator fun component2(): String? = customTemplateId

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("params"))
        public operator fun component3(): List<Param>? = params

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("content"))
        public operator fun component4(): String? = content

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            templateId: Int? = this.templateId,
            customTemplateId: String? = this.customTemplateId,
            params: List<Param>? = this.params,
            content: String? = this.content,
        ): Markdown = Markdown(templateId, customTemplateId, params, content)
        //endregion

        /**
         * [MessageMarkdownParams](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#MessageMarkdownParams)
         *
         * Deprecated: 在 4.1.4 中 [Markdown.params] 被修改以修复原本错误的定义，
         * 并由此提供了更符合语义的 [Param]。
         *
         * @see Param
         * @property key Markdown 模板参数键。
         * @property values Markdown 模板参数值列表。
         */
        @Serializable
        @Deprecated(
            "在 4.1.4 中 [Markdown.params] 被修改以修复原本错误的定义，" +
                "并由此提供了更符合语义的 [Param]。",
            replaceWith = ReplaceWith(
                "Param(key, values)",
                "love.forte.simbot.qguild.model.Message.Markdown.Param"
            ),
        )
        @Suppress("DEPRECATION")
        public class Params(
            public val key: String,
            public val values: List<String>
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Params) return false

                if (key != other.key) return false
                if (values != other.values) return false

                return true
            }

            override fun hashCode(): Int {
                var result = key.hashCode()
                result = 31 * result + values.hashCode()
                return result
            }

            override fun toString(): String = "Params(key='$key', values=$values)"

            //region data-class 兼容
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("key"))
            public operator fun component1(): String = key

            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("values"))
            public operator fun component2(): List<String> = values

            @Suppress("DeprecatedCallableAddReplaceWith")
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
            public fun copy(
                key: String = this.key,
                values: List<String> = this.values,
            ): Params = Params(key, values)
            //endregion
        }

        /**
         * [MessageMarkdownParams](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/type/markdown.html#%E5%8F%91%E9%80%81%E6%96%B9%E5%BC%8F)
         *
         * @property key Markdown 模板参数键。
         * @property values Markdown 模板参数值列表。
         */
        @Serializable
        public class Param(
            public val key: String,
            public val values: List<String>
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Param) return false

                if (key != other.key) return false
                if (values != other.values) return false

                return true
            }

            override fun hashCode(): Int {
                var result = key.hashCode()
                result = 31 * result + values.hashCode()
                return result
            }

            override fun toString(): String = "Param(key='$key', values=$values)"

            //region data-class 兼容
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("key"))
            public operator fun component1(): String = key

            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("values"))
            public operator fun component2(): List<String> = values

            @Suppress("DeprecatedCallableAddReplaceWith")
            @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
            public fun copy(
                key: String = this.key,
                values: List<String> = this.values,
            ): Param = Param(key, values)
            //endregion
        }

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
 *
 * @property nick 用户的昵称。
 * @property roles 用户在频道内的身份组 ID。
 * @property joinedAt 用户加入频道的时间。
 */
@ApiModel
@Serializable
public class MessageMember @ApiModelConstructor constructor(
    override val nick: String = "",
    override val roles: List<String> = emptyList(),
    @SerialName("joined_at")
    override val joinedAt: String = QQ.ZERO_ISO_INSTANT
) : Member {

    @Transient
    @set:JvmSynthetic // hide setter
    override lateinit var user: User
        internal set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageMember) return false

        if (nick != other.nick) return false
        if (roles != other.roles) return false
        if (joinedAt != other.joinedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nick.hashCode()
        result = 31 * result + roles.hashCode()
        result = 31 * result + joinedAt.hashCode()
        return result
    }

    override fun toString(): String = "MessageMember(nick='$nick', roles=$roles, joinedAt='$joinedAt')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("nick"))
    public operator fun component1(): String = nick

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("roles"))
    public operator fun component2(): List<String> = roles

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("joinedAt"))
    public operator fun component3(): String = joinedAt

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        nick: String = this.nick,
        roles: List<String> = this.roles,
        joinedAt: String = this.joinedAt,
    ): MessageMember = MessageMember(nick, roles, joinedAt)
    //endregion

    public companion object {

        /**
         * 将任意 [Member] 类型转化为 [MessageMember].
         * 如果类型本身即为 [MessageMember] 则返回自身，否则得到新的实例。
         */
        @JvmStatic
        public fun Member.asMessageMember(): MessageMember =
            this as? MessageMember ?: MessageMember(nick, roles, joinedAt).also { it.user = user }
    }
}


@OptIn(ExperimentalSerializationApi::class)
internal object MessageEmbedFieldSerializer : KSerializer<Message.Embed.Field> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MessageEmbedField") {
        element<String>("name")
    }

    override fun deserialize(decoder: Decoder): Message.Embed.Field {
        val input = decoder.beginStructure(descriptor)
        var name: String? = null
        while (true) {
            when (val index = input.decodeElementIndex(descriptor)) {
                0 -> name = input.decodeStringElement(descriptor, 0)
                CompositeDecoder.DECODE_DONE -> break
                else -> throw SerializationException("Unknown index $index")
            }
        }
        input.endStructure(descriptor)
        return Message.Embed.Field(name ?: throw MissingFieldException("name", "Message.Embed.Field"))
    }

    override fun serialize(encoder: Encoder, value: Message.Embed.Field) {
        val output = encoder.beginStructure(descriptor)
        output.encodeStringElement(descriptor, 0, value.name)
        output.endStructure(descriptor)
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

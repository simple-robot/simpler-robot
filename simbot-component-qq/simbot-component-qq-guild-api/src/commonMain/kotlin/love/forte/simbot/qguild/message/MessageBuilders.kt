/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

@file:Suppress("MemberVisibilityCanBePrivate")

package love.forte.simbot.qguild.message

import love.forte.simbot.qguild.model.Message
import kotlin.jvm.JvmOverloads


/**
 *
 * 通过 `DSL` 语法构建 [Message.Ark].
 *
 *
 * ```
 * val ark = buildArk("0") {
 *     kvs { // 增加kvs
 *         kv("Key1", "value") // kv
 *         kv("Key2", "value2") {
 *             obj() // empty obj -> { "objKv": [] }
 *             obj {
 *                 kv("objKey1", "valueA")
 *                 kv("objKey2", "valueB")
 *             }
 *         }
 *     }
 *  }
 *
 *
 * ```
 *
 */
public inline fun buildArk(templateId: String, builder: ArkBuilder.() -> Unit): Message.Ark {
    return ArkBuilder(templateId).also(builder).build()
}

/**
 * 根据模板ID构建一个 [Message.Ark]
 */
public fun buildArk(templateId: String): Message.Ark {
    return Message.Ark(templateId)
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class ArkBuilderDSL


@ArkBuilderDSL
public class ArkBuilder(public var templateId: String) {

    public fun from(ark: Message.Ark): ArkBuilder = also {
        templateId = ark.templateId
        kvs = ark.kv.toMutableList()
    }

    @ArkBuilderDSL
    public var kvs: MutableList<Message.Ark.Kv> = mutableListOf()

    public fun kv(key: String, value: String): ArkBuilder = also {
        kvs.add(Message.Ark.Kv(key, value))
    }

    @ArkBuilderDSL
    public fun kvs(block: ArkKvListBuilder.() -> Unit): ArkBuilder = also {
        kvs.addAll(ArkKvListBuilder().also(block).build())
    }


    public fun build(): Message.Ark {
        return Message.Ark(templateId, kvs.toList())
    }
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class ArkKvBuilderDSL


@ArkKvBuilderDSL
public class ArkKvListBuilder {
    @ArkKvBuilderDSL
    public var arkKvs: MutableList<Message.Ark.Kv> = mutableListOf()

    @ArkKvBuilderDSL
    public fun kv(key: String, value: String): ArkKvListBuilder = also {
        arkKvs.add(Message.Ark.Kv(key, value))
    }

    @ArkKvBuilderDSL
    @JvmOverloads
    public fun kv(key: String, value: String? = null, build: ArkObjListBuilder.() -> Unit): ArkKvListBuilder = also {
        arkKvs.add(Message.Ark.Kv(key, value, ArkObjListBuilder().also(build).build()))
    }

    public fun build(): List<Message.Ark.Kv> = arkKvs.toList()
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class ArkObjBuilderDSL


@ArkObjBuilderDSL
public class ArkObjListBuilder {
    @ArkObjBuilderDSL
    public var objList: MutableList<Message.Ark.Obj> = mutableListOf()

    @ArkObjBuilderDSL
    public fun obj(): ArkObjListBuilder = also {
        objList.add(Message.Ark.Obj())
    }

    @ArkObjBuilderDSL
    public fun obj(block: ArkObjKvListBuilder.() -> Unit): ArkObjListBuilder = also {
        objList.add(Message.Ark.Obj(ArkObjKvListBuilder().also(block).build()))
    }

    public fun build(): List<Message.Ark.Obj> = objList.toList()
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class ArkObjKvBuilderDSL


@ArkObjKvBuilderDSL
public class ArkObjKvListBuilder {

    @ArkObjKvBuilderDSL
    public var objKvs: MutableList<Message.Ark.Obj.Kv> = mutableListOf()

    @ArkObjKvBuilderDSL
    public fun kv(key: String, value: String): ArkObjKvListBuilder = also {
        objKvs.add(Message.Ark.Obj.Kv(key, value))
    }

    public fun build(): List<Message.Ark.Obj.Kv> = objKvs.toList()
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class EmbedBuilderDsl

@EmbedBuilderDsl
public class EmbedBuilder {
    /**
     * 标题
     */
    public lateinit var title: String

    /**
     * 消息弹窗内容
     */
    public lateinit var prompt: String

    /**
     * 缩略图
     *
     * 选填，没有缩略图的可以不填
     */
    public var thumbnail: Message.Embed.Thumbnail? = null

    /**
     * 设置缩略图。
     *
     * @see thumbnail
     */
    public var thumbnailUrl: String?
        get() = thumbnail?.url
        set(value) {
            thumbnail = if (value == null) {
                null
            } else {
                Message.Embed.Thumbnail(value)
            }
        }

    /**
     * MessageEmbedField 对象数组	字段信息
     */
    public var fields: MutableList<Message.Embed.Field> = mutableListOf()

    /**
     * 向 [fields] 中添加一个元素。
     *
     */
    public fun addField(name: String) {
        fields.add(Message.Embed.Field(name))
    }

    /**
     * 构建得到 [Message.Embed]
     */
    public fun build(): Message.Embed {
        return Message.Embed(title, prompt, thumbnail, fields.toList())
    }
}



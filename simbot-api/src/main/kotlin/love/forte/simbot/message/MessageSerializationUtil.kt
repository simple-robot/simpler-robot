/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.message

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import love.forte.simbot.Api4J
import love.forte.simbot.ComponentAutoRegistrarFactory
import love.forte.simbot.event.EventProcessingContext
import love.forte.simbot.utils.runInNoScopeBlocking
import org.slf4j.LoggerFactory
import java.util.*

private val logger = LoggerFactory.getLogger("love.forte.simbot.message.MessageSerializationUtil")


/**
 *
 * 服务于Java对 [Messages] 进行序列化的工具类。
 *
 *
 */
@Api4J
public object MessageSerializationUtil {
    
    /**
     * 默认的 [Json] 实例。会在首次获取的时候尝试加载当前环境中所有可用组件中的序列化模块。
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public val defaultJson: Json by lazy {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
            serializersModule = runInNoScopeBlocking { tryFindAllMessageSerializersModule() }
        }
    }
    
    private val defaultJsonBuilder: JsonBuilder4J = JsonBuilder4J {}
    
    /**
     * 操作提供的 [JsonBuilder] 构建一个 `kotlinx-serialization-json` 中的 [Json] 对象。
     *
     * e.g.
     * ```java
     * MessageSerializationUtil.createJson(Json.Default, builder -> {
     *     builder.setLenient(true);
     *     // or other...
     * });
     * ```
     *
     */
    @JvmStatic
    @JvmOverloads
    public fun createJson(
        from: Json? = null,
        builder: JsonBuilder4J = defaultJsonBuilder,
        eventProcessingContext: EventProcessingContext? = null,
    ): Json {
        return Json(from = from ?: Json.Default) {
            eventProcessingContext?.also { context ->
                serializersModule += context.messagesSerializersModule
            }
            builder(this)
        }
    }
    
    /**
     * 操作提供的 [JsonBuilder] 构建一个 `kotlinx-serialization-json` 中的 [Json] 对象。
     *
     * e.g.
     * ```java
     * MessageSerializationUtil.createJson(Json.Default, builder -> {
     *     builder.setLenient(true);
     *     // or other...
     * });
     * ```
     *
     */
    @JvmStatic
    @JvmOverloads
    public fun createJson(
        from: Json? = null,
        builder: JsonBuilder4J = defaultJsonBuilder,
        vararg serializersModules: SerializersModule,
    ): Json {
        return Json(from = from ?: Json.Default) {
            if (serializersModules.isNotEmpty()) {
                val currentSerializerModule = serializersModule
                serializersModule = SerializersModule {
                    include(currentSerializerModule)
                    serializersModules.forEach { module ->
                        include(module)
                    }
                }
            }
            builder(this)
        }
    }
    
    /**
     * 直接通过 [Json] 对 [Messages] 进行序列化。
     *
     * 相当于 `json.encodeToString(Messages.serializer, messages)`。
     *
     * 需要确保使用的 [json] 中存在当前 [Messages] 中的所有消息类型。
     *
     */
    @JvmStatic
    @JvmOverloads
    public fun toJsonString(messages: Messages, json: Json = defaultJson): String {
        return json.encodeToString(Messages.serializer, messages)
    }
    
    /**
     * 直接通过 [Json] 对json字符串进行反序列化。
     *
     * 相当于 `json.decodeFromString(Messages.serializer, jsonString)`。
     *
     * 需要确保使用的 [json] 中存在当前 [Messages] 中的所有消息类型。
     *
     */
    @JvmStatic
    @JvmOverloads
    public fun fromJsonString(jsonString: String, json: Json = defaultJson): Messages {
        return json.decodeFromString(Messages.serializer, jsonString)
    }
    
    
}

/**
 * ```java
 * builder -> { /* build json */ }
 * ```
 */
@Api4J
public fun interface JsonBuilder4J {
    public operator fun invoke(builder: JsonBuilder)
}


private suspend fun tryFindAllMessageSerializersModule(): SerializersModule {
    
    logger.debug("Try find all message serializers module...")
    val componentSerializers = runCatching {
        ServiceLoader.load(ComponentAutoRegistrarFactory::class.java).map {
            val component = it.registrar.create { }
            component.componentSerializersModule.also { module ->
                logger.debug("Load serializers module [{}] from component {}", module, component)
            }
        }
    }.getOrElse { emptyList() }
    
    return SerializersModule {
        include(Messages.serializersModule)
        componentSerializers.forEach {
            include(it)
        }
    }
}

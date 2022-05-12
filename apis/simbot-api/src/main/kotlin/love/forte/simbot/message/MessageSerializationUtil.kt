/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.message

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import love.forte.simbot.Api4J
import love.forte.simbot.ComponentAutoRegistrarFactory
import love.forte.simbot.event.EventProcessingContext
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
            serializersModule = tryFindAllMessageSerializersModule()
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
        eventProcessingContext: EventProcessingContext? = null
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
        vararg serializersModules: SerializersModule
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


private fun tryFindAllMessageSerializersModule(): SerializersModule {

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
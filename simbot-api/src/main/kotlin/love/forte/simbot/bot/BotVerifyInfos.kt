/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("BotVerifyInfos")

package love.forte.simbot.bot

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.NoSuchComponentException
import love.forte.simbot.resources.Resource
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.inputStream


/**
 *
 * [BotVerifyInfo] 的基础抽象类, 使用 [BotVerifyInfoDecoder] 作为内置解码器。
 *
 */
public abstract class DecoderBotVerifyInfo : BotVerifyInfo {
    protected abstract val decoder: BotVerifyInfoDecoder
    
    /**
     * 已经初始化了的组件id信息。
     *
     * 此属性只会被使用一次。
     */
    protected open val initializedComponentId: String? get() = null
    
    /**
     * 获取此验证信息的组件id。默认情况下优先尝试通过 [initializedComponentId] 初始化信息，
     * 如果 [initializedComponentId] 值为null, 则尝试通过 [decoder.decodeComponentId(...)][BotVerifyInfoDecoder.decodeComponentId]
     * 来初始化组件id信息。
     */
    override val componentId: String by lazy {
        initializedComponentId
            ?: inputStream().use { inp -> decoder.decodeComponentId(inp) }
            ?: throw NoSuchComponentException("required property 'component' cannot be found in current verify info $name")
    }
    
    override fun <T> decode(deserializer: DeserializationStrategy<T>): T {
        return inputStream().use { inp -> decoder.decode(inp, deserializer) }
    }
    
}


private class ResourceAsBotVerifyInfo(override val decoder: BotVerifyInfoDecoder, private val resource: Resource) :
    DecoderBotVerifyInfo(), Resource by resource {
    
    override fun inputStream(): InputStream {
        return openStream()
    }
    
    override fun openStream(): InputStream {
        return resource.openStream()
    }
}


private class URLBotVerifyInfo(
    override val decoder: BotVerifyInfoDecoder,
    private val url: URL,
) : DecoderBotVerifyInfo() {
    override val name: String get() = url.toString()
    
    override fun inputStream(): InputStream = url.openStream()
    
    override fun close() {
        // close nothing.
    }
}


private class PathBotVerifyInfo(
    override val decoder: BotVerifyInfoDecoder,
    private val path: Path,
) : DecoderBotVerifyInfo() {
    override val name: String get() = path.toString()
    
    override fun inputStream(): InputStream = path.inputStream()
    
    
    override fun close() {
        // close nothing.
    }
}

/**
 * 将 [ByteArray] 内容作为 [BotVerifyInfo] 实现。
 *
 */
public class ByteArrayBotVerifyInfo(
    override val decoder: BotVerifyInfoDecoder,
    override val name: String,
    private val value: ByteArray,
) : DecoderBotVerifyInfo() {
    
    override fun inputStream(): InputStream {
        return value.inputStream()
    }
    
    
    override fun close() {
        // close nothing.
    }
    
    override fun <T> decode(deserializer: DeserializationStrategy<T>): T {
        if (decoder is StandardBinaryFormatBotVerifyInfoDecoder) {
            return decoder.decode(value, deserializer)
        }
        
        if (decoder is StandardStringFormatBotVerifyInfoDecoder) {
            return decoder.decode(value.toString(Charsets.UTF_8), deserializer)
        }
        
        return super.decode(deserializer)
    }
}


/**
 * 将一个 [Resource] 转为 [BotVerifyInfo].
 */
public fun Resource.toBotVerifyInfo(decoder: BotVerifyInfoDecoder): BotVerifyInfo {
    return ResourceAsBotVerifyInfo(decoder, this)
}


/**
 * 将一个 [URL] 转为 [BotVerifyInfo].
 *
 */
public fun URL.toBotVerifyInfo(decoder: BotVerifyInfoDecoder): BotVerifyInfo {
    return URLBotVerifyInfo(decoder, this)
}

/**
 * 将一个 [Path] 转为 [BotVerifyInfo].
 */
public fun Path.toBotVerifyInfo(decoder: BotVerifyInfoDecoder): BotVerifyInfo {
    return PathBotVerifyInfo(decoder, this)
}

/**
 * 将一个 [InputStream] 转为 [BotVerifyInfo].
 *
 * @param infoName 给 bot verify info 提供一个名称。
 */
public fun InputStream.toBotVerifyInfo(decoder: BotVerifyInfoDecoder, infoName: String): ByteArrayBotVerifyInfo {
    val bytes = readBytes()
    return ByteArrayBotVerifyInfo(decoder, infoName, bytes)
}

/**
 * 将一个 [URL] 转为 [BotVerifyInfo].
 *
 * e.g.
 * ```kotlin
 * // 解析json配置
 *
 * url: URL = ...
 *
 * url.toBotVerifyInfo(StandardBotVerifyInfoDecoderFactory.Json) {
 *   isLenient = true
 *   ignoreUnknownKeys = true
 * }
 * ```
 */
@JvmOverloads
public fun <C : Any> URL.toBotVerifyInfo(
    factory: BotVerifyInfoDecoderFactory<C, *>,
    configurator: C.() -> Unit = {},
): BotVerifyInfo {
    return toBotVerifyInfo(factory.create(configurator))
}


/**
 * 将一个 [Path] 转为 [BotVerifyInfo].
 * e.g.
 * ```kotlin
 * // 解析json配置
 * path: Path = ...
 * path.toBotVerifyInfo(StandardBotVerifyInfoDecoderFactory.Json) {
 *   isLenient = true
 *   ignoreUnknownKeys = true
 * }
 * ```
 */
@JvmOverloads
public fun <C : Any> Path.toBotVerifyInfo(
    factory: BotVerifyInfoDecoderFactory<C, *>,
    configurator: C.() -> Unit = {},
): BotVerifyInfo {
    return toBotVerifyInfo(factory.create(configurator))
}


/**
 * 将一个 [InputStream] 转为 [BotVerifyInfo].
 * e.g.
 * ```kotlin
 * // 解析json配置
 * input: InputStream = ...
 * val infoName: String = "myBot.json"
 * input.toBotVerifyInfo(StandardBotVerifyInfoDecoderFactory.Json, infoName) {
 *   isLenient = true
 *   ignoreUnknownKeys = true
 * }
 * ```
 */
@JvmOverloads
public fun <C : Any> InputStream.toBotVerifyInfo(
    factory: BotVerifyInfoDecoderFactory<C, *>,
    infoName: String,
    configurator: C.() -> Unit = {},
): BotVerifyInfo {
    return toBotVerifyInfo(factory.create(configurator), infoName)
}





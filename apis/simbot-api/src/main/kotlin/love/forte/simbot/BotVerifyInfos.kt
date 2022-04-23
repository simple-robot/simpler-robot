/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

@file:JvmName("BotVerifyInfos")

package love.forte.simbot

import kotlinx.serialization.DeserializationStrategy
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
            ?: throw NoSuchComponentException("required property 'component' cannot be found in current verify info $infoName")
    }

    override fun <T> decode(deserializer: DeserializationStrategy<T>): T {
        return inputStream().use { inp -> decoder.decode(inp, deserializer) }
    }

}


private class URLBotVerifyInfo(
    override val decoder: BotVerifyInfoDecoder,
    private val url: URL,
) : DecoderBotVerifyInfo() {
    override val infoName: String get() = url.toString()

    override fun inputStream(): InputStream = url.openStream()
}


private class PathBotVerifyInfo(
    override val decoder: BotVerifyInfoDecoder,
    private val path: Path,
) : DecoderBotVerifyInfo() {
    override val infoName: String get() = path.toString()

    override fun inputStream(): InputStream = path.inputStream()
}

/**
 * 将 [ByteArray] 内容作为 [BotVerifyInfo] 实现。
 *
 */
public class ByteArrayBotVerifyInfo(
    override val decoder: BotVerifyInfoDecoder,
    override val infoName: String,
    private val value: ByteArray,
) : DecoderBotVerifyInfo() {

    override fun inputStream(): InputStream {
        return value.inputStream()
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





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

package love.forte.simbot.bot

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.properties.Properties
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.LoggerFactory
import love.forte.simbot.resources.DeserializableResource
import love.forte.simbot.resources.DeserializableResourceDecoder
import love.forte.simbot.resources.SerialFormatDeserializableResourceDecoder
import org.slf4j.Logger
import java.io.IOException
import java.io.InputStream
import java.util.Properties as JavaProperties

/**
 * 用于快速从配置信息中解析出来 `component` 信息的模型。
 *
 * 使用的时候需要注意确保所使用的解析器能够忽略未知或冗余属性。
 */
@Serializable
public data class ComponentModel(val component: String? = null)


/**
 * BOT用于验证身份的信息，通过读取 `*.bot` 文件解析而来.
 *
 * 目前实际支持的解析格式可参考 [StandardBotVerifyInfoDecoderFactory] 下的所有实现。
 *
 * ## 构建
 * 来构建一个 [BotVerifyInfo] 可以通过:
 * - [java.net.URL.toBotVerifyInfo]
 * - [java.nio.file.Path.toBotVerifyInfo]
 * - [InputStream.toBotVerifyInfo]
 * - [ByteArrayBotVerifyInfo]
 * - 自行实现 [BotVerifyInfo]
 *
 * ```kotlin
 * val path: Path("my-bot.bot.json")
 * val info = path.toBotVerifyInfo(StandardBotVerifyInfoDecoderFactory.Json)
 * ```
 *
 * 在 Java 中，上述的 `toBotVerifyInfo` 方法存在于 `BotVerifyInfos` 类中。
 *
 * ```java
 * final Path path = Paths.get("my-bot.bot.json");
 * final BotVerifyInfo info = BotVerifyInfos.toBotVerifyInfo(path, StandardBotVerifyInfoDecoderFactory.json());
 * ```
 *
 *
 *
 */
public interface BotVerifyInfo : DeserializableResource {

    public companion object {

        /**
         * 在所有的配置文件中, 'component' 所应当代表的属性名。
         *
         * @see ComponentModel
         */
        public const val COMPONENT_PROPERTIES_KEY: String = "component"

    }

    /**
     * 此验证信息中的组件信息。
     */
    public val componentId: String


    /**
     * 获取此资源的名称，一般代表其文件名。
     */
    override val name: String

    /**
     * 读取其输入流.
     */
    @Throws(IOException::class)
    public fun inputStream(): InputStream


    /**
     * 读取其输入流. 同 [inputStream].
     */
    @Throws(IOException::class)
    override fun openStream(): InputStream {
        return inputStream()
    }

    /**
     * 提供一个 [DeserializationStrategy], 将当前验证信息解码为目标类型。
     */
    override fun <T> decode(deserializer: DeserializationStrategy<T>): T


}


/**
 * [BotVerifyInfoDecoder] 的构建工厂。
 *
 * 对此类的实现尽可能以伴生或单例的形式实现，在使用的时候会直接以此类实例作为唯一标识使用。
 *
 */
public interface BotVerifyInfoDecoderFactory<C : Any, D : BotVerifyInfoDecoder> {

    /**
     * 根据提供的名称验证是否符合此工厂目标decoder的要求。
     */
    public fun match(verificationInfoName: String): Boolean

    /**
     * 提供配置，并构建一个目标解码器。
     */
    public fun create(configurator: C.() -> Unit = {}): D

}

/**
 * bot验证信息的解码器。
 *
 * 默认提供的实现可以参考 [StandardBotVerifyInfoDecoderFactory] 的实现工厂。
 *
 * @see StandardBotVerifyInfoDecoderFactory
 */
public interface BotVerifyInfoDecoder : DeserializableResourceDecoder {

    /**
     * 尝试从提供的数据信息中解析得到当前配置中的组件信息。
     *
     * @param inputStream 提供的数据输入流。应当由调用者关闭。
     */
    public fun decodeComponentId(inputStream: InputStream): String?

    /**
     * 提供数据输入流和[deserializer], 解析为目标类型。
     *
     * @param inputStream 提供的数据输入流。应当由调用者关闭。
     */
    override fun <T> decode(inputStream: InputStream, deserializer: DeserializationStrategy<T>): T

}


/**
 * 基于 [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) 库中的部分模块的 [BotVerifyInfoDecoderFactory] 标准实现。
 *
 * @see JsonBotVerifyInfoDecoder.Factory
 * @see YamlBotVerifyInfoDecoder.Factory
 * @see PropertiesBotVerifyInfoDecoder.Factory
 *
 */
public sealed class StandardBotVerifyInfoDecoderFactory<C : Any, D : BotVerifyInfoDecoder> :
    BotVerifyInfoDecoderFactory<C, D> {

    public companion object {
        private val logger: Logger = LoggerFactory.getLogger("love.forte.simbot.bot.StandardBotVerifyInfoDecoderFactory")

        /**
         * 支持`Json`格式的bot配置文件。
         *
         * @see JsonBotVerifyInfoDecoder
         */
        @JvmStatic
        @get:JvmName("json")
        public val Json: JsonBotVerifyInfoDecoder.Factory get() = JsonBotVerifyInfoDecoder

        /**
         * 支持`Yaml`格式的bot配置文件。
         *
         * @see YamlBotVerifyInfoDecoder
         */
        @JvmStatic
        @get:JvmName("yaml")
        public val Yaml: YamlBotVerifyInfoDecoder.Factory get() = YamlBotVerifyInfoDecoder

        /**
         * 支持`Properties`格式的bot配置文件。
         *
         * @see PropertiesBotVerifyInfoDecoder
         */
        @ExperimentalSerializationApi
        @JvmStatic
        @get:JvmName("properties")
        public val Properties: PropertiesBotVerifyInfoDecoder.Factory get() = PropertiesBotVerifyInfoDecoder

        /**
         * 尝试获取当前环境下所支持的所有解析工厂。
         *
         * @param classLoader 用于检测环境的 ClassLoader.
         * @param warnLogger 当存在不支持的解析器的时候, 会使用提供的 logger 输出警告日志。
         */
        @JvmStatic
        @JvmOverloads
        @ExperimentalSimbotApi
        @ExperimentalSerializationApi
        public fun supportDecoderFactories(
            warnLogger: Logger? = logger,
            classLoader: ClassLoader = Companion::class.java.classLoader,
        ): List<StandardBotVerifyInfoDecoderFactory<*, *>> {
            return buildList {
                if (checkJson(warnLogger, classLoader)) {
                    add(Json)
                }

                if (checkYaml(warnLogger, classLoader)) {
                    add(Yaml)
                }

                if (checkProperties(warnLogger, classLoader)) {
                    add(Properties)
                }
            }
        }


        private fun checkJson(warnLogger: Logger?, classLoader: ClassLoader): Boolean {
            return kotlin.runCatching {
                classLoader.loadClass("kotlinx.serialization.json.Json")
                true
            }.getOrElse {
                warnLogger?.warn("Unable to find the kotlinx-serialization-json in current classpath, the bot configuration parser in *.bot(.json) format will not be available.")
                false
            }
        }

        private fun checkYaml(warnLogger: Logger?, classLoader: ClassLoader): Boolean {
            return kotlin.runCatching {
                classLoader.loadClass("com.charleskorn.kaml.Yaml")
                true
            }.getOrElse {
                warnLogger?.warn("Unable to find the com.charleskorn.kaml:kaml in current classpath, the bot configuration parser in *.bot.yaml format will not be available.")
                false
            }
        }

        private fun checkProperties(warnLogger: Logger?, classLoader: ClassLoader): Boolean {
            return kotlin.runCatching {
                classLoader.loadClass("kotlinx.serialization.properties.Properties")
                true
            }.getOrElse {
                warnLogger?.warn("Unable to find the kotlinx-serialization-properties in current classpath, the bot configuration parser in *.bot.properties format will not be available.")
                false
            }
        }


    }

}

private fun regexMatcher(regex: Regex): (String) -> Boolean = regex::matches


/**
 * 基于 [SerialFormat] 的标准解码器抽象。
 */
public abstract class StandardSerialFormatBotVerifyInfoDecoder<F : SerialFormat, V : Any> internal constructor() :
    BotVerifyInfoDecoder, SerialFormatDeserializableResourceDecoder() {

    /**
     * 用于进行常规解码的解码器。
     */
    public abstract override val format: F


    /**
     * 解码。
     */
    public abstract fun <T> decode(decoder: F, value: V, deserializer: DeserializationStrategy<T>): T


    /**
     * 解码。
     */
    public fun <T> decode(value: V, deserializer: DeserializationStrategy<T>): T = decode(format, value, deserializer)

    /**
     * 将 [inputStream] 准备为目标结果类型。
     */
    protected abstract fun InputStream.prepareToValue(): V


    /**
     * 解码目标类型。
     */
    override fun <T> decode(inputStream: InputStream, deserializer: DeserializationStrategy<T>): T {
        val value = inputStream.prepareToValue()
        return decode(format, value, deserializer)
    }
}

/**
 * 基于 [StringFormat] 的标准解码器抽象。
 */
public abstract class StandardStringFormatBotVerifyInfoDecoder internal constructor() :
    StandardSerialFormatBotVerifyInfoDecoder<StringFormat, String>() {

    /**
     * 用于解析 [ComponentModel] 的解码器。通常应该提供一个具有较为宽松的规则的解码器。
     */
    protected abstract val modelDecoder: StringFormat


    override fun InputStream.prepareToValue(): String {
        return trimText()
    }

    override fun <T> decode(decoder: StringFormat, value: String, deserializer: DeserializationStrategy<T>): T {
        return decoder.decodeFromString(deserializer, value)
    }

    override fun decodeComponentId(inputStream: InputStream): String? {
        val value = inputStream.prepareToValue().takeIf { it.isNotEmpty() } ?: return null
        val model = decode(modelDecoder, value, ComponentModel.serializer())
        return model.component
    }
}

/**
 * 基于 [BinaryFormat] 的标准解码器抽象。
 */
public abstract class StandardBinaryFormatBotVerifyInfoDecoder internal constructor() :
    StandardSerialFormatBotVerifyInfoDecoder<BinaryFormat, ByteArray>() {

    /**
     * 用于解析 [ComponentModel] 的解码器。通常应该提供一个具有较为宽松的规则的解码器。
     */
    protected abstract val modelDecoder: BinaryFormat


    override fun InputStream.prepareToValue(): ByteArray {
        return this.readBytes()
    }

    override fun <T> decode(decoder: BinaryFormat, value: ByteArray, deserializer: DeserializationStrategy<T>): T {
        return decoder.decodeFromByteArray(deserializer, value)
    }

    override fun decodeComponentId(inputStream: InputStream): String? {
        val value = inputStream.prepareToValue().takeIf { it.isNotEmpty() } ?: return null
        val model = decode(modelDecoder, value, ComponentModel.serializer())
        return model.component
    }
}


private fun InputStream.trimText(): String = reader().readText().trim()


/**
 * 使用 [Json] decode 验证信息。
 *
 * 支持解析Json格式的bot配置文件。格式允许 `*.bot` 或 `*.bot.json`。
 *
 * Note: 需要保证环境中存在 `org.jetbrains.kotlinx:kotlinx-serialization-json`，参考 [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)。
 *
 */
public class JsonBotVerifyInfoDecoder(override val format: Json) :
    StandardStringFormatBotVerifyInfoDecoder() {
    override val modelDecoder: StringFormat = Json(from = format) {
        isLenient = true
        ignoreUnknownKeys = true
    }


    /**
     * [JsonBotVerifyInfoDecoder] 的工厂.
     */
    public companion object Factory : StandardBotVerifyInfoDecoderFactory<JsonBuilder, JsonBotVerifyInfoDecoder>() {
        private val matcher = regexMatcher(Regex(".+\\.bot(\\.json)?"))
        override fun match(verificationInfoName: String): Boolean {
            return matcher(verificationInfoName)
        }

        override fun create(configurator: JsonBuilder.() -> Unit): JsonBotVerifyInfoDecoder {
            return JsonBotVerifyInfoDecoder(Json {
                isLenient = true
                ignoreUnknownKeys = true
                configurator()
            })
        }

        public fun create(decoder: Json): JsonBotVerifyInfoDecoder {
            return JsonBotVerifyInfoDecoder(decoder)
        }
    }
}

/**
 * 使用 [Yaml] decode 验证信息。
 *
 * 支持解析 [Yaml](https://yaml.org) 格式的bot配置文件。格式允许 `*.bot.yaml` 或 `*.bot.yml`。
 *
 * Note: 需要保证环境中存在 `com.charleskorn.kaml:kaml`, 参考 [charleskorn/kaml](https://github.com/charleskorn/kaml)
 *
 */
public class YamlBotVerifyInfoDecoder(override val format: Yaml) :
    StandardStringFormatBotVerifyInfoDecoder() {
    override val modelDecoder: StringFormat =
        Yaml(format.serializersModule, format.configuration.copy(strictMode = false))

    public companion object Factory :
        StandardBotVerifyInfoDecoderFactory<YamlBotVerifyInfoDecoderConfiguration, YamlBotVerifyInfoDecoder>() {
        private val matcher = regexMatcher(Regex(".+\\.bot\\.ya?ml"))
        override fun match(verificationInfoName: String): Boolean {
            return matcher(verificationInfoName)
        }

        override fun create(configurator: YamlBotVerifyInfoDecoderConfiguration.() -> Unit): YamlBotVerifyInfoDecoder {
            val configuration = YamlBotVerifyInfoDecoderConfiguration().also(configurator)
            return YamlBotVerifyInfoDecoder(
                Yaml(
                    configuration.serializersModule,
                    configuration.createYamlConfiguration()
                )
            )
        }

        public fun create(decoder: Yaml): YamlBotVerifyInfoDecoder {
            return YamlBotVerifyInfoDecoder(decoder)
        }
    }

    /**
     * 用于 [YamlBotVerifyInfoDecoder.Factory] 的配置类。
     */
    public open class YamlBotVerifyInfoDecoderConfiguration {
        /**
         * 配置 [SerializersModule].
         */
        public var serializersModule: SerializersModule = SerializersModule {}

        private var configuration: YamlConfiguration? = null

        public fun config(newConfiguration: YamlConfiguration) {
            configuration = newConfiguration
        }

        internal fun createYamlConfiguration(): YamlConfiguration {
            return configuration ?: YamlConfiguration(strictMode = false)
        }

    }
}


/**
 * 使用 [kotlinx.serialization.properties.Properties] decode 验证信息。
 *
 * 支持解析 kotlinx.properties 格式的bot配置文件。格式允许 `*.bot.properties`。
 *
 * Note: 需要保证环境中存在 `org.jetbrains.kotlinx:kotlinx-serialization-properties`，参考 [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)。
 *
 */
@ExperimentalSerializationApi
public class PropertiesBotVerifyInfoDecoder(override val format: Properties) :
    StandardSerialFormatBotVerifyInfoDecoder<Properties, Map<String, String>>() {

    override fun decodeComponentId(inputStream: InputStream): String? {
        val javaProperties = inputStream.prepareToJavaProperties()
        return javaProperties.getProperty(BotVerifyInfo.COMPONENT_PROPERTIES_KEY)
    }

    override fun <T> decode(
        decoder: Properties,
        value: Map<String, String>,
        deserializer: DeserializationStrategy<T>,
    ): T {
        return decoder.decodeFromStringMap(deserializer, value)
    }

    private fun InputStream.prepareToJavaProperties(): JavaProperties {
        return JavaProperties().also {
            it.load(reader())
        }
    }

    override fun InputStream.prepareToValue(): Map<String, String> {
        return prepareToJavaProperties().toStringMap()
    }


    /**
     * [PropertiesBotVerifyInfoDecoder] 的工厂。
     */
    public companion object Factory :
        StandardBotVerifyInfoDecoderFactory<PropertiesConfiguration, PropertiesBotVerifyInfoDecoder>() {
        private val matcher = regexMatcher(Regex(".+\\.bot\\.properties"))
        private fun JavaProperties.toStringMap(): Map<String, String> {
            val stringMap = mutableMapOf<String, String>()
            stringPropertyNames().forEach { name ->
                stringMap[name] = getProperty(name)
            }
            return stringMap
        }


        override fun match(verificationInfoName: String): Boolean {
            return matcher(verificationInfoName)
        }

        override fun create(configurator: PropertiesConfiguration.() -> Unit): PropertiesBotVerifyInfoDecoder {
            return PropertiesBotVerifyInfoDecoder(Properties(PropertiesConfiguration().also(configurator).serializersModule))
        }
    }

    /**
     * 服务于 [PropertiesBotVerifyInfoDecoder.Factory] 的配置类。
     */
    public open class PropertiesConfiguration {
        public var serializersModule: SerializersModule = SerializersModule {}
    }
}



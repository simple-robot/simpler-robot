/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.bot

import love.forte.common.utils.scanner.ResourcesScanner
import love.forte.simbot.bot.BotVerifyInfoConfiguration.Companion.ACTION_NAME_KEY
import org.slf4j.LoggerFactory
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import kotlin.io.path.*


/**
 *
 * bot验证信息配置管理器。
 *
 * 是一个配置类接口，其内部记录本次启动所读取到的所有 **配置** 的bot验证信息。
 * 其数据是静态的，在启动的时候读取所有配置记录后不再更新，因此与后续追加、移除的bot信息无关。
 *
 * 其中包括了 `simbot.core.bots` 中的配置以及 `simbot.core.bot-resource-type` 中指定类型的配置。
 *
 * `simbot.core.bot-resource-type` 指定扫描模式后，[BotVerifyInfoConfiguration] 会去扫描 `simbot-bots` 目录下的所有 `.bot` 文件。
 * `.bot` 文件是一种 properties 格式文件，其中记录bot的各项信息。
 *
 *
 * @author ForteScarlet
 * @since 2.1.0
 */
public interface BotVerifyInfoConfiguration {

    companion object {
        const val PATH_DIR = "simbot-bots"
        const val ACTION_NAME_KEY = "action_name"
    }

    /**
     * Bot资源获取的类型。
     */
    val botResourceType: BotResourceType

    /**
     * 已配置的bot信息列表。可能为空。
     */
    val configuredBotVerifyInfos: List<BotVerifyInfo>

}


/**
 * 基础的 [BotVerifyInfoConfiguration] 配置类实现。
 *
 * 通过 action_name 来决定是否被加载。
 *
 */
public class SimpleBotVerifyInfoConfiguration(
    override val botResourceType: BotResourceType,
    actionBots: List<String> = listOf(ALL_ACTION_KEY),
    codeAlias: Array<String> = CODE_ALIAS,
    verificationAlias: Array<String> = VERIFICATION_ALIAS,
    other: List<BotVerifyInfo> = emptyList(),
) : BotVerifyInfoConfiguration {

    private companion object {
        private val LOGGER = LoggerFactory.getLogger(BotVerifyInfoConfiguration::class.java)
        private const val ALL_ACTION_KEY = "*"
    }

    override val configuredBotVerifyInfos: List<BotVerifyInfo>

    init {
        fun fromFile(): List<Properties> {
            val root = Path(BotVerifyInfoConfiguration.PATH_DIR)
            val collection = mutableListOf<Properties>()

            if (!root.exists()) {
                LOGGER.debug("Cannot read bots configure by file: The directory '${BotVerifyInfoConfiguration.PATH_DIR}' does not exist, skip.")
                return emptyList()
            }

            Files.walkFileTree(root, FileVisitorByExtension("bot", collection) { p ->
                LOGGER.debug("Bot verify info by {}", p.name)
                Properties().apply {
                    p.reader(Charsets.UTF_8).use(::load)
                }
            })

            return collection
        }

        fun fromResource(): List<Properties> {
            return runCatching {
                val loader = Thread.currentThread().contextClassLoader
                ResourcesScanner(loader).scan(BotVerifyInfoConfiguration.PATH_DIR) { uri ->
                    uri.toASCIIString().endsWith("bot")
                }.collection.map { uri ->
                    LOGGER.debug("Bot verify info by {}", uri.toASCIIString())
                    Properties().apply {
                        val asciiString = uri.toASCIIString()
                        loader.getResourceAsStream(asciiString)?.reader(Charsets.UTF_8)?.use(::load)
                            ?: kotlin.runCatching {
                                LOGGER.debug("Uri stream null: {}, try use url", asciiString)
                            uri.toURL().openStream().reader(Charsets.UTF_8).use(::load)
                        }.getOrElse { e1 ->
                            throw IllegalStateException("Uri stream read failed: $uri", e1)
                        }


                    }
                }
            }.getOrElse { e ->
                LOGGER.debug("Cannot read bots configure by resource, skip. info : {}", e.localizedMessage)
                LOGGER.debug("Details: $e", e)
                emptyList()
            }
        }


        LOGGER.debug("Bot resource Type: {}", botResourceType)

        val propertiesList = when (botResourceType) {
            BotResourceType.NONE -> emptyList()
            BotResourceType.FILE -> fromFile()
            BotResourceType.RESOURCE -> fromResource()
            BotResourceType.BOTH -> fromFile() + fromResource()
            BotResourceType.FILE_FIRST -> fromFile().ifEmpty { fromResource() }
            BotResourceType.RESOURCE_FIRST -> fromResource().ifEmpty { fromFile() }
        }

        val infos: List<BotVerifyInfo> = propertiesList.map { p ->
            pairBotVerifyInfo(p,
                codeAlias,
                verificationAlias)
        }

        val actionBotsSet = actionBots.toSet()

        configuredBotVerifyInfos = ((
            if (ALL_ACTION_KEY in actionBotsSet) infos
            else infos.filter { i ->
                i[ACTION_NAME_KEY]?.let { actionName -> actionName in actionBotsSet } ?: kotlin.run {
                    LOGGER.warn("Bot(code=${i.code})'s config property '$ACTION_NAME_KEY' is null, but your action bots config properties is not ignore or '$ALL_ACTION_KEY', so this bot will always be loaded.")
                    true
                }
            }) + other).distinctBy { info ->
            info.code
        }


    }


}

/**
 * 根据扩展名获取文件的文件访问器。
 */
internal class FileVisitorByExtension<T>(
    private val extension: String,
    private val collection: MutableList<T>,
    private val mapper: (path: Path) -> T,
) : SimpleFileVisitor<Path>() {

    override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        requireNotNull(file)
        if (file.extension == extension) {
            collection.add(mapper(file))
        }

        return FileVisitResult.CONTINUE
    }
}


/**
 * 加载 `simbot-bots` 路径资源的时候，使用的文件、资源匹配原则。
 */
public enum class BotResourceType {
    /**
     * 只查找文件目录
     */
    FILE,

    /**
     * 只查找资源目录
     */
    RESOURCE,

    /**
     * 两者都查找且进行合并
     */
    BOTH,

    /**
     * 优先文件目录，如果没有任何结果则查询资源目录。
     */
    FILE_FIRST,

    /**
     * 优先资源目录，如果没有任何结果则查询文件目录。
     */
    RESOURCE_FIRST,

    /**
     * 不去获取bot配置信息。
     */
    NONE
}

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

package love.forte.simboot.spring.autoconfigure

import kotlinx.serialization.ExperimentalSerializationApi
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationBuilder
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfiguration
import love.forte.simbot.*
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.FileNotFoundException


/**
 * 用于配置bot的扫描与自动注册的配置类。
 * @author ForteScarlet
 */
public open class SimbotSpringBootBotAutoRegisterBuildConfigure(
    private val customDecoderFactories: List<BotVerifyInfoDecoderFactory<*, *>>,
) :
    SimbotSpringBootApplicationBuildConfigure {
    
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSimbotApi::class)
    override fun SpringBootApplicationBuilder.config(configuration: SpringBootApplicationConfiguration) {
        val resolver = PathMatchingResourcePatternResolver()
        
        val decoderList = customDecoderFactories + StandardBotVerifyInfoDecoderFactory.supportDecoderFactories()
        
        val botConfigResources = configuration.botConfigurationResources
            .asSequence()
            .flatMap {
                try {
                    resolver.getResources(it).asList()
                } catch (fne: FileNotFoundException) {
                    configuration.logger.warn(
                        "Can not resolve resource path 「{}」: {}, just use empty resources.",
                        it,
                        fne.localizedMessage
                    )
                    emptyList()
                }
            }
            .filter {
                if (it.filename == null) {
                    configuration.logger.warn("Resource [{}]'s filename is null, skip.", it)
                }
                
                it.filename != null
            }
            .distinct()
            .mapNotNull {
                configuration.logger.debug("Resolved bot register resource: {}", it)
                val decoderFactory = decoderList.findLast { decoder -> decoder.match(it.filename!!) }
                    // ?: null // err? warn?
                
                if (decoderFactory == null) {
                    // 没有任何解码器能匹配此资源。
                    configuration.logger.warn("No decoders match bot resource [{}] in {}.", it, decoderList)
                    return@mapNotNull null
                }
                
                var botVerifyInfo: BotVerifyInfo? = null
                
                if (it.isFile) {
                    kotlin.runCatching {
                        botVerifyInfo =
                            it.file.takeIf { f -> f.exists() }?.toPath()?.toBotVerifyInfo(decoderFactory)
                                ?: return@runCatching
                    }.getOrNull()
                }
                
                botVerifyInfo ?: it.url.toBotVerifyInfo(decoderFactory)
            }.toList()
        
        if (botConfigResources.isNotEmpty()) {
            bots {
                botConfigResources.forEach { res ->
                    register(res).also { bot ->
                        if (bot == null) {
                            configuration.logger.warn(
                                "Bot verify info [{}] not registered by any registrars, skip. The botRegistrar: {}",
                                res,
                                this
                            )
                        }
                    }?.also { bot ->
                        onCompletion {
                            // start bot on completion
                            val logger = configuration.logger
                            
                            logger.debug("Starting bot: {}", bot)
                            val start = bot.start()
                            configuration.logger.debug("Bot [{}] started: {}", bot, start)
                        }
                    }
                }
            }
        }
        
    }
}

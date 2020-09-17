package org.slf4j.impl

import love.forte.common.configuration.Configuration
import love.forte.common.configuration.ConfigurationManagerRegistry
import love.forte.common.configuration.ConfigurationParserManager
import love.forte.common.configuration.impl.ConfigurationInjectorImpl
import love.forte.common.utils.ResourceUtil
import love.forte.nekolog.*
import org.slf4j.ILoggerFactory
import org.slf4j.event.Level
import java.io.Reader

/**
 *
 * common simple logger.
 *
 * neko logger~
 *
 */
object StaticLoggerBinder {

    /**
     * 配置信息
     */
    private val configuration: NekoLogConfiguration by lazy(LazyThreadSafetyMode.NONE) {
        try {
            // conf by nekolog.yml / nekolog.properties
            var type = "yml"
            val reader: Reader =
                try {
                    ResourceUtil.getResourceUtf8Reader("nekolog.yml")
                }catch (e1: Exception) {
                    try {
                        type = "properties"
                        ResourceUtil.getResourceUtf8Reader("nekolog.properties")
                    }catch (e2: Exception) {
                        null
                    }
                } ?: return@lazy NekoLogConfiguration()
            val manager: ConfigurationParserManager = ConfigurationManagerRegistry.defaultManager()
            val config: Configuration = manager.parse(type, reader)
            // inject and return
            ConfigurationInjectorImpl.inject(NekoLogConfiguration(), config)
        }catch (e: Throwable) {
            System.err.println("cannot found config file. use default config.")
            NekoLogConfiguration()
        }
    }


    /**
     * logger factory
     */
    val loggerFactory: ILoggerFactory by lazy(LazyThreadSafetyMode.NONE) {
        // 颜色格式化工厂
        val colorBuilderFactory: ColorBuilderFactory = if(configuration.enableColor) {
            NormalColorBuilderFactory
        }else {
            NocolorBuilderFactory
        }

        val level: Level = configuration.level

        if(configuration.enableLanguage) {
            try {
                // contains Language.
                Class.forName("love.forte.common.language.Language")
                if(!love.forte.common.language.Language.isInitialized()) {
                    love.forte.common.language.Language.init()
                }
                LanguageNekoLoggerFactory(colorBuilderFactory, level)
            }catch (ignore: Exception){
                System.err.println("cannot found class 'love.forte.common.language.Language'. cannot enable language.")
                // no Language.
                NoLanguageNekoLoggerFactory(colorBuilderFactory, level)
            }
        } else {
            // no language.
            NoLanguageNekoLoggerFactory(colorBuilderFactory, level)
        }
    }


    @JvmStatic
    fun getSingleton() = this
}
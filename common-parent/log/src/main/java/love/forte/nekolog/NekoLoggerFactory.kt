/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     NekoLoggerFactory.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.nekolog

import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import org.slf4j.event.Level


/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface NekoLoggerFactory : ILoggerFactory




public abstract class BaseNekoLoggerFactory(private val colorBuilderFactory: ColorBuilderFactory, private val level: Int) : NekoLoggerFactory {
    constructor(colorBuilderFactory: ColorBuilderFactory, level: Level): this(colorBuilderFactory, level.toInt())
    abstract val loggerFormatter: LoggerFormatter
    override fun getLogger(name: String?): Logger {
        return NekoLogger(name ?: "neko", colorBuilderFactory, level, loggerFormatter)
    }
}


public class LanguageNekoLoggerFactory(colorBuilderFactory: ColorBuilderFactory, level: Level) : BaseNekoLoggerFactory(colorBuilderFactory, level) {
    override val loggerFormatter: LoggerFormatter = LanguageLoggerFormatter
}


public class NoLanguageNekoLoggerFactory(colorBuilderFactory: ColorBuilderFactory, level: Level) : BaseNekoLoggerFactory(colorBuilderFactory, level) {
    override val loggerFormatter: LoggerFormatter = NoLanguageLoggerFormatter
}



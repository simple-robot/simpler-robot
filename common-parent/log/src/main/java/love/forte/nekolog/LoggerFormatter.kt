/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     LoggerFormater.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.nekolog

import love.forte.nekolog.color.ColorBuilder


/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public fun interface LoggerFormatter {
    /**
     * 对输出的日志文本进行格式化。
     */
    fun format(info: FormatterInfo): String
}

public data class FormatterInfo(val info: String? = null,
                                val level: String? = null,
                                val colorBuilder: ColorBuilder? = ColorBuilder.getNocolorInstance(),
                                val args: Array<Any?>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FormatterInfo

        if (info != other.info) return false
        if (level != other.level) return false
        if (colorBuilder != other.colorBuilder) return false
        if (!args.contentEquals(other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = info?.hashCode() ?: 0
        result = 31 * result + (level?.hashCode() ?: 0)
        result = 31 * result + (colorBuilder?.hashCode() ?: 0)
        result = 31 * result + args.contentHashCode()
        return result
    }
}


/**
 * 使用 Language 进行格式化.
 */
object LanguageLoggerFormatter : LoggerFormatter {
    /**
     * 对输出的日志文本进行格式化。
     */
    override fun format(info: FormatterInfo): String {
        val text: String = love.forte.common.language.Language.format(info.info, info.args)
        val levelText: String = info.level?.let { "[$it]" } ?: ""

        TODO()
    }
}




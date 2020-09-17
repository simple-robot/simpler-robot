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
import love.forte.nekolog.color.ColorTypes
import love.forte.nekolog.color.FontColorTypes
import org.slf4j.event.Level
import java.time.LocalDateTime


/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface LoggerFormatter {
    /**
     * 对输出的日志文本进行格式化。
     */
    fun format(info: FormatterInfo): String

    /**
     * 只得到格式化的正文文本。
     */
    fun formatText(text: String?, args: Array<out Any?>): String
}


public data class FormatterInfo
@JvmOverloads
constructor(
    val info: String? = null,
    val level: Level? = null,
    val name: String? = null,
    val thread: Thread? = null,
    val stackTrace: StackTraceElement? = null,
    val colorBuilder: ColorBuilder = ColorBuilder.getNocolorInstance(),
    var args: Array<out Any?>
) {


    companion object {
        @JvmStatic
        fun create(info: String, level: Level, vararg args: Any?): FormatterInfo =
            FormatterInfo(info = info, level = level, args = args)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        Thread.currentThread().stackTrace

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
        result = 31 * result + (colorBuilder.hashCode())
        result = 31 * result + args.contentHashCode()
        return result
    }
}


public abstract class BaseLoggerFormatter(private val textFormat: (String?, Array<out Any?>) -> String) :
    LoggerFormatter {
    /**
     * 对输出的日志文本进行格式化。
     */
    override fun format(info: FormatterInfo): String {
        val builder = info.colorBuilder
        // [time][threadName] [level] stackTrace : msg

        val color: ColorTypes = info.level?.color ?: FontColorTypes.BLUE

        builder.color(color)

        builder.add("[", LocalDateTime.now().toString(), "]")
        info.thread?.apply {
            val threadName = this.name
            builder.add("[", threadName, "]")
        }
        builder.add(" ")
        info.level?.apply {
            builder.add("[", this.name.text, "] ")
        }
        info.name?.apply {
            builder.append(this).append(' ')
        }
        info.stackTrace?.apply {
            if (info.name != null) {
                builder.append("| ")
            }
            val stackTraceText: String = this.show(info.name)
            builder.append(stackTraceText).append(' ')
        }
        builder.append(": ")
        builder.add(color, textFormat(info.info, info.args))
        return builder.toString()
    }

    override fun formatText(text: String?, args: Array<out Any?>): String = textFormat(text, args)
}


/**
 * 使用 Language 进行格式化.
 */
object LanguageLoggerFormatter :
    BaseLoggerFormatter({ text, args -> love.forte.common.language.Language.format(text, *args) })


/**
 * 不进行语言格式化.
 */
object NoLanguageLoggerFormatter : BaseLoggerFormatter({ text, _ -> text ?: "null" })


internal fun StackTraceElement.show(name: String? = null): String {
    return this.toString().let {
        if (name != null && it.startsWith(name)) {
            it.substring(name.length)
        } else if (it.length < 50) {
            it
        } else {
            val sb = StringBuilder(50)
            val split: List<String> = className.split(".")
            split.forEachIndexed { index, s ->
                if (index < split.lastIndex) {
                    sb.append(s.firstOrNull().toString()).append('.')
                } else sb.append(s)
            }
            sb.append('(').append(fileName).append(':').append(lineNumber).append(')')
            sb.toString()
        }
    }
}


private val Level.color: ColorTypes
    get() {
        return when (this) {
            Level.ERROR -> FontColorTypes.RED
            Level.WARN -> FontColorTypes.YELLOW
            Level.INFO -> FontColorTypes.BLUE
            Level.DEBUG -> FontColorTypes.PURPLE
            Level.TRACE -> FontColorTypes.DARK_GREEN
        }
    }

private var maxSize = 5

private val String.text: String
    get() {
        val size = maxSize
        return when {
            length == size -> this
            length > size -> {
                maxSize = length
                val appendSize = size - length
                this + String(CharArray(appendSize) { ' ' })
            }
            else -> {
                val appendSize = size - length
                this + String(CharArray(appendSize) { ' ' })
            }
        }
    }

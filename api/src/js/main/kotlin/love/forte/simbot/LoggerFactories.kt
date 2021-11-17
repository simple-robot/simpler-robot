package love.forte.simbot

import kotlin.js.Date
import kotlin.reflect.KClass

/**
 * Js actual LoggerFactory
 *
 * @author ForteScarlet
 */
public actual object LoggerFactory {
    // private var
    public val factory: (String) -> Logger = ::JsPrintLogger
    public actual fun getLogger(name: String): Logger = factory(name)
    public actual fun getLogger(type: KClass<*>): Logger = getLogger(type.js.name)
}

/**
 * Js actual Logger
 */
public actual interface Logger {
    public actual fun getName(): String
    public actual fun isTraceEnabled(): Boolean

    @JsName("trace")
    public actual fun trace(msg: String)

    @JsName("traceFormat1")
    public actual fun trace(format: String, arg: Any)

    @JsName("traceFormat2")
    public actual fun trace(format: String, arg1: Any, arg2: Any)

    @JsName("traceError")
    public actual fun trace(msg: String, t: Throwable)
    public actual fun isDebugEnabled(): Boolean

    @JsName("debug")
    public actual fun debug(msg: String)

    @JsName("debugFormat1")
    public actual fun debug(format: String, arg: Any)

    @JsName("debugFormat2")
    public actual fun debug(format: String, arg1: Any, arg2: Any)

    @JsName("debugError")
    public actual fun debug(msg: String, t: Throwable)
    public actual fun isInfoEnabled(): Boolean

    @JsName("info")
    public actual fun info(msg: String)

    @JsName("infoFormat1")
    public actual fun info(format: String, arg: Any)

    @JsName("infoFormat2")
    public actual fun info(format: String, arg1: Any, arg2: Any)

    @JsName("infoError")
    public actual fun info(msg: String, t: Throwable)
    public actual fun isWarnEnabled(): Boolean

    @JsName("warn")
    public actual fun warn(msg: String)

    @JsName("warnFormat1")
    public actual fun warn(format: String, arg: Any)

    @JsName("warnFormat2")
    public actual fun warn(format: String, arg1: Any, arg2: Any)

    @JsName("warnError")
    public actual fun warn(msg: String, t: Throwable)
    public actual fun isErrorEnabled(): Boolean

    @JsName("error")
    public actual fun error(msg: String)

    @JsName("errorFormat1")
    public actual fun error(format: String, arg: Any)

    @JsName("errorFormat2")
    public actual fun error(format: String, arg1: Any, arg2: Any)

    @JsName("errorError")
    public actual fun error(msg: String, t: Throwable)
}


@Suppress("NOTHING_TO_INLINE")
private class JsPrintLogger(
    private val name: String,
) : Logger {
    override fun getName(): String = name
    private inline fun doLog(check: () -> Boolean, log: (Any?) -> Unit, name: String, msg: String, args: Any?) {
        if (check()) {
            when(args) {
                null -> log(msg.toMsg(name))
                is Array<*> -> {
                    var m = msg
                    for (arg in args) {
                        if (!m.contains("{}")) break
                        m = m.replace("{}", arg.toString())
                    }
                    log(m.toMsg(name))
                }
                is Throwable -> {
                    log(msg.toMsg(name))
                    log(args.stackTraceToString())
                }
                else -> log(msg.replace("{}", args.toString()).toMsg(name))
            }
        }
    }
    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.toMsg(name: String): String = "${Date().toDateString()} - $name - [$name]: $this"

    override fun isTraceEnabled(): Boolean = true

    private inline fun trace0(msg: String, args: Any? = null) =
        doLog(::isTraceEnabled, console::log, "TRACE", msg, args)

    override fun trace(msg: String) = trace0(msg)
    override fun trace(format: String, arg: Any) = trace0(format, arg)
    override fun trace(format: String, arg1: Any, arg2: Any) = trace0(format, arrayOf(arg1, arg2))
    override fun trace(msg: String, t: Throwable) = trace0(msg, t)


    override fun isDebugEnabled(): Boolean = true
    private inline fun debug0(msg: String, args: Any? = null) =
        doLog(::isDebugEnabled, console::log, "DEBUG", msg, args)

    override fun debug(msg: String) = debug0(msg)
    override fun debug(format: String, arg: Any) = debug0(format, arg)
    override fun debug(format: String, arg1: Any, arg2: Any) = debug0(format, arrayOf(arg1, arg2))
    override fun debug(msg: String, t: Throwable) = debug0(msg, t)

    override fun isInfoEnabled(): Boolean = true
    private inline fun info0(msg: String, args: Any? = null) = doLog(::isInfoEnabled, console::info, " INFO", msg, args)
    override fun info(msg: String) = info0(msg)
    override fun info(format: String, arg: Any) = info0(format, arg)
    override fun info(format: String, arg1: Any, arg2: Any) = info0(format, arrayOf(arg1, arg2))
    override fun info(msg: String, t: Throwable) = info0(msg, t)

    override fun isWarnEnabled(): Boolean = true
    private inline fun warn0(msg: String, args: Any? = null) = doLog(::isWarnEnabled, console::warn, " WARN", msg, args)
    override fun warn(msg: String) = warn0(msg)
    override fun warn(format: String, arg: Any) = warn0(format, arg)
    override fun warn(format: String, arg1: Any, arg2: Any) = warn0(format, arrayOf(arg1, arg2))
    override fun warn(msg: String, t: Throwable) = warn0(msg, t)

    override fun isErrorEnabled(): Boolean = true
    private inline fun error0(msg: String, args: Any? = null) = doLog(::isErrorEnabled, console::error, "ERROR", msg, args)
    override fun error(msg: String) = error0(msg)
    override fun error(format: String, arg: Any) = error0(format, arg)
    override fun error(format: String, arg1: Any, arg2: Any) = error0(format, arrayOf(arg1, arg2))
    override fun error(msg: String, t: Throwable) = error0(msg, t)

}


public actual object I18n {
    @Suppress("ObjectPropertyName")
    private var _locale: String = "cn"

    private val properties = mutableMapOf<String, String>()

    public actual operator fun get(key: String): String =
        properties[key] ?: throw NullPointerException("I18n key: $key")

    public actual val keys: Iterator<String> get() = properties.keys.iterator()

    public actual val locale: String
        get() = _locale

    public fun setLocale(value: String) {
        _locale = value
    }
}

/**
 * 字符串的国际化获取，
 * 约定国际化语言路径为 `lang.message`.
 *
 * JVM实现中使用 @PropertyKey 进行约束。
 */
@Suppress("NOTHING_TO_INLINE")
public actual val String.i18n: String
    get() = I18n[this]
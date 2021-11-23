/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:Suppress("unused")
package love.forte.simbot

import org.jetbrains.annotations.PropertyKey
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName
import org.slf4j.LoggerFactory as Slf4jLoggerFactory

/**
 * Logger by slf4j logger.
 */
public actual typealias Logger = org.slf4j.Logger

/**
 * JVM actual LoggerFactory
 */
public actual object LoggerFactory {
    @JvmStatic
    public actual fun getLogger(name: String): Logger = Slf4jLoggerFactory.getLogger(name)

    @JvmStatic
    public actual fun getLogger(type: KClass<*>): Logger =
        kotlin.runCatching { getLogger(type.java) }.getOrElse {
            kotlin.runCatching { getLogger(type.qualifiedName ?: type.simpleName ?: type.jvmName) }.getOrElse {
                getLogger(type.toString())
            }
        }

    @JvmStatic
    public fun getLogger(type: Class<*>): Logger = Slf4jLoggerFactory.getLogger(type)

}


/**
 * 通过 [resourceBundle] 加载 i18n properties.
 *
 */
public actual object I18n {
    private val resourceBundle = java.util.ResourceBundle.getBundle("lang/message")


    public actual operator fun get(
        @PropertyKey(
            resourceBundle = "lang.message")
        key: String,
    ): String = resourceBundle.getString(key)

    public actual val keys: Iterator<String>
        get() = resourceBundle.keys.asIterator()

    public actual val locale: String
        get() = resourceBundle.locale.displayName
}

/**
 * 字符串的国际化获取，
 * 约定国际化语言路径为 `lang.message`.
 *
 * JVM实现中使用 @PropertyKey 进行约束。
 */
public actual val
        @receiver:PropertyKey(resourceBundle = "lang.message")
        String.i18n: String
    get() = I18n[this]
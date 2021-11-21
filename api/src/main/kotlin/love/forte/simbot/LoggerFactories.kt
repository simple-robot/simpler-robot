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

@file:JvmSynthetic
@file:JvmMultifileClass
@file:JvmName("LoggerFactories")

package love.forte.simbot

import org.jetbrains.annotations.PropertyKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.KClass


/**
 * api模块下使用的 i18n内容。
 */
internal val
        @receiver:PropertyKey(resourceBundle = "lang.api-message")
        String.i18n: String
    get() = I18n[this]


internal object I18n {
    private val resourceBundle = ResourceBundle.getBundle("lang/api-message")
    operator fun get(
        @PropertyKey(
            resourceBundle = "lang.api-message"
        )
        key: String,
    ): String = resourceBundle.getString(key)
    val keys: Iterator<String> get() = resourceBundle.keys.iterator()
    val locale: String get() = resourceBundle.locale.displayName
}


/**
 * 日志工厂, 用于得到一个日志实例.
 * @author ForteScarlet
 */
public object LoggerFactory {
    @JvmStatic
    public fun getLogger(name: String): Logger = LoggerFactory.getLogger(name)

    @JvmStatic
    public fun getLogger(type: KClass<*>): Logger =
        kotlin.runCatching { getLogger(type.java) }.getOrElse {
            kotlin.runCatching { getLogger(type.qualifiedName ?: type.simpleName ?: type.toString()) }.getOrElse {
                getLogger(type.toString())
            }
        }

    @JvmStatic
    public fun getLogger(type: Class<*>): Logger = LoggerFactory.getLogger(type)

}

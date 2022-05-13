/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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
 *
 */

@file:JvmSynthetic
@file:JvmMultifileClass
@file:JvmName("LoggerFactories")

package love.forte.simbot

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass


// /**
//  * api模块下使用的 i18n内容。
//  * TODO
//  */
// internal val
//         @receiver:PropertyKey(resourceBundle = "lang.api-message")
//         String.i18n: String
//     get() = I18n[this]

// TODO
//
// internal object I18n {
//     private val resourceBundle = ResourceBundle.getBundle("lang/api-message")
//     operator fun get(
//         @PropertyKey(
//             resourceBundle = "lang.api-message"
//         )
//         key: String,
//     ): String = resourceBundle.getString(key)
//
//     val keys: Iterator<String> get() = resourceBundle.keys.iterator()
//     val locale: String get() = resourceBundle.locale.displayName
// }


/**
 * 日志工厂, 用于得到一个日志实例.
 * @author ForteScarlet
 */
public object LoggerFactory {
    
    /**
     * 根据名称得到一个 [Logger].
     *
     * @see LoggerFactory.getLogger
     */
    @JvmStatic
    public fun getLogger(name: String): Logger = LoggerFactory.getLogger(name)
    
    /**
     * 根据 [KClass]（的全限定名称）构建一个 [Logger].
     *
     */
    @JvmStatic
    public fun getLogger(type: KClass<*>): Logger =
        kotlin.runCatching { getLogger(type.java) }.getOrElse {
            kotlin.runCatching { getLogger(type.qualifiedName ?: type.simpleName ?: type.toString()) }.getOrElse {
                getLogger(type.toString())
            }
        }
    
    /**
     * 根据 [T]（的全限定名称）构建一个 [Logger].
     *
     */
    public inline fun <reified T : Any> getLogger(): Logger = getLogger(T::class)
    
    
    /**
     * 根据 [Class] 构建一个 [Logger].
     *
     * @see LoggerFactory.getLogger
     */
    @JvmStatic
    public fun getLogger(type: Class<*>): Logger = LoggerFactory.getLogger(type)
    
}

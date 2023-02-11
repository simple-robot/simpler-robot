/*
 * Copyright (c) 2021-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */
package love.forte.annotationtool.core

import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.safeCast

/**
 * Converters, Only when the types are the same will the conversion be carried out.
 *
 * @author ForteScarlet
 */
internal object NonConverters : Converters {
    private val primitives: Set<KClass<*>> = setOf(
        Byte::class, Short::class, Int::class, Long::class,
        Double::class, Float::class, Char::class, Boolean::class,
        String::class, CharSequence::class
    )

    /**
     * when the types are the same will the conversion be carried out.
     *
     * @throws ConvertException if [type &#39;to&#39;][TO] is not assignable from [type &#39;from&#39;][FROM]
     */
    override fun <FROM : Any, TO : Any> convert(from: KClass<FROM>?, instance: FROM, to: KClass<TO>): TO {
        val fromType: KClass<out FROM> = from ?: instance::class

        val safeCast: TO? = to.safeCast(instance)
        if (safeCast != null) return safeCast

        @Suppress("UNCHECKED_CAST")
        if (from == Class::class && to == KClass::class) {
            // class to kClass
            return (instance as Class<*>).kotlin as TO
        }
        @Suppress("UNCHECKED_CAST")
        if (from == KClass::class && to == Class::class) {
            // class to kClass
            return (instance as KClass<*>).java as TO
        }

        if (fromType in primitives && to in primitives) {
            when {
                fromType.isSubclassOf(Number::class) -> {
                    @Suppress("UNCHECKED_CAST")
                    fromType as KClass<out Number>
                    val converted = when (to) {
                        Byte::class -> to.cast(fromType.cast(instance).toByte())
                        Short::class -> to.cast(fromType.cast(instance).toShort())
                        Int::class -> to.cast(fromType.cast(instance).toInt())
                        Char::class -> to.cast(fromType.cast(instance).toChar())
                        Long::class -> to.cast(fromType.cast(instance).toLong())
                        Float::class -> to.cast(fromType.cast(instance).toFloat())
                        Double::class -> to.cast(fromType.cast(instance).toDouble())
                        String::class -> to.cast(fromType.cast(instance).toString())
                        CharSequence::class -> to.cast(fromType.cast(instance).toString())
                        else -> null
                    }
                    if (converted != null) return converted
                }
                fromType == String::class -> {
                    @Suppress("UNCHECKED_CAST")
                    fromType as KClass<out String>
                    val converted = when (to) {
                        Byte::class -> to.cast(fromType.cast(instance).toByte())
                        Short::class -> to.cast(fromType.cast(instance).toShort())
                        Int::class -> to.cast(fromType.cast(instance).toInt())
                        Char::class -> {
                            val str = fromType.cast(instance)
                            if (str.length == 1) to.cast(str[0]) else throw ConvertException(fromType, to, instance)
                        }
                        Long::class -> to.cast(fromType.cast(instance).toLong())
                        Float::class -> to.cast(fromType.cast(instance).toFloat())
                        Double::class -> to.cast(fromType.cast(instance).toDouble())
                        String::class -> to.cast(fromType.cast(instance))
                        CharSequence::class -> to.cast(fromType.cast(instance))
                        else -> null
                    }
                    if (converted != null) return converted
                }
            }

        }

        throw ConvertException(fromType, to, instance)
        // throw ConvertException("NonConverters only support when the types are the same will the conversion be carried out, But $from is not a subtype of $to")
    }

}

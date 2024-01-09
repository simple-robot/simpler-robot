/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.quantcat.common.convert

import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.safeCast

/**
 * Converters, Only when the types are the same will the conversion be carried out.
 *
 * @author ForteScarlet
 */
internal object NonConverters {
    private val primitives: Set<KClass<*>> = setOf(
        Byte::class, Short::class, Int::class, Long::class,
        UInt::class, ULong::class,
        Double::class, Float::class, Char::class, Boolean::class,
        String::class, CharSequence::class
    )

    /**
     * when the types are the same will the conversion be carried out.
     *
     * @throws ConvertException if [type &#39;to&#39;][TO] is not assignable from [type &#39;from&#39;][FROM]
     */
    fun <FROM : Any, TO : Any> convert(instance: FROM, to: KClass<TO>): TO {
        val fromType: KClass<out FROM> = instance::class

        val safeCast: TO? = to.safeCast(instance)
        if (safeCast != null) return safeCast

        if (fromType in primitives && to in primitives) {
            when (instance) {
                is Number -> {
                    val converted = when (to) {
                        Byte::class -> to.cast(instance.toByte())
                        Short::class -> to.cast(instance.toShort())
                        Int::class -> to.cast(instance.toInt())
                        UInt::class -> to.cast(instance.toInt().toUInt())
                        Char::class -> to.cast(instance.toInt().toChar())
                        Long::class -> to.cast(instance.toLong())
                        ULong::class -> to.cast(instance.toLong().toULong())
                        Float::class -> to.cast(instance.toFloat())
                        Double::class -> to.cast(instance.toDouble())
                        String::class -> to.cast(instance.toString())
                        CharSequence::class -> to.cast(instance.toString())
                        else -> null
                    }
                    if (converted != null) return converted
                }

                is String -> {
                    val converted = when (to) {
                        Byte::class -> to.cast(instance.toByte())
                        Short::class -> to.cast(instance.toShort())
                        Int::class -> to.cast(instance.toInt())
                        UInt::class -> to.cast(instance.toUInt())
                        Char::class -> {
                            if (instance.length == 1) to.cast(instance[0]) else throw ConvertException(
                                fromType,
                                to,
                                instance
                            )
                        }

                        Long::class -> to.cast(instance.toLong())
                        ULong::class -> to.cast(instance.toULong())
                        Float::class -> to.cast(instance.toFloat())
                        Double::class -> to.cast(instance.toDouble())
                        String::class -> to.cast(instance)
                        CharSequence::class -> to.cast(instance)
                        else -> null
                    }
                    if (converted != null) return converted
                }
            }
        }

        throw ConvertException(fromType, to, instance)
    }

}

/**
 * @author ForteScarlet
 */
public class ConvertException : ClassCastException {
    public constructor()
    public constructor(s: String?) : super(s)
    public constructor(
        from: KClass<*>,
        to: KClass<*>,
        instance: Any
    ) : super("Cannot convert instance $instance type of $from to type of $to")
}

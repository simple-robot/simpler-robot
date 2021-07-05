/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("KTypeUtil")
package love.forte.simbot.utils

import kotlin.reflect.KClass


private inline val stringOrPrimitives: List<KClass<*>> get() = listOf(
    // string
    String::class,
    // primitives
    Byte::class,
    Short::class,
    Int::class,
    Long::class,
    Double::class,
    Float::class,
    Boolean::class,
    Char::class,
)


public fun KClass<*>.isStringOrPrimitive(): Boolean {
    return stringOrPrimitives.any { pClass -> this == pClass }
}

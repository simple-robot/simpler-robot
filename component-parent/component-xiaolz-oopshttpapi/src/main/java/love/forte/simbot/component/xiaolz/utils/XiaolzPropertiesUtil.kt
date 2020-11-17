/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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
@file:JvmName("XiaolzPropertiesUtil")

package love.forte.simbot.component.xiaolz.utils

import love.forte.common.utils.convert.ConverterManager
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.javaType
import kotlin.reflect.jvm.javaType


private fun String.toBigHump(): String {
    val builder = StringBuilder(length)
    var toUpper = false
    forEach {
        when {
            toUpper -> {
                builder.append(it.toUpperCase())
                toUpper = false
            }
            it == '_' -> {
                toUpper = true
            }
            else -> {
                builder.append(it)
            }
        }
    }
    return builder.toString()
}


/**
 * 将 xx=xx&xx=xx的参数转化为Map参数。
 */
public fun String.toParamMap(): Map<String, String?> {
    return splitToSequence("&").map { sp ->
        sp.split("=").let {
            val k = it[0].toBigHump()
            k to if (it.size > 1) {
                it[1]
            } else ""
        }
    }.toMap()
}


/**
 * 将 xx=xx&xx=xx的参数注入到一个Data class type中。
 */
public fun <T> String.toDataMap(type: KClass<*>, convert: ConverterManager): T {
    val paramMap = this.toParamMap()
    return if (type.isData) {
        val primaryConstructor = type.primaryConstructor
        primaryConstructor?.run {
            val params = parameters.map {
                convert.convert<Any>(it.type.javaType, paramMap[it.name])
            }.toTypedArray()
            call(params)
        } as T ?: throw IllegalStateException("$type No primaryConstructor.")
    } else {
        // not data class.
        TODO()
    }
}









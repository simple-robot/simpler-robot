/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

import kotlin.reflect.KFunction
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.jvm.kotlinFunction

fun a() {}

fun String.b() {}

object AA {

    fun a() {}
    fun String.b() {}

}

class BB {
    fun a() {}
    fun String.b() {}
}


fun main() {
    val clazz = ClassLoader.getSystemClassLoader().loadClass("FunctionTestKt")
    println(clazz)
    clazz.classes

    for (method in clazz.methods) {
        println(method)
        val f = method.kotlinFunction
        println(f)
        println()

    }

    // ::a.showInfo()
    // println("====")
    // String::b.showInfo()
    // println("====")
    // for (function in AA::class.functions) {
    //     function.showInfo()
    // }
    // println("====")
    // for (function in BB::class.memberFunctions) {
    //     function.showInfo()
    // }

}

internal fun KFunction<*>.showInfo() {
    println("function: $this")
    println("instanceParameter: $instanceParameter type: ${instanceParameter?.type}")
    println("extensionReceiverParameter: $extensionReceiverParameter type: ${extensionReceiverParameter?.type}")
    println()
}
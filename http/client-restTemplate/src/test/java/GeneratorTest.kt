/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     GeneratorTest.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */


fun main() {

    val nums = generator { start: Int ->
        println("start cor.")
        for (i in 0 .. 5) {
            println("bef yield $i")
            yield(start + i)
            println("aft yield $i")
        }
        println("end cor.")
    }
    println("bef get gen.")

    val gen = nums(10)

    println("aft get gen.")

    println("to get j.")

    val iter = gen.iterator()

    println("get iter.")


    for (j in gen) {
        println(j)
        println("aft get $j")
        println()
    }


}
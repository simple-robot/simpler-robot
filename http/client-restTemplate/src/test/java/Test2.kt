/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     Test2.kt
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


    val wait = waitFor<Int> {
        println("n1")
        val n1 = wait()
        println("n1: $n1")
        println("n2")
        val n2 = wait()
        println("n2: $n2")
        println("n1 n2 finished.")
        println("total: ${n1 + n2}")
    }



    println("bef wake1")
    wait.wakeup(1)
    println("bef wake2")
    wait.wakeup(4)


    println("end?")


}

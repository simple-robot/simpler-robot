
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.suspendCoroutine

/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     Test.kt
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


suspend fun getInt(num: Int): String {
    return suspendCoroutine {
        // thread {
            println("$num\t before ${Thread.currentThread()}")
            // Thread.sleep(1000)
            it.resumeWith(Result.success("NUM($num)")).also {
                println("$num\t after ${Thread.currentThread()}")

            // }
        }
    }
}


suspend fun main() {
    val s = System.currentTimeMillis()
    (1 .. 1000).map {
        GlobalScope.async {
            getInt(it)
        }
    }.map { runBlocking { it.await() } }


    println("end: ${System.currentTimeMillis() - s} ms.")

}




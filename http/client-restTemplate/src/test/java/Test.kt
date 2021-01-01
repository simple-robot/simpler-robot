import kotlinx.coroutines.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.*
import kotlin.reflect.full.callSuspend

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

val executor = Executors.newScheduledThreadPool(0) { r -> thread(start = false, isDaemon = false, block = { r.run() }) }

val now: Int get() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()

suspend fun getInt(num: Int): String {
    return suspendCoroutine {
        // println("'$num' 2秒后得到. t: $now")
        Dispatchers.Default.dispatch(it.context) {
            val str = "NUM($num)"
            println("得到结果$str 了！t: $now")
            println("准备恢复调用了 $str")
            it.resume(str)
            println("恢复调用了 $str")
        }
        // executor.schedule({
        //     val str = "NUM($num)"
        //     println("得到结果$str 了！t: $now")
        //     println("准备恢复调用了 $str")
        //     it.resume(str)
        //     println("恢复调用了 $str")
        // }, 2, TimeUnit.SECONDS)
        println("'$num' 已经开始等待了. ")
    }
}


suspend fun main() {
    val s = System.currentTimeMillis()


    // val job1 = GlobalScope.launch {
    println("要开始取1了！")
    println("要开始取1了！")
    ::getInt.startCoroutine(1, Continuation(EmptyCoroutineContext) {
        println("get int: $it")
    })
    println("要开始取2了！")
    println("要开始取2了！")
    ::getInt.startCoroutine(2, Continuation(EmptyCoroutineContext) {
        println("get int: $it")
    })



    // }

    // val job2 = GlobalScope.launch {
    // println("要开始取3了！")
    // println("要开始取3了！")
    // println("get int: " + ::getInt.startCoroutine(3, Continuation(EmptyCoroutineContext) { }))
    // println("要开始取4了！")
    // println("要开始取4了！")
    // println("get int: " + ::getInt.startCoroutine(4, Continuation(EmptyCoroutineContext) { }))
    // }

    // println("主挂起1")
    // println("主挂起1")
    // delay(2000)
    // println("主挂起2")
    // println("主挂起2")
    // delay(2000)

    println("end.")
    println("${System.currentTimeMillis() - s} ms.")

    // println("要主睡眠了！")
    // (0 .. 10).forEach {
    //     println("$it 层循环了！")
    //     (0 .. 100).forEach {
    //     }
    // }
    // Thread.sleep(2000)

}




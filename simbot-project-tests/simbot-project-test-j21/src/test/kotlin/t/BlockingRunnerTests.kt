package t

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import love.forte.simbot.utils.runInBlocking
import love.forte.simbot.utils.runInNoScopeBlocking
import kotlin.coroutines.resume


fun main() {
    System.setProperty("simbot.blockingRunner.waitTimeoutMilliseconds", "2000")
//    System.setProperty("simbot.runInBlocking.dispatcher", "custom")
    System.setProperty("simbot.runInBlocking.dispatcher", "forkJoinPool")
//    runInNoScopeBlocking {
//        delay(5000)
//        println("你好")
//    }
//
//
//    val value = runInNoScopeBlocking {
//        delay(5000)
//        kotlin.random.Random.nextLong()
//    }
//
//    println("value = $value")

    val value2 = runInBlocking {
        println("T[1]" + Thread.currentThread())
        coroutineScope {
            println("T[2]" + Thread.currentThread())
            suspendCancellableCoroutine { c ->
                println("T[3]" + Thread.currentThread())
                launch {
                    println("T[4]" + Thread.currentThread())
                    delay(20)
                    c.resume(kotlin.random.Random.nextLong())
                    println("T[5]" + Thread.currentThread())
                }
            }
            println("T[6]" + Thread.currentThread())
        }
        println("T[7]" + Thread.currentThread())
    }

    println("value2 = $value2")

//    runInNoScopeBlocking {
//        delay(5000)
//        throw RuntimeException()
//    }

    println("Done.")
}

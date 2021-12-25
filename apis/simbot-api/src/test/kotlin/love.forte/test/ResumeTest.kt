package love.forte.test

import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ObsoleteCoroutinesApi::class)
private val scope = CoroutineScope(newFixedThreadPoolContext(16, "A"))

suspend fun resumeA(id: String): Int = suspendCoroutine { continuation ->
    scope.launch {
        delay(300)
        continuation.resume(5)
        println("resumed $id!")
    }
}

suspend fun main() {

    println("Before a")
    println(resumeA("A"))
    println("After  a")
    println("Before  b")
    println(resumeA("B"))
    println("After  b")


}
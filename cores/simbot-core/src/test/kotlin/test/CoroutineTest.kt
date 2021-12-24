package test

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay


suspend fun runA(n: Int) : Int {
    delay(1)
    if (n == 2) error("2")
    println("N: $n")
    return n
}



suspend fun runB(): Int = coroutineScope {
    val c1 = coroutineScope {
        val v1 = async { runA(1) }
        val v2 = async { runA(3) }
        val v3 = async { runA(5) }
        val v4 = async { runA(2) }
        val v5 = async { runA(4) }
        v1.await() + v2.await() + v3.await() + v4.await() + v5.await()
    }
    val c2 = coroutineScope {
        val v1 = runA(1)
        val v2 = runA(3)
        v1 + v2
    }

    c1 + c2
}

suspend fun main() {
    println(runB())
}
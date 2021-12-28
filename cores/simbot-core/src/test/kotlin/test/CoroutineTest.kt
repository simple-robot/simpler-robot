/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package test

import kotlinx.coroutines.*
import kotlin.coroutines.resume


suspend fun main() = coroutineScope {
    val deferred = async {
        delay(1000)
        5
    }

    val v1 = withTimeoutOrNull(200) { deferred.await() }

    val v2 = withTimeoutOrNull(200) { deferred.await() }

    println(v1)
    println(v2)
    println(deferred.isCompleted)
    println(deferred.isActive)
    println(deferred.isCancelled)
    println(deferred.await())

}

lateinit var c: CancellableContinuation<Int>

suspend fun num(): Int = suspendCancellableCoroutine {
    it.invokeOnCancellation { e ->
        println("Cancel! $e")
    }
    c = it
}

fun setNum(value: Int) {
    c.resume(value)
}
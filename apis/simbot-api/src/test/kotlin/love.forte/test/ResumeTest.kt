/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

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
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

import kotlinx.coroutines.*
import love.forte.simbot.utils.lazyValue

/*
 *  Copyright (c) 2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

class A



@OptIn(ObsoleteCoroutinesApi::class)
suspend fun main() {
    val job = Job()
    val scope = CoroutineScope(job + newFixedThreadPoolContext(8, "A"))

    val value = scope.lazyValue(init = true) {
        println("init")
        delay(1000)
        //throw NullPointerException("NOO")
        A()
    }

    println("wait..")
    delay(100)

    repeat(100) {
        scope.launch {
            println(value.await().hashCode())
        }
    }

    println(job.children.toList().size)
    while (job.isActive) {
        delay(500)
        if (job.children.toList().isEmpty()) job.cancel()

        println(job.children.toList().size)
    }
    job.cancel()
}
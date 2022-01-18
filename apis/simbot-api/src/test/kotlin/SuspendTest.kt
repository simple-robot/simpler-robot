/*
 *  Copyright (c) 2022-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

import kotlinx.coroutines.*

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

@OptIn(ObsoleteCoroutinesApi::class)
suspend fun main() {
    val j = SupervisorJob()
    val scope = CoroutineScope(j + newFixedThreadPoolContext(8, "test"))

    val job = scope.launch(start = CoroutineStart.LAZY) {
        delay(700)
        println("get value!")
    }

    job.cancel()
    job.join()
    println("job down.")

    println(job.start())

    j.join()
}
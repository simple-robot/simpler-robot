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

package jobtest/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import love.forte.simboot.SimbootApp
import love.forte.simboot.core.SimbootApplication
import love.forte.simbot.LoggerFactory
import kotlin.time.Duration.Companion.seconds

/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@SimbootApplication
class JobTest

suspend fun main() {
    val logger = LoggerFactory.getLogger(JobTest::class)

    val context = SimbootApp.run(JobTest::class)

    context.invokeOnCompletion { logger.info("Done.") }

    logger.info("Started: {}", context)

    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        delay(5.seconds.inWholeMilliseconds)
        logger.info("Shutdown!")
        context.cancel()
    }



    context.join()
}
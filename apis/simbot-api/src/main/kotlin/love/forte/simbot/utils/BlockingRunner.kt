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

package love.forte.simbot.utils

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

private val DefaultBlockingContext: CoroutineContext = Dispatchers.IO + CoroutineName("runInBlocking")


@Throws(InterruptedException::class)
public fun <T> runInBlocking(
    context: CoroutineContext = DefaultBlockingContext,
    block: suspend CoroutineScope.() -> T
): T = runBlocking(context, block)
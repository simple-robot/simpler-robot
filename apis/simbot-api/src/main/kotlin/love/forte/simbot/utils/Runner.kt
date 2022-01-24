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

package love.forte.simbot.utils

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlin.coroutines.CoroutineContext

@PublishedApi
internal val RunWithInterruptibleDefaultCoroutineContext: CoroutineContext =
    Dispatchers.IO + CoroutineName("runWithInterruptible")

/**
 * 默认使用 [Dispatchers.IO] 作为调度器的可中断执行函数。
 *
 * @see runInterruptible
 */
public suspend inline fun <T> runWithInterruptible(
    context: CoroutineContext = RunWithInterruptibleDefaultCoroutineContext,
    crossinline block: () -> T
): T {
    return runInterruptible(context) { block() }
}
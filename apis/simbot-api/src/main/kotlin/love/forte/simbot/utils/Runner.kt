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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlin.coroutines.CoroutineContext


/**
 * 默认使用 [Dispatchers.IO] 作为调度器的可中断执行函数。
 *
 * @see runInterruptible
 */
public suspend inline fun <T> runWithInterruptible(
    context: CoroutineContext = Dispatchers.IO,
    noinline block: () -> T
): T {
    return runInterruptible(context, block)
}
/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.core.application

import kotlinx.coroutines.CompletableJob
import love.forte.simbot.application.Application
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException


/**
 *
 * @author ForteScarlet
 */
public abstract class BaseApplication : Application {
    abstract override val coroutineContext: CoroutineContext
    protected abstract val job: CompletableJob
    protected abstract val logger: Logger

    override suspend fun join() {
        job.join()
    }

    // TODO sync.

    override suspend fun shutdown(reason: Throwable?) {
        job.cancel(reason?.let { CancellationException(reason) })
        stopAll()
    }

    protected open suspend fun stopAll() {
        providers.forEach {
            kotlin.runCatching {
                it.cancel()
            }.getOrElse { e ->
                logger.error("Event provider $it cancel failure.", e)
            }
        }
    }
}
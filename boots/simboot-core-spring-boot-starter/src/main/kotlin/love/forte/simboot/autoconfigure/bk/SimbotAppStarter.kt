/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simboot.autoconfigure.bk

import love.forte.simboot.SimbootContext
import love.forte.simbot.Api4J
import love.forte.simbot.LoggerFactory
import org.slf4j.Logger
import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import kotlin.concurrent.thread

/**
 * 为 [SimbootContext] 提供启动日志。
 *
 * @author ForteScarlet
 */
@ConditionalOnClass(SimbootContext::class)
public class SimbotAppStarter(
    private val simbootContext: SimbootContext,
    private val simbootContextStarterProperties: SimbootContextStarterProperties
    ) : ApplicationRunner, DisposableBean {

    @Volatile
    private var keepaliveThread: Thread? = null

    override fun run(args: ApplicationArguments) {
        logger.info("Simboot for spring start finished. context: {}", simbootContext)
        if (simbootContextStarterProperties.keepAlive) {

            @OptIn(Api4J::class)
            keepaliveThread = thread(start = true, isDaemon = false) {
                simbootContext.toAsync().get()
                keepaliveThread = null
            }
        }
    }

    @OptIn(Api4J::class)
    override fun destroy() {
        kotlin.runCatching {
            simbootContext.cancelBlocking()
            keepaliveThread?.run {
                interrupt()
                keepaliveThread = null
            }
        }.onFailure {
            it.printStackTrace()
        }

    }

    public companion object {
        private val logger: Logger = LoggerFactory.getLogger(SimbotAppStarter::class)
    }
}
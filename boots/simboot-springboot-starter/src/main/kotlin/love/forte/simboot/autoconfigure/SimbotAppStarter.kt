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

package love.forte.simboot.autoconfigure

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
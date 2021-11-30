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

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.isActive
import love.forte.simbot.component.tencentguild.TencentGuildBot
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildBotImpl(

) : TencentGuildBot() {
    // 0 init
    // 1 start
    // 2 cancel
    private val activeStatus = AtomicInteger(0)

    override suspend fun start(): Boolean = sourceBot.start().also {
        activeStatus.compareAndSet(0, 1)
    }

    override suspend fun join() {
        sourceBot.join()
    }

    override suspend fun cancel(): Boolean = sourceBot.cancel().also {
        activeStatus.set(2)
    }

    override val isStarted: Boolean
        get() = activeStatus.get() >= 1

    override val isCancelled: Boolean
        get() = activeStatus.get() == 2
}
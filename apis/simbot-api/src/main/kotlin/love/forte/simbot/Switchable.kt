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
 *
 */

package love.forte.simbot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import love.forte.simbot.utils.runInBlocking

/**
 *
 * 可开关的。
 *
 * 此接口提供 `start`、`cancel` 等相关开始、关闭的操作。
 *
 * 对于开关操作，在执行了 [cancel] 之后将不允许二次开启。
 *
 * @author ForteScarlet
 */
public interface Switchable : CoroutineScope {

    /**
     * 启动操作.
     * @return 从未启动且尚未关闭的情况下启动成功则返回true。
     */
    @JvmSynthetic
    public suspend fun start(): Boolean

    @Api4J
    @Throws(InterruptedException::class)
    public fun startBlocking(): Boolean = runInBlocking { start() }

    @Api4J
    public fun startAsync() {
        launch { start() }
    }

    /**
     * 关闭操作.
     *
     * @return 尚未关闭且关闭成功时返回true。
     */
    @JvmSynthetic
    public suspend fun cancel(reason: Throwable? = null): Boolean

    @Api4J
    @Throws(InterruptedException::class)
    public fun cancelBlocking(reason: Throwable?): Boolean = runInBlocking { cancel(reason) }

    @Api4J
    @Throws(InterruptedException::class)
    public fun cancelBlocking(): Boolean = runInBlocking { cancel() }

    @Api4J
    public fun cancelAsync() {
        launch { cancel() }
    }

    @Api4J
    public fun cancelAsync(reason: Throwable?) {
        launch { cancel(reason) }
    }



    /**
     * 是否已经启动过了。
     */
    public val isStarted: Boolean

    /**
     * 是否正在运行，即启动后尚未关闭。
     */
    public val isActive: Boolean

    /**
     * 是否已经被取消。
     */
    public val isCancelled: Boolean

}
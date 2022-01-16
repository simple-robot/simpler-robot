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

package love.forte.simbot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
    public fun startBlocking(): Boolean = runBlocking { start() }

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
    public fun cancelBlocking(reason: Throwable?): Boolean = runBlocking { cancel(reason) }

    @Api4J
    public fun cancelBlocking(): Boolean = runBlocking { cancel() }

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
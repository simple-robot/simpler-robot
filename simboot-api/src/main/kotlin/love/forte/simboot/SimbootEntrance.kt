/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot

import kotlinx.coroutines.CompletionHandler
import love.forte.simbot.ability.Survivable
import org.slf4j.Logger

/**
 *
 * Boot下的入口接口。入口接收一个任意类型的参数和args参数集，并在一系列的流程后得到一个最终的结果 [SimbootContext].
 *
 * 对于参数的类型的判断与使用，以及最终返回值的实现细节，将全权交由实现者自行决断。
 *
 * @author ForteScarlet
 */
public interface SimbootEntrance {

    /**
     * 提供一个可能的启动器实例和启动参数，通过一系列流程执行后得到最终的 [SimbootContext].
     */
    public fun run(context: SimbootEntranceContext): SimbootContext

}


/**
 * 为 [SimbootEntrance.run] 提供参数的参数。
 */
public interface SimbootEntranceContext {
    /**
     * 调用者所提供的启动参数。
     *
     * 对于参数的类型的判断与使用，以及最终返回值的实现细节，将全权交由实现者自行决断。
     */
    public val application: Any?

    /**
     * 启动命令参数。
     */
    public val args: Array<String>

    /**
     * 由boot所提供的日志。
     */
    public val logger: Logger
}


/**
 * Boot启动流程结束后得到的最终结果。
 */
public interface SimbootContext : Survivable {

    /**
     * 挂起context直至其被彻底终止。
     */
    @JvmSynthetic
    override suspend fun join()

    /**
     * 终止simbot程序。
     */
    @JvmSynthetic
    override suspend fun cancel(reason: Throwable?): Boolean


    /**
     * 注册一个在simbot程序结束后执行逻辑.
     */
    override fun invokeOnCompletion(handler: CompletionHandler)


}

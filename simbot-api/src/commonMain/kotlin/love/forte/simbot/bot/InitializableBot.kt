/*
 *     Copyright (c) 2025. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.bot

import love.forte.simbot.suspendrunner.ST


/**
 * 一个可初始化的 [Bot]，使其用于**延迟初始化**的能力。
 * 实现了 [InitializableBot] 的 Bot 类型约定，在 [init] 之前，
 * 可以继续修改 Bot 中的一些可修改信息，例如Bot所提供的配置类等。
 *
 * ## 特殊含义
 * [ConfigurableBot] 具有特殊含义，可能会在部分场景被判断并有特殊作用。
 *
 * - 在 Spring Boot 中，
 *
 * @since 4.13.0
 *
 * @author ForteScarlet
 */
public interface InitializableBot : ConfigurableBot {

    /**
     * 初始化当前的 bot。当初始化完成后，[isInitialized] 将会得到 `true`,
     * 且 [configuration] 中的属性不应再被修改。
     *
     * [init] 同样会在 [start] 的过程中被自动初始化，因此不需要手动在 [start] 之前调用 [init]。
     * 你只需要在只需要初始化而不启动 bot 的情况下主动调用 [init]。
     *
     * [init] 可以被重复调用，但最终效果是一致的 —— bot只会被初始化一次。
     * 当初始化完成后，后续的其他调用会直接返回 `false`。[init] 内部需通过锁或其他手段来保证这一点。
     * 当 [init] 尚在初始化过程中持有锁时，[isInitializing] 将会得到 `true`。
     *
     * @return 如果本次初始化成功，则得到 `true`, 如果已经初始化过了，则得到 `false`。
     */
    @ST
    public suspend fun init(): Boolean

    /**
     * 当前 bot 是否已经完成初始化。
     */
    public val isInitialized: Boolean

    /**
     * 是否正在初始化。
     * 当 [isInitialized] 为 `false` 且 [init] 内部正在进行初始化并持有锁时，
     * [isInitializing] 得到 `false`。
     */
    public val isInitializing: Boolean
}

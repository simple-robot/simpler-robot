/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.bot

import kotlinx.serialization.Serializable
import love.forte.simbot.kook.stdlib.BotConfiguration

/**
 * KOOK 组件中对 [KookBot] 的配置信息。
 *
 * @author ForteScarlet
 */
public class KookBotConfiguration(
    /**
     * 用于提供给标准库 [love.forte.simbot.kook.stdlib.Bot] 的bot配置信息
     */
    public var botConfiguration: BotConfiguration
) {

    /**
     * 内部缓存对象信息的同步周期。
     *
     * 同步用于防止出现根据事件进行对象调整时出现遗漏而导致出现数据不对等的情况。同步会做如下操作：
     * - 如果当前缓存中不存在，但实际存在的实例，补充添加。
     * - 如果出现当前缓存存在，但实际不存在的实例，终止内部所有任务并移除。
     *
     */
    public var syncPeriods: SyncPeriods = DEFAULT_SYNC_PERIODS

    /**
     * 内部缓存对象信息的同步周期信息。
     *
     * 周期内各个不同的任务独立执行，如果需要自定义同步周期，请同时考虑api是否可能达到调用频率上限。
     */
    @Serializable
    public data class SyncPeriods(
        val guild: GuildSyncPeriod = GuildSyncPeriod()
    )

    /**
     * 对频道服务器进行同步的周期信息。
     */
    @Serializable
    public data class GuildSyncPeriod(
        /**
         * 对频道服务器进行同步的周期，单位毫秒。
         * 目前服务器同步的同时会去同步此服务器下的所有频道列表与成员列表。
         */
        val syncPeriod: Long = 180_000L,


        /**
         * 同步数据是分页分批次的同步。[batchDelay] 配置每批次后进行挂起等待的时间，单位毫秒。
         *
         * 可以通过调大此参数来减缓api的请求速率, 默认不等待。
         */
        public val batchDelay: Long = 0L
    )


    public companion object {
        internal val DEFAULT_SYNC_PERIODS = SyncPeriods(GuildSyncPeriod())
    }
}

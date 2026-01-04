/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.api

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import love.forte.simbot.component.onebot.v11.common.api.StatusResult
import kotlin.jvm.JvmStatic

/**
 * [`get_status`-获取运行状态](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_status-获取运行状态)
 *
 * @author ForteScarlet
 */
public class GetStatusApi private constructor() : OneBotApi<StatusResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<StatusResult>
        get() = StatusResult.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<StatusResult>>
        get() = RES_SER

    override val body: Any?
        get() = null

    public companion object Factory {
        private const val ACTION: String = "get_status"

        private val RES_SER: KSerializer<OneBotApiResult<StatusResult>> =
            OneBotApiResult.serializer(StatusResult.serializer())

        private val INSTANCE: GetStatusApi = GetStatusApi()

        /**
         * 构建一个 [GetStatusApi].
         */
        @JvmStatic
        public fun create(): GetStatusApi = INSTANCE
    }
}

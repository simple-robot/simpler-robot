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
import kotlinx.serialization.builtins.serializer
import kotlin.jvm.JvmStatic

/**
 * [`clean_cache`-清理缓存](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#clean_cache-清理缓存)
 *
 * @author ForteScarlet
 */
public class CleanCacheApi private constructor() : OneBotApi<Unit> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<Unit>>
        get() = OneBotApiResult.emptySerializer()

    override val body: Any?
        get() = null

    public companion object Factory {
        private const val ACTION: String = "clean_cache"

        private val INSTANCE: CleanCacheApi = CleanCacheApi()

        /**
         * 构建一个 [CleanCacheApi].
         */
        @JvmStatic
        public fun create(): CleanCacheApi = INSTANCE
    }
}

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

package love.forte.simbot.kook.api

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.GetGatewayApi.*
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmStatic

/**
 * [获取网关连接地址](https://developer.kaiheila.cn/doc/http/gateway)。
 *
 * @see Compress
 * @see NotCompress
 * @see Resume
 *
 * @author ForteScarlet
 */
public sealed class GetGatewayApi(private val isCompress: Boolean) : KookGetApi<Gateway>() {
    public companion object Factory {
        private val PATH = ApiPath.create("gateway", "index")

        /**
         * 根据 [isCompress] 构造 [GetGatewayApi]。
         *
         * @param isCompress 是否压缩数据
         *
         * @see Compress
         * @see NotCompress
         */
        @JvmStatic
        public fun create(isCompress: Boolean): GetGatewayApi = if (isCompress) Compress else NotCompress


        /**
         * 用于进行重连恢复的API。
         *
         * @param isCompress 是否压缩数据
         */
        @JvmStatic
        public fun resume(isCompress: Boolean, sn: Long, sessionId: String): Resume =
            Resume(isCompress, sn, sessionId)

        /**
         * 根据一个 [GetGatewayApi] 的信息获得一个 [Resume]。
         * 这个 [Resume] 的 `isCompress` 与目标一致。
         */
        @JvmStatic
        public fun GetGatewayApi.resume(sn: Long, sessionId: String): Resume {
            return when (this) {
                Compress -> Resume(true, sn, sessionId)
                NotCompress -> Resume(false, sn, sessionId)
                // Resume
                else -> Resume(isCompress, sn, sessionId)
            }
        }

    }

    override val apiPath: ApiPath get() = PATH
    override val resultDeserializationStrategy: DeserializationStrategy<Gateway> get() = Gateway.serializer()

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters.append("compress", if (isCompress) "1" else "0")
    }

    /**
     * 压缩数据
     */
    public data object Compress : GetGatewayApi(true)

    /**
     * 不进行数据压缩
     */
    public data object NotCompress : GetGatewayApi(false)

    /**
     * 用于内部重连恢复的 [Resume] API。
     *
     * @see Factory.resume
     */
    public class Resume internal constructor(isCompress: Boolean, private val sn: Long, private val sessionId: String) :
        GetGatewayApi(isCompress) {
        override fun urlBuild(builder: URLBuilder) {
            super.urlBuild(builder)
            builder.parameters {
                append("resume", "1")
                append("sn", sn.toString())
                append("session_id", sessionId)
            }

        }
    }
}


/**
 * 通过 [GetGatewayApi] 得到的网关信息。
 */
@Serializable
public data class Gateway @ApiResultType constructor(val url: String)

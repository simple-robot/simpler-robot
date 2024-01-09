/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.bot.configuration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 得到一个与 [Ktor proxy](https://ktor.io/docs/proxy.html#http_proxy) 类似的配置结果的配置类。
 *
 * @author ForteScarlet
 */
@Serializable
public sealed class ProxyConfiguration {
    /**
     * 得到配置结果
     */
    public abstract val value: ProxyValue

    /**
     * Http 代理
     */
    @Serializable
    @SerialName("http")
    public data class Http(val url: String) : ProxyConfiguration() {
        override val value: ProxyValue.Http
            get() = ProxyValue.Http(url)
    }

    /**
     * socks 代理
     */
    @Serializable
    @SerialName("socks")
    public data class Socks(val host: String, val port: Int) : ProxyConfiguration() {
        override val value: ProxyValue.Socks
            get() = ProxyValue.Socks(host, port)
    }
}


/**
 * [ProxyConfiguration] 的配置结果类型。
 *
 * @see ProxyConfiguration
 */
public sealed class ProxyValue {
    /**
     * HTTP proxy
     */
    public data class Http(val url: String) : ProxyValue()

    /**
     * socks proxy.
     */
    public data class Socks(val host: String, val port: Int) : ProxyValue()
}

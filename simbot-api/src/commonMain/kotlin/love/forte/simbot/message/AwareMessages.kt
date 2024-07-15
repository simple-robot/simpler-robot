/*
 *     Copyright (c) 2024. ForteScarlet.
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

@file:JvmName("StandardMessages")
@file:JvmMultifileClass

package love.forte.simbot.message

import love.forte.simbot.suspendrunner.STP
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


/**
 * 表示为一个可以得知 URL 地址的消息元素，
 * 例如 [UrlAwareImage]。
 * 应当由某个 [Message.Element] 的子类型实现。
 *
 * [UrlAwareMessage] 主要用于那些从服务端返回的消息元素使用，
 * 而不是本地构建的消息元素。
 *
 * @since 4.3.0
 */
public interface UrlAwareMessage {
    /**
     * 获取到链接字符串。
     * 如果链接信息包含在响应数据中，则会立即返回，
     * 否则会挂起并查询链接信息（例如通过网络接口查询）。
     * 如果需要查询，其内部不会缓存结果，因此每次调用 [url]
     * 均会产生挂起与查询行为。
     *
     * @throws IllegalStateException 如果当前状态无法查询信息，
     * 比如由于消息元素的序列化导致某些认证信息丢失。
     * @throws RuntimeException 在获取过程中可能产生的任何异常，
     * 比如网络请求问题、权限问题等。
     * JVM中的受检异常应当被包装为非受检异常。具体其他可能的异常请参考具体实现说明。
     */
    @STP
    public suspend fun url(): String
}

/**
 * 表示一个可以获取到其二进制数据的消息元素，
 * 例如某种图片消息或文件消息。
 * 应当由某个 [Message.Element] 的子类型实现。
 *
 * [BinaryDataAwareMessage] 主要用于那些从服务端返回的消息元素使用，
 * 而不是本地构建的消息元素。
 *
 * 注意：如果文件很大，则操作可能会比较耗时。
 *
 * @since 4.3.0
 */
public interface BinaryDataAwareMessage {
    /**
     * 获取到二进制数据。当需要进行网络请求才可得到内容时，会挂起。
     *
     * @throws IllegalStateException 如果当前状态无法读取数据，
     * 比如由于消息元素的序列化导致某些认证信息丢失。
     * @throws RuntimeException 在获取过程中可能产生的任何异常，
     * 比如网络请求问题、权限问题等。
     * JVM中的受检异常应当被包装为非受检异常。具体其他可能的异常请参考具体实现说明。
     */
    @STP
    public suspend fun binaryData(): ByteArray
}

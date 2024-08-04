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

package love.forte.simbot.message

import love.forte.simbot.ability.DeleteFailureException
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.common.id.ID
import love.forte.simbot.suspendrunner.STP
import kotlin.jvm.JvmSynthetic


/**
 * 一个消息本体内容。通常是通过一个事件接收到的消息本体。
 *
 * ## [DeleteSupport]
 *
 * [MessageContent] 实现 [DeleteSupport]，提供可能支持的删除行为。更多说明参考 [MessageContent.delete]
 *
 * @author ForteScarlet
 */
public interface MessageContent : DeleteSupport {
    /**
     * 消息的ID。
     *
     * 如果消息来源（例如推送的事件）支持消息ID，
     * 则此处即为原始的消息ID；如果来源不支持或不存在ID，则此处可能为一个随机ID。
     */
    public val id: ID

    /**
     * 消息本体中完整消息的消息链。
     * [messages] 中的所有元素都是可离线构造的，换言之都是直接通过事件本体解析而来的。
     * 如果存在某些例如必须进行网络查询才能得知的消息元素，则不会被包含在 [messages] 中。
     *
     * 如果想要获取消息引用信息，也可参考 [reference]。
     *
     * @see reference
     */
    public val messages: Messages

    /**
     * 消息本体中提取出的所有 [文本消息][PlainText] 的合并结果。
     *
     * 类似于:
     * ```kotlin
     * messages.filterIsInstance<PlainText>().joinToString { it.text }
     * ```
     *
     * 但是如果 [messages] 没有任何 [PlainText] 类型的消息元素，则会得到 `null`。
     *
     * @see messages
     *
     */
    public val plainText: String?

    /**
     * 删除、撤回、撤销此消息。
     *
     * 如果实现者不支持这类API，则会抛出 [UnsupportedOperationException]。
     * 如果删除过程中由于权限、不存在等各种业务原因导致的删除失败，则应抛出异常，
     * 并建议使用或扩展 [DeleteFailureException] 异常类型。
     *
     * @param options 删除时的可选选项。不支持的选项将会被忽略。更多说明参考 [DeleteOption]。
     *
     * @throws UnsupportedOperationException API本身不被实现者支持
     * @throws DeleteFailureException 删除行为失败
     * @throws NoSuchElementException 没有可删除目标
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption)

    /**
     * 获取当前消息内容中有关 [消息引用][MessageReference] 的消息元素。
     *
     * [reference] 在明确不支持或直接通过 [messages] 寻找获取时，不会发生挂起。
     * 否则当需要通过网络查询结果时会产生挂起。
     *
     * [reference] 所得结果**不一定**是 [messages] 中的元素。
     * 如上所述，如果需要通过网络查询才能得到结果，则 [reference] 的结果不会包含在 [messages] 中。
     *
     * - 如果实现者尚未针对性地实现此API，则默认逻辑为：
     * 从 [messages] 中寻找第一个类型为 [MessageReference] 的元素。
     * - 如果实现者的所属平台不存在、不支持 _消息引用_ 的概念，则可能始终得到 `null`。
     * - 如果实现者的所属平台有明确的 _消息引用_ 概念，但是无法通过 [MessageReference] 这个类型进行表述，
     * 则使用 [reference] 时应当抛出信息明确的 [UnsupportedOperationException] 异常。
     *
     * @throws UnsupportedOperationException 如果实现者的所属平台有明确的 _消息引用_ 概念，
     * 但是无法通过 [MessageReference] 这个类型进行表述
     *
     * @since 4.5.0
     */
    @STP
    public suspend fun reference(): MessageReference? =
        messages.firstOrNull { it is MessageReference } as? MessageReference?

}

/**
 * 如果 [MessageContent.plainText] 为 `null`，则以空字符串 `""` 替代之。
 * @see MessageContent.plainText
 */
public inline val MessageContent.safePlainText: String get() = plainText ?: ""

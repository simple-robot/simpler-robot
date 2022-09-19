/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.action

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking


/**
 * 允许一种删除行为。
 * 标记一个消息为可删除的。
 *
 * 对于一种**删除行为**来讲，它最常见的含义就是**撤回**（针对于消息, 参考 [RemoteMessageContent][love.forte.simbot.message.RemoteMessageContent]、[MessageReceipt][love.forte.simbot.message.MessageReceipt]）
 * 和 **踢出/移除**（针对于好友、群成员等, 但是没有提供默认实现）。
 *
 * @see love.forte.simbot.message.RemoteMessageContent
 *
 * @author ForteScarlet
 */
@JvmBlocking
@JvmAsync
public interface DeleteSupport {

    /**
     * 删除当前目标。
     *
     * 如果因为组件自身特性而导致任何条件都无法满足任何对象的 `delete` 操作，
     * 则可能固定返回 `false`, 否则大多数情况下会返回 `true`.
     *
     * 如果是因为诸如权限、超时等限制条件导致的无法删除，则可能会抛出相应的异常。
     *
     * @return 在支持的情况下代表是否删除成功，不支持的情况下可能恒返回 `false`。
     */
    public suspend fun delete(): Boolean
}

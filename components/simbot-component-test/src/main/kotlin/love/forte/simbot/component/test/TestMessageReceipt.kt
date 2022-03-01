/*
 *  Copyright (c) 2022-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x、simbot 3.x、simbot3) 的一部分。
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

package love.forte.simbot.component.test

import love.forte.simbot.ID
import love.forte.simbot.action.MessageReactReceipt
import love.forte.simbot.action.MessageReplyReceipt
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.randomID


/**
 * test组件中对于 [MessageReceipt] 的基础实现。
 * @author ForteScarlet
 */
public class TestMessageReceipt(override val id: ID = randomID(), override val isSuccess: Boolean = true) :
    MessageReceipt

/**
 * test组件中对于 [MessageReactReceipt] 的基础实现。
 * @author ForteScarlet
 */
public class TestMessageReactReceipt(override val id: ID = randomID(), override val isSuccess: Boolean = true) :
    MessageReactReceipt

/**
 * test组件中对于 [MessageReplyReceipt] 的基础实现。
 * @author ForteScarlet
 */
public class TestMessageReplyReceipt(
    override val id: ID = randomID(), override val isSuccess: Boolean = true,
    override val isReplySuccess: Boolean = true
) : MessageReplyReceipt
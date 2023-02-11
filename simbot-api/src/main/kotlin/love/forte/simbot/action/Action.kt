/*
 * Copyright (c) 2021-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simbot.action

import love.forte.simbot.SimbotRuntimeException
import love.forte.simbot.action.ActionType.PASSIVE
import love.forte.simbot.action.ActionType.PROACTIVE


/**
 * 一个行为的类型。行为不论形式，都分为主动行为与被动行为。
 *
 * 判定主动与被动一般可以通过这个行为能否随时随地进行主动发起来判断。
 *
 * 例如主动向好友发送一个消息的行为就是 [主动的][PROACTIVE]行为，而只有通过一次事件才能进行 *回复* 的行为是 [被动的][PASSIVE].
 *
 */
public enum class ActionType {
    /** 主动的 */
    PROACTIVE,
    
    /** 被动的 */
    PASSIVE
}


/**
 * 一个行为异常。
 */
public open class ActionException : SimbotRuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

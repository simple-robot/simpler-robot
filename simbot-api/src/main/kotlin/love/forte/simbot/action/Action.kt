/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
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

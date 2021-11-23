/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.event

import love.forte.simbot.action.Action
import love.forte.simbot.action.ActionType

/**
 * 此接口标记为一个api提供的常见 [RequestEvent.Action] 场景事件。
 * 情景下的
 */
public sealed interface StandardRequestAction : Action {
    /**
     * 对于 [请求事件][RequestEvent] 所需的 [行为][RequestEvent.Action],
     * 一般来讲都应该是被动的。
     */
    override val actionType: ActionType get() = ActionType.PASSIVE
}

/**
 * 为本次行为提供一个 [备注][remark]。
 */
public interface RemarkSupportAction : StandardRequestAction {
    public val remark: String
}

/**
 * 为本次行为提供一个 [原因][reason]。
 */
public interface ReasonSupportAction : StandardRequestAction {

    /**
     * 本次行为的原因。
     */
    public val reason: String
}

/**
 * 为本次行为提供一个 [封锁][blocking] 选项。
 */
public interface BlockingSupportAction : StandardRequestAction {
    public val blocking: Boolean
}





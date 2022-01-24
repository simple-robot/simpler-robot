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
 *
 */

package love.forte.simbot.action

import love.forte.simbot.Api4J
import love.forte.simbot.SimbotRuntimeException
import love.forte.simbot.action.ActionType.PASSIVE
import love.forte.simbot.action.ActionType.PROACTIVE


/**
 * 一个 **行为动作**。
 *
 * 不论是对于 [love.forte.simbot.Bot], [组织][love.forte.simbot.definition.Organization] 还是 [用户][love.forte.simbot.definition.User] 等，
 *
 * 尽管他们在不同组件中的能力与含义有所不同，但是它们总会存在一部分相似的能力与行为, 例如对于一个群成员，
 * 在权限足够的情况下，有很多平台都允许对其进行禁言或踢出。
 *
 * [Action] 对这些常见的行为进行描述，假若组件存在支持的相似功能，优先考虑实现相关接口。
 *
 * [Action] 应当完全有组件实现者实现，并提供所有对应类型的预先定义。
 *
 * @see ActionReceipt
 *
 * @author ForteScarlet
 */
public interface Action<S, T> {

    // delete?

}


// delete?
public interface ActionSupport {

    /**
     * 执行一个行为。
     */
    @JvmSynthetic
    public fun <S, T>
            action(actionType: Action<S, T>, action: suspend (S) -> T): ActionReceipt


    @Api4J
    public fun <S, T> doActionBlocking(
        actionType: Action<S, T>, actionBlock: (S) -> T
    ): ActionReceipt =
        action(actionType) { s -> actionBlock(s) }

}


/**
 * 一个行为回执。
 * 行为回执出现在一些与行为相关的地方，并描述本次行为所得到的一次结果。
 * 行为回执最基本的能力是描述本次行为 [是否成功][isSuccess]。
 *
 * 存在 [isSuccess] 但是这不意味着 [ActionReceipt] 会对任何异常进行处理。对于 [isSuccess] 的可能性，
 * 通常情况下仅代表合法情况下的执行结果，不代表异常情况下的执行结果。
 *
 * 假如对应行为过程中出现了非法操作等非正常流程行为，应当直接抛出对应异常。
 *
 *
 * 对于此接口的其他实现或继承，如果仅仅描述 [isSuccess], 考虑提供默认实现减少对象实例。
 *
 */
public interface ActionReceipt {

    /**
     * 正常流程下此次行为是否成功。
     */
    public val isSuccess: Boolean
}


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
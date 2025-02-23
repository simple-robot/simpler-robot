/*
 *     Copyright (c) 2025. ForteScarlet.
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

package love.forte.simbot.event


/**
 * 一个内部事件。
 * 用于表示一个仅在内部流转、与外界无关的事件，
 * 通常用于一些内部的状态通知或功能拦截，例如 [BotStageEvent] 或 [SendSupportInteractionEvent] 等。
 *
 * 通常由组件实现进行扩展，不过也提供了一些默认的定义。
 *
 * @see InternalNotificationEvent
 * @see InternalInterceptionEvent
 * @see BotStageEvent
 * @see SendSupportInteractionEvent
 *
 * @since 4.11.0
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface InternalEvent : Event

/**
 * 一个内部通知事件。
 * 通知性质的内部事件通常仅用作“通知”，即它不会对某些行为造成影响。
 *
 * @since 4.11.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface InternalNotificationEvent : InternalEvent

/**
 * 一个内部拦截事件。
 * 拦截性质的内部事件通常用作“拦截”，即它会对某些行为进行拦截，并有可能会产生影响，
 * 例如改变原本行为的参数、或者通过抛出异常直接阻止某些行为的发生。
 * 例如针对 [SendSupport.send][love.forte.simbot.ability.SendSupport.send]
 * 进行拦截，并改变其入参。
 *
 * ## 异常处理
 *
 * 在拦截过程中，如果产生了异常，它们不会影响后续事件的继续调度，
 * 而所有产生的异常都会被收集到 [InternalInterceptionException.suppressedExceptions] 中，
 * 并在最终抛出并影响原函数的执行。
 *
 * @since 4.11.0
 *
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface InternalInterceptionEvent : InternalEvent

/**
 * [InternalInterceptionEvent] 中产生的异常的收集与包装。
 * 更多参考 [InternalInterceptionEvent] 中有关异常的说明。
 *
 * @see InternalInterceptionEvent
 * @since 4.11.0
 */
public open class InternalInterceptionException : RuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

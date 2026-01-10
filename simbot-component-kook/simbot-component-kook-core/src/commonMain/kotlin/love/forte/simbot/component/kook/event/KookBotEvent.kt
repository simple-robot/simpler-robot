/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.event

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.event.BotEvent
import love.forte.simbot.event.Event
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.kook.event.EventExtra
import love.forte.simbot.kook.event.SystemExtra
import love.forte.simbot.kook.event.Event as KEvent

/**
 * Kook 组件的事件类型基类。
 *
 * @see UnsupportedKookEvent
 * @see KookBotEvent
 * @see KookSystemEvent
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public abstract class KookEvent : Event {
    override fun toString(): String {
        return "KookEvent(id=$id, time=$time)"
    }
}

/**
 * [KookEvent] 下实现 [BotEvent] 的基础类型。
 *
 * @see UnsupportedKookEvent
 *
 * @param E Kook api模块中所定义的原始 Kook 事件对象 [KEvent].
 * @param EX Kook api模块中所定义的原始 Kook 事件对象的 [extra][EventExtra] 属性类型。
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public abstract class KookBotEvent<out EX : EventExtra, out E : KEvent<EX>> : KookEvent(), BotEvent {
    /**
     * 此事件对应的bot示例。
     */
    abstract override val bot: KookBot

    /**
     * 当前事件内部对应的原始事件实体。
     */
    public abstract val sourceEvent: E

    /**
     * 当前事件对应的原始事件JSON字符串。
     */
    public abstract val sourceEventRaw: String

    override fun toString(): String {
        return "KookBotEvent(type=${sourceEvent.type}, channelType=${sourceEvent.channelType}, source=$sourceEvent)"
    }
}

/**
 * [KookBotEvent] 的 **系统事件** 相关的事件基类。
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public abstract class KookSystemEvent :
    KookBotEvent<SystemExtra, KEvent<SystemExtra>>() {
    override val id: ID
        get() = sourceEvent.msgId.ID

    /**
     * [sourceEvent] 中的 `extra.body` 信息。
     *
     * 不同的系统事件的 body 类型可能是截然不同的。
     * [sourceBody] 的具体类型由具体的实现类重写定义。
     *
     */
    public abstract val sourceBody: Any?

    override fun toString(): String {
        return "KookSystemEvent(type=${sourceEvent.type}, channelType=${sourceEvent.channelType}, source=$sourceEvent)"
    }
}

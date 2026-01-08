/*
 * Copyright (c) 2025. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.event

import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.InternalMessageInteractionEvent
import love.forte.simbot.event.InternalMessagePostSendEvent
import love.forte.simbot.event.InternalMessagePreSendEvent

/**
 * QQ组件中针对 [InternalMessageInteractionEvent] 的子类型实现。
 *
 * @author ForteScarlet
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGInternalMessageInteractionEvent : InternalMessageInteractionEvent, QGInternalEvent

/**
 * QQ组件中针对消息前置拦截事件的子类型实现。
 * @author ForteScarlet
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGInternalMessagePreSendEvent : QGInternalMessageInteractionEvent, InternalMessagePreSendEvent

/**
 * QQ组件中针对消息后置通知事件的子类型实现。
 * @author ForteScarlet
 * @since 4.2.0
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGInternalMessagePostSendEvent : QGInternalMessageInteractionEvent, InternalMessagePostSendEvent

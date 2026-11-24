/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.message

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.message.Message
import love.forte.simbot.suspendrunner.asPublisher
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.reserve.suspendReserve
import love.forte.simbot.suspendrunner.runInAsync
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 将 [Message] 解析为一用于API请求的 [OneBotMessageSegment] 列表。
 *
 * @see resolveToOneBotSegmentList
 */
@InternalOneBotAPI
@Api4J
public fun Message.resolveToOneBotSegmentListBlocking(): List<OneBotMessageSegment> =
    runInNoScopeBlocking { resolveToOneBotSegmentList() }

/**
 * 将 [Message] 解析为一用于API请求的 [OneBotMessageSegment] 列表。
 *
 * @see resolveToOneBotSegmentList
 */
@OptIn(InternalSimbotAPI::class)
@InternalOneBotAPI
@Api4J
public fun Message.resolveToOneBotSegmentListAsync(): CompletableFuture<out List<OneBotMessageSegment>> =
    runInAsync { resolveToOneBotSegmentList() }

/**
 * 将 [Message] 解析为一用于API请求的 [OneBotMessageSegment] 列表。
 *
 * @see resolveToOneBotSegmentList
 */
@OptIn(InternalSimbotAPI::class, DelicateCoroutinesApi::class)
@InternalOneBotAPI
@Api4J
public fun Message.resolveToOneBotSegmentListReserve(): SuspendReserve<List<OneBotMessageSegment>> =
    suspendReserve(
        GlobalScope,
        EmptyCoroutineContext
    ) { resolveToOneBotSegmentList() }

/**
 * 将一个 [Message.Element] 转化为用于API请求的 [OneBotMessageSegment]。
 * @see resolveToOneBotSegment
 */
@InternalOneBotAPI
@Api4J
public fun Message.Element.resolveToOneBotSegmentBlocking(): OneBotMessageSegment? =
    runInNoScopeBlocking { resolveToOneBotSegment() }

/**
 * 将一个 [Message.Element] 转化为用于API请求的 [OneBotMessageSegment]。
 * @see resolveToOneBotSegment
 */
@OptIn(InternalSimbotAPI::class)
@InternalOneBotAPI
@Api4J
public fun Message.Element.resolveToOneBotSegmentAsync(): CompletableFuture<out OneBotMessageSegment?> =
    runInAsync { resolveToOneBotSegment() }

/**
 * 将一个 [Message.Element] 转化为用于API请求的 [OneBotMessageSegment]。
 * @see resolveToOneBotSegment
 */
@OptIn(DelicateCoroutinesApi::class, InternalSimbotAPI::class)
@InternalOneBotAPI
@Api4J
public fun Message.Element.resolveToOneBotSegmentReserve(): SuspendReserve<OneBotMessageSegment?> =
    suspendReserve(
        GlobalScope,
        EmptyCoroutineContext
    ) { resolveToOneBotSegment() }

/**
 * 订阅时将消息解析为 OneBot API 使用的消息段列表。
 *
 * @see resolveToOneBotSegmentList
 * @since 5.0
 */
@InternalOneBotAPI
@Api4J
@ExperimentalReactiveBridgingApi
@OptIn(InternalSimbotAPI::class)
public fun Message.resolveToOneBotSegmentListReactive(): Publisher<List<OneBotMessageSegment>> =
    asPublisher { resolveToOneBotSegmentList() }

/**
 * 订阅时将消息元素解析为 OneBot API 使用的消息段。元素无法转换时，订阅直接完成。
 *
 * @see resolveToOneBotSegment
 * @since 5.0
 */
@InternalOneBotAPI
@Api4J
@ExperimentalReactiveBridgingApi
@OptIn(InternalSimbotAPI::class)
public fun Message.Element.resolveToOneBotSegmentReactive(): Publisher<OneBotMessageSegment> =
    asPublisher { resolveToOneBotSegment() }

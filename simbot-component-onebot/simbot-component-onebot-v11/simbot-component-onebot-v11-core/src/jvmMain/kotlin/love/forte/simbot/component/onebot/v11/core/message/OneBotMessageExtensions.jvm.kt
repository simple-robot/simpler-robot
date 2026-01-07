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
@file:JvmName("OneBotMessageExtensions")
@file:JvmMultifileClass

package love.forte.simbot.component.onebot.v11.core.message

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.component.onebot.v11.core.api.GetImageApi
import love.forte.simbot.component.onebot.v11.core.api.GetImageResult
import love.forte.simbot.component.onebot.v11.core.api.GetRecordApi
import love.forte.simbot.component.onebot.v11.core.api.GetRecordResult
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.message.segment.OneBotImage
import love.forte.simbot.component.onebot.v11.message.segment.OneBotRecord
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.reserve.suspendReserve
import love.forte.simbot.suspendrunner.runInAsync
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 通过 [bot] 使用 [GetImageApi] 查询 [OneBotImage]
 * 中的图片地址。
 *
 * @see getImageInfo
 * @throws Throwable 任何请求API可能产生的异常
 */
@Api4J
public fun OneBotImage.getImageInfoBlocking(bot: OneBotBot): GetImageResult =
    runInNoScopeBlocking { getImageInfo(bot) }

/**
 * 通过 [bot] 使用 [GetImageApi] 查询 [OneBotImage]
 * 中的图片地址。
 *
 * @see getImageInfo
 * @throws Throwable 任何请求API可能产生的异常
 */
@Api4J
public fun OneBotImage.getImageInfoAsync(
    bot: OneBotBot,
    scope: CoroutineScope? = null,
    context: CoroutineContext? = null
): CompletableFuture<out GetImageResult> =
    runInAsync(scope ?: bot, context ?: EmptyCoroutineContext) {
        getImageInfo(bot)
    }

/**
 * 通过 [bot] 使用 [GetImageApi] 查询 [OneBotImage]
 * 中的图片地址。
 *
 * @see getImageInfo
 * @throws Throwable 任何请求API可能产生的异常
 */
@Api4J
@JvmOverloads
public fun OneBotImage.getImageInfoReserve(
    bot: OneBotBot,
    scope: CoroutineScope? = null,
    context: CoroutineContext? = null
): SuspendReserve<GetImageResult> =
    suspendReserve(scope ?: bot, context ?: EmptyCoroutineContext) {
        getImageInfo(bot)
    }

/**
 * 通过 [bot] 使用 [GetImageApi] 查询 [OneBotImage.Element.segment]
 * 中的图片地址。
 *
 * @throws Throwable 任何请求API可能产生的异常
 */
@Api4J
public fun OneBotImage.Element.getImageInfoBlocking(bot: OneBotBot): GetImageResult =
    runInNoScopeBlocking { getImageInfo(bot) }

/**
 * 通过 [bot] 使用 [GetImageApi] 查询 [OneBotImage.Element.segment]
 * 中的图片地址。
 *
 * @throws Throwable 任何请求API可能产生的异常
 */
@Api4J
@JvmOverloads
public fun OneBotImage.Element.getImageInfoAsync(
    bot: OneBotBot,
    scope: CoroutineScope? = null,
    context: CoroutineContext? = null
): CompletableFuture<out GetImageResult> =
    runInAsync(scope ?: bot, context ?: EmptyCoroutineContext) {
        getImageInfo(bot)
    }

/**
 * 通过 [bot] 使用 [GetImageApi] 查询 [OneBotImage.Element.segment]
 * 中的图片地址。
 *
 * @throws Throwable 任何请求API可能产生的异常
 */
@Api4J
@JvmOverloads
public fun OneBotImage.Element.getImageInfoReserve(
    bot: OneBotBot,
    scope: CoroutineScope? = null,
    context: CoroutineContext? = null
): SuspendReserve<GetImageResult> =
    suspendReserve(scope ?: bot, context ?: EmptyCoroutineContext) {
        getImageInfo(bot)
    }

/**
 * 通过 [bot] 使用 [GetRecordApi] 查询 [OneBotRecord]
 * 中的语音地址。
 *
 * @param outFormat 要转化为的格式。参考 [GetRecordApi]。
 *
 * @throws Throwable 任何请求API可能产生的异常
 */
@Api4J
public fun OneBotRecord.getRecordInfoBlocking(bot: OneBotBot, outFormat: String): GetRecordResult =
    runInNoScopeBlocking { getRecordInfo(bot, outFormat) }

/**
 * 通过 [bot] 使用 [GetRecordApi] 查询 [OneBotRecord]
 * 中的语音地址。
 *
 * @param outFormat 要转化为的格式。参考 [GetRecordApi]。
 *
 * @throws Throwable 任何请求API可能产生的异常
 */
@Api4J
@JvmOverloads
public fun OneBotRecord.getRecordInfoAsync(
    bot: OneBotBot, outFormat: String,
    scope: CoroutineScope? = null,
    context: CoroutineContext? = null
): CompletableFuture<out GetRecordResult> =
    runInAsync(scope ?: bot, context ?: EmptyCoroutineContext) {
        getRecordInfo(bot, outFormat)
    }

/**
 * 通过 [bot] 使用 [GetRecordApi] 查询 [OneBotRecord]
 * 中的语音地址。
 *
 * @param outFormat 要转化为的格式。参考 [GetRecordApi]。
 *
 * @throws Throwable 任何请求API可能产生的异常
 */
@Api4J
@JvmOverloads
public fun OneBotRecord.getRecordInfoReserve(
    bot: OneBotBot, outFormat: String,
    scope: CoroutineScope? = null,
    context: CoroutineContext? = null
): SuspendReserve<GetRecordResult> =
    suspendReserve(scope ?: bot, context ?: EmptyCoroutineContext) {
        getRecordInfo(bot, outFormat)
    }

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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.component.onebot.common.annotations.ExperimentalOneBotAPI
import love.forte.simbot.component.onebot.v11.core.api.*
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.message.segment.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * 通过 [bot] 使用 [GetImageApi] 查询 [OneBotImage]
 * 中的图片地址。
 *
 * @throws Throwable 任何请求API可能产生的异常
 */
@JvmSynthetic
public suspend fun OneBotImage.getImageInfo(bot: OneBotBot): GetImageResult =
    bot.executeData(GetImageApi.create(data.file))

/**
 * 通过 [bot] 使用 [GetImageApi] 查询 [OneBotImage.Element.segment]
 * 中的图片地址。
 *
 * @throws Throwable 任何请求API可能产生的异常
 */
@JvmSynthetic
public suspend fun OneBotImage.Element.getImageInfo(bot: OneBotBot): GetImageResult =
    segment.getImageInfo(bot)

/**
 * 通过 [bot] 使用 [GetRecordApi] 查询 [OneBotRecord]
 * 中的语音地址。
 *
 * @param outFormat 要转化为的格式。参考 [GetRecordApi]。
 *
 * @throws Throwable 任何请求API可能产生的异常
 */
@JvmSynthetic
public suspend fun OneBotRecord.getRecordInfo(bot: OneBotBot, outFormat: String): GetRecordResult =
    bot.executeData(GetRecordApi.create(data.file, outFormat))

/**
 * 通过 [bot] 使用 [GetForwardMsgApi] 查询 [OneBotForward] 内的消息节点列表。
 *
 * @throws Throwable 任何请求API可能产生的异常
 */
@JvmSynthetic
@ExperimentalOneBotAPI
public suspend fun OneBotForward.getForwardNodes(bot: OneBotBot): List<OneBotForwardNode> {
    return bot.executeData(GetForwardMsgApi.create(id)).message
}

/**
 * 通过 [bot] 使用 [GetForwardMsgApi] 查询 [OneBotForward] 内的消息元素。
 *
 * 如果获取到的结果内仍然包含 [OneBotForward]，则继续查询，并将最后的所有结果扁平为 [Flow]
 * 后包装为 [Collectable] 返回。
 *
 * 返回的结果中不会包含 [OneBotForward]。
 *
 * @throws Throwable 收集时可能会产生任何请求API过程中的异常。
 */
@ExperimentalOneBotAPI
public fun OneBotForward.getAllForwardInfo(bot: OneBotBot): Collectable<OneBotMessageSegment> =
    getAllForwardInfoFlow(bot).asCollectable()

private fun OneBotForward.getAllForwardInfoFlow(bot: OneBotBot): Flow<OneBotMessageSegment> {
    return flow {
        val list = getForwardNodes(bot)
        for (note in list) {
            val contents = note.data.content ?: continue
            for (content in contents) {
                if (content !is OneBotForward) {
                    emit(content)
                } else {
                    emitAll(content.getAllForwardInfoFlow(bot))
                }
            }

        }
    }
}

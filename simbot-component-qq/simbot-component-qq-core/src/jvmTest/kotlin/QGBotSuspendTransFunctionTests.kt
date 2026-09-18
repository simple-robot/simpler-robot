/*
 *     Copyright (c) 2026. ForteScarlet.
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

import love.forte.simbot.common.id.ID
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import java.util.concurrent.CompletableFuture
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class QGBotSuspendTransFunctionTests {

    @Test
    fun checkQGBotSTFunctions() {
        /*
            @ST
            public suspend fun sendTo(channelId: ID, text: String): QGMessageReceipt
         */
        with(QGBot::class.java.getMethod("sendToBlocking", ID::class.java, String::class.java)) {
            assertEquals(QGMessageReceipt::class.java, returnType)
        }
        with(QGBot::class.java.getMethod("sendToAsync", ID::class.java, String::class.java)) {
            assertEquals(CompletableFuture::class.java, returnType)
        }
        with(QGBot::class.java.getMethod("sendToReserve", ID::class.java, String::class.java)) {
            assertEquals(SuspendReserve::class.java, returnType)
        }
    }

}

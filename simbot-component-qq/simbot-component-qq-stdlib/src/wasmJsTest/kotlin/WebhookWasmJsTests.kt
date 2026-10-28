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

import io.ktor.client.engine.mock.*
import kotlinx.coroutines.test.runTest
import love.forte.simbot.qguild.stdlib.BotFactory
import love.forte.simbot.qguild.stdlib.ConfigurableBotConfiguration
import love.forte.simbot.qguild.stdlib.EmitResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class WebhookWasmJsTests {
    @Test
    fun signsWebhookVerificationChallenge() = runTest {
        val bot = BotFactory.create(
            "11111111", "DG5g3B4j9X2KOErG", "",
            ConfigurableBotConfiguration().apply {
                apiClientEngine = MockEngine { respondOk() }
                wsClientEngine = MockEngine { respondOk() }
            }
        )

        try {
            val result = assertIs<EmitResult.Verified>(
                bot.emitEvent(
                    """{"d":{"plain_token":"Arq0D5A61EgUu4OxUvOp","event_ts":"1725442341"},"op":13}"""
                )
            )
            assertEquals("Arq0D5A61EgUu4OxUvOp", result.verified.plainToken)
            assertEquals(
                "87befc99c42c651b3aac0278e71ada338433ae26fcb24307bdc5ad38c1adc2d01bcfc" +
                    "adc0842edac85e85205028a1132afe09280305f13aa6909ffc2d652c706",
                result.verified.signature
            )
        } finally {
            bot.apiClient.close()
            bot.cancel()
        }
    }
}

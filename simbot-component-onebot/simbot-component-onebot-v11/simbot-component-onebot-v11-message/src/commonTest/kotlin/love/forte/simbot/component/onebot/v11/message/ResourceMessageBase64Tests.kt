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

package love.forte.simbot.component.onebot.v11.message

import love.forte.simbot.component.onebot.v11.message.segment.OneBotImage
import love.forte.simbot.component.onebot.v11.message.segment.OneBotRecord
import love.forte.simbot.component.onebot.v11.message.segment.OneBotVideo
import love.forte.simbot.resource.toResource
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class ResourceMessageBase64Tests {
    companion object {
        private const val BASE64_D = "qr6f9jnXeM/3txdpG94OQA=="
        private const val BASE64_U = "qr6f9jnXeM_3txdpG94OQA=="

        @OptIn(ExperimentalStdlibApi::class)
        private val original = "AABE9FF639D778CFF7B717691BDE0E40".hexToByteArray()
    }

    @Test
    fun imageBase64DefaultTest() {
        assertEquals(
            "base64://$BASE64_D",
            OneBotImage.create(
                original.toResource(),
            ).data.file
        )

        assertEquals(
            "base64://$BASE64_U",
            OneBotImage.create(
                original.toResource(),
                OneBotImage.AdditionalParams().apply {
                    base64Encoder = Base64Encoder.UrlSafe
                }
            ).data.file
        )
    }

    @Test
    fun videoBase64DefaultTest() {
        assertEquals(
            "base64://$BASE64_D",
            OneBotVideo.create(
                original.toResource(),
            ).data.file
        )

        assertEquals(
            "base64://$BASE64_U",
            OneBotVideo.create(
                original.toResource(),
                OneBotVideo.AdditionalParams().apply {
                    base64Encoder = Base64Encoder.UrlSafe
                }
            ).data.file
        )
    }

    @Test
    fun recordBase64DefaultTest() {
        assertEquals(
            "base64://$BASE64_D",
            OneBotRecord.create(
                original.toResource(),
            ).data.file
        )

        assertEquals(
            "base64://$BASE64_U",
            OneBotRecord.create(
                original.toResource(),
                OneBotRecord.AdditionalParams().apply {
                    base64Encoder = Base64Encoder.UrlSafe
                }
            ).data.file
        )
    }

}

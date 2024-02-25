/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.common.apidefinition

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.serializer
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class RestApiDefinitionTests {

    @Test
    fun restApiDefTest() {
        class Body

        with(object : PostApiDefinition<Unit>() {
            override val url: Url = Url("/")
            override val resultDeserializationStrategy: DeserializationStrategy<Unit> = Unit.serializer()
            override fun createBody(): Any = Body()
        }) {
            assertEquals(body, body)
            assertEquals(HttpMethod.Post, method)
        }

        with(object : PutApiDefinition<Unit>() {
            override val url: Url = Url("/")
            override val resultDeserializationStrategy: DeserializationStrategy<Unit> = Unit.serializer()
            override fun createBody(): Any = Body()
        }) {
            assertEquals(body, body)
            assertEquals(HttpMethod.Put, method)
        }

        with(object : GetApiDefinition<Unit>() {
            override val url: Url = Url("/")
            override val resultDeserializationStrategy: DeserializationStrategy<Unit> = Unit.serializer()
        }) {
            assertEquals(HttpMethod.Get, method)
        }

        with(object : DeleteApiDefinition<Unit>() {
            override val url: Url = Url("/")
            override val resultDeserializationStrategy: DeserializationStrategy<Unit> = Unit.serializer()
        }) {
            assertEquals(HttpMethod.Delete, method)
        }

    }

}

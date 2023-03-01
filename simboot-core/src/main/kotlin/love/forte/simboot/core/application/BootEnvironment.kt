/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.core.application

import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.Component
import love.forte.simbot.NoSuchComponentException
import love.forte.simbot.application.Application
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class BootEnvironment(
    override val components: List<Component>,
    val logger: Logger,
    val coroutineContext: CoroutineContext,
) : Application.Environment {
    override fun getComponent(id: String): Component = getComponentOrNull(id) ?: throw NoSuchComponentException(id)
    override fun getComponentOrNull(id: String): Component? = components.firstOrNull { it.id == id }
    override val serializersModule: SerializersModule = SerializersModule {
        components.forEach {
            include(it.componentSerializersModule)
        }
    }

}

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

package love.forte.simbot.core.application

import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.Component
import love.forte.simbot.NoSuchComponentException
import love.forte.simbot.application.Application
import love.forte.simbot.utils.view
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author ForteScarlet
 */
internal class SimpleEnvironment(
    components0: List<Component>,
    val logger: Logger,
    val coroutineContext: CoroutineContext,

    ) : Application.Environment {
    override val components: List<Component> = components0.view()
    override val serializersModule: SerializersModule = SerializersModule {
        components0.forEach { include(it.componentSerializersModule) }
    }

    override fun getComponent(id: String): Component = getComponentOrNull(id) ?: throw NoSuchComponentException(id)
    override fun getComponentOrNull(id: String): Component? = components.firstOrNull { it.id == id }
}


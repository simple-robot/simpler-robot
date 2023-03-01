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

package love.forte.simboot.config

import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.installAllComponents

/**
 *
 * [ComponentRegistryConfigure] 的默认实现，
 * 当依赖环境中不存在任何 [ComponentRegistryConfigure] 的时候使用。
 *
 * @author ForteScarlet
 */
public object InstallAllComponentRegistryConfigure : ComponentRegistryConfigure() {
    override fun registerComponent(applicationBuilder: ApplicationBuilder<*>) {
        applicationBuilder.installAllComponents()
    }
}

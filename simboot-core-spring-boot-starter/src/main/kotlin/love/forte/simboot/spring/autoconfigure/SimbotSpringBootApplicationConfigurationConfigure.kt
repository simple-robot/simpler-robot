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

package love.forte.simboot.spring.autoconfigure

import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfiguration


/**
 *
 * 对 application 的配置环节进行操作的配置类。
 * ```kotlin
 * springBootApplication(initialConfiguration, configurator = { /* 此配置位于此处 */  }) { ... }
 * ```
 *
 * @author ForteScarlet
 */
public fun interface SimbotSpringBootApplicationConfigurationConfigure {
    public fun SpringBootApplicationConfiguration.config()
}

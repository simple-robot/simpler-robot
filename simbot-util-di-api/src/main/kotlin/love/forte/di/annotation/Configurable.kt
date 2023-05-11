/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.di.annotation

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.annotation.AliasFor

/**
 * 标记一个类可以注入配置信息。
 *
 * 提供一个 [prefix] 为配置项的前置属性。
 *
 *
 * 在json中，以属性前缀 `simbot.core.xxx` 为例，其格式应为：
 * ```json
 * {
 *     "simbot": {
 *          "core": {
 *              "xxx": xxx
 *          }
 *     }
 * }
 * ```
 *
 * 在properties中应为：
 * ```properties
 * simbot.core.xxx=xxx
 *  ```
 *
 * 在 yaml 中应为：
 * ```yaml
 * simbot:
 *    core:
 *      xxx: xxx
 *
 * ```
 *
 *
 * @property prefix 属性坐标前缀.
 *
 */
@ConfigurationProperties
@Target(AnnotationTarget.CLASS)
public annotation class Configurable(
    @get:AliasFor(annotation = ConfigurationProperties::class)
    val prefix: String = "",

    @get:AliasFor(annotation = ConfigurationProperties::class)
    val ignoreUnknownFields: Boolean = true
)

/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.spring2.warn

import love.forte.simbot.logger.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered


/**
 * 请尽可能考虑迁移至 Spring Boot v3.x.
 * @author ForteScarlet
 */
@RequiresOptIn(
    message = MIGRATING_SPRING_BOOT_VERSION_WARNING
)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
public annotation class MigratingSpringBootVersion

/**
 * @suppress
 */
public const val MIGRATING_SPRING_BOOT_VERSION_WARNING: String =
    "Spring Boot starter 的 v2 版本的维护成本较高，不会持续维护很久。" +
        "并且现在 Spring Boot v2.7.x已经在 2023-11-24 结束支持，" +
        "请考虑迁移至 Spring Boot v3.x。"

/**
 * 版本迁移警告。
 */
@Configuration(proxyBeanMethods = false)
public open class MigratingSpringBootVersionWarningPrinter : CommandLineRunner, Ordered {
    public companion object {
        private val logger = LoggerFactory.getLogger(MigratingSpringBootVersionWarningPrinter::class)
    }

    override fun run(vararg args: String?) {
        logger.warn(MIGRATING_SPRING_BOOT_VERSION_WARNING)
    }

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE
}

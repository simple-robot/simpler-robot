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

package love.forte.simboot.spring.autoconfigure

import org.springframework.context.annotation.Import

/**
 * 标记于Springboot相关例如启动类中，告知simbot-springboot-starter simbot的启动入口。
 *
 * simbot的starter会直接扫描spring的依赖信息来加载监听函数，但是无法通过spring来扫描kotlin的顶层函数，
 * 因此 [EnableSimbot] 可以用于告知simbot扫描顶层函数的范围。
 * ```kotlin
 * // Kotlin
 *  @EnableSimbot
 *  @SpringBootApplication
 *  open class Main
 *
 *  fun main(vararg args: String) {
 *      val context = runApplication<Main>(*args)
 *  }
 * ```
 *
 * ```Java
 *  @EnableSimbot
 *  @SpringBootApplication
 *  public class Main {
 *      public static void main(String[] args) {
 *          SpringApplication.run(Main.class, args);
 *      }
 *  }
 * ```
 *
 *
 *
 */
@Target(AnnotationTarget.CLASS)
@Import(
    // 兼容 simbot.factories
    SimbotIncludesSelector::class,
    // 调度器
    CoroutineDispatcherConfiguration::class,
    // configures
    SimbotSpringBootDefaultConfigures::class,
    // app
    SimbotSpringBootApplicationConfiguration::class,
    // after application
    
    // listener register
    SimbotSpringBootListenerAutoRegisterBuildConfigure::class,
    // bot register
    SimbotSpringBootBotAutoRegisterBuildConfigure::class,
)
public annotation class EnableSimbot

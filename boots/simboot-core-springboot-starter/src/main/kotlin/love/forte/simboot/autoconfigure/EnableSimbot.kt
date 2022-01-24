/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simboot.autoconfigure

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
    SimbotIncludesSelector::class,
    SpringbootCoreBootEntranceContextFactoriesConfiguration::class,
    SpringEventListenerManagerConfiguration::class,
    SpringbootCoreBootEntranceContextConfiguration::class,
    SimbootAppRunnerConfiguration::class,
    SimbootAutoconfigure::class,
    SimbootContextStarterProperties::class,
    SimbotAppStarter::class
)
public annotation class EnableSimbot(
    //
)

/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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

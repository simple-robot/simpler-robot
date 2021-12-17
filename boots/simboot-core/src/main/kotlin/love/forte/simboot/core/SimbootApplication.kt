/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.core

import love.forte.simboot.SimbootEntrance

/**
 * 基础的 boot-core 所使用的启动类标记注解，提供部分参数。
 *
 * **不应** 在spring环境下使用。
 *
 * 此注解在当前环境 [SimbootEntrance] 所选取的启动入口为 [CoreBootEntrance] 的情况下有效。
 *
 *
 * @property scanPackages 指定所需扫描包路径。 如果为空默认选取标记类所在包路径为根路径。
 * @property configFilePrefix 配置文件前缀。会扫描 `resources:$prefix*.*` 和 `file:$prefix*.*` 的相关文件。
 * 文件格式支持：
 * - `.properties`
 * - `.yaml`
 * - `.yml`
 * - `.json`
 *
 */
@Target(AnnotationTarget.CLASS)
public annotation class SimbootApplication(
    val scanPackages: Array<String> = [],
    val configFilePrefix: String = "simbot"
)

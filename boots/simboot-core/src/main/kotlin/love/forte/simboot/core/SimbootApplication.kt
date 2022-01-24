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
    val topListenerScanPackages: Array<String> = [],
    val configFilePrefix: String = "simbot"
)

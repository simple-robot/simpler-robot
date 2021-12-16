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

package love.forte.simboot.annotation

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
@Deprecated("不确定到底用不用")
public annotation class Configurable(
    @get:AliasFor(annotation = ConfigurationProperties::class, value = "prefix")
    val prefix: String = ""
)

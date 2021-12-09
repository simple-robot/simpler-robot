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

import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AliasFor

/**
 * 标记一个类为配置bean。
 *
 * 配置bean
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Configuration
public annotation class ConfigBeans(
    @get:AliasFor(annotation = Configuration::class)
    val value: String = ""
)

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

import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component


/**
 * 为依赖注入模块提供的标准注解，代表一个需要交由容器管理的结果。
 * 此注解标记在类上时，当前类解析为一个Bean实例。
 *
 * 提供针对于Springboot的映射，但是映射相关注解仅用于编译，不存在于依赖传递中。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Component
@Bean
public annotation class Beans(
    /**
     *
     * 当标记于类上时，用于指定此Bean的名称。
     *
     * 在依赖管理模块中，每一个依赖bean都应拥有一个唯一的ID名称。
     *
     * 当name未指定的时候，如果是类，则为此类的 [全限定二进制名称][java.lang.Class.getName].
     *
     * 如果为函数返回值，则为所在类的[全限定二进制名称][java.lang.Class.getName].函数名。
     *
     * 如果函数为 `get` 开头，则会视其为一个 getter, 清除get并取属性名。
     *
     */
    @get:AliasFor("value", annotation = Component::class)
    val classBeanName: String = "",


    /**
     * 当标记于子元素（@BeansConfig下的某些方法等）上时，用于指定此Bean的名称。
     */
    @get:AliasFor("value", annotation = Bean::class)
    val childBeanName: String = ""
)

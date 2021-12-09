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

import org.springframework.core.annotation.AliasFor
import javax.annotation.Resource
import kotlin.reflect.KClass


/**
 * 为某个Bean中的属性或者某些函数指定其注入值。
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER
)
@MustBeDocumented
@Resource
public annotation class Depend(

    /**
     * 指定目标依赖的名称。
     * 在依赖管理中，依赖的名称应当是唯一的，但是需要注意你所需的依赖类型应当与当前的属性类型一致。
     */
    @get:AliasFor(annotation = Resource::class)
    val name: String = "",

    /**
     * 指定一个所需类型。
     * 当无法确定目标 name 为何时，可以通过类型寻找最终的目标依赖。
     * 当 [name] 未指定且 [type] 值为 `Any::class`(java.lang.Object.class) 的时候，
     * 默认使用类型判断最终注入结果的类型。
     */
    @get:AliasFor(annotation = Resource::class)
    val type: KClass<*> = Any::class,

    /**
     * 是否为必须的。如果 [required] 为 false，且在无法断定其最终目标的时候尝试为其注入一个 `null`.
     *
     */
    val required: Boolean = true

)

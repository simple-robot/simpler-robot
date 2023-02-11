/*
 * Copyright (c) 2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.di.core

import love.forte.di.BeanManager
import love.forte.di.core.internal.AnnotationGetter
import love.forte.di.core.internal.CoreBeanClassRegistrarImpl
import kotlin.reflect.KClass

/**
 * bean注册器，通过加载 [KClass]
 *
 * @author ForteScarlet
 */
public interface CoreBeanClassRegistrar {

    /**
     * 注册一个类型到缓冲区中。
     * [types] 中出现的类型默认认为其均为可注入的，不会再检测注解(例如 [love.forte.di.annotation.Beans] ) ,
     *
     * 但是会检测 [love.forte.di.annotation.BeansFactory], 只有存在此注解才会扫描下层的注册函数。
     *
     */
    public fun register(vararg types: KClass<*>): CoreBeanClassRegistrar

    /**
     * 清除当前缓冲区。
     */
    public fun clear()

    /**
     * 将目前已经注册的所有Bean信息解析注入至指定Bean.
     */
    public fun inject(beanManager: BeanManager)
}


public fun coreBeanClassRegistrar(annotationGetter: AnnotationGetter): CoreBeanClassRegistrar =
    CoreBeanClassRegistrarImpl(annotationGetter)

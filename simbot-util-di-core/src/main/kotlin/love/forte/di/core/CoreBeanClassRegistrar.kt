/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
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

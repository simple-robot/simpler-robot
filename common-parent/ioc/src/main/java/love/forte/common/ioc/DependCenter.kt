/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     DependCenter.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.ioc

import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * 依赖管理中心。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 *
 * @param singletonMap 保存单例的map。在put的时候会有同步锁，所以应该不需要线程安全的Map.
 * @param nameResourceWarehouse 保存依赖的map。以name为key, 对应着唯一的值。
 * @param typeResourceWarehouse 保存依赖的map。以type为key, 可以存在多个相同的类型，但是最终只会留下一个。
 * @param parent 依赖中心的父类依赖。
 */
public class DependCenter
@JvmOverloads
constructor(
    private val singletonMap: MutableMap<String, Any> = mutableMapOf(),
    private val nameResourceWarehouse: MutableMap<String, BeanDepend> = ConcurrentHashMap<String, BeanDepend>(),
    private val typeResourceWarehouse: MutableMap<Class<*>, Deque<BeanDepend>> = ConcurrentHashMap<Class<*>, Deque<BeanDepend>>(),
    private val parent: DependBeanFactory? = null
) {














}
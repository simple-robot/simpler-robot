/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.core.listener

import love.forte.common.ioc.DependBeanFactory
import love.forte.common.utils.annotation.AnnotationUtil
import love.forte.common.utils.convert.ConverterManager
import love.forte.simbot.annotation.Listens
import love.forte.simbot.filter.FilterManager
import love.forte.simbot.listener.ListenerFunction
import java.lang.reflect.Modifier

/**
 *
 * method 监听函数工厂。提供一个 class，解析其中的method并转化为 functions。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface MethodListenerFunctionFactory {

    /**
     * 解析一个class并获取其中的listenerFunctions。
     */
    fun getListenerFunctions(type: Class<*>): List<ListenerFunction>

}

/**
 * [MethodListenerFunctionFactory] 默认实现。
 */
public class MethodListenerFunctionFactoryImpl(
    private val dependBeanFactory: DependBeanFactory,
    private val filterManager: FilterManager,
    private val converterManager: ConverterManager
) : MethodListenerFunctionFactory {
    override fun getListenerFunctions(type: Class<*>): List<ListenerFunction> {
        return type.declaredMethods.mapNotNull {
            AnnotationUtil.getAnnotation(it, Listens::class.java)?.run {
                if(!Modifier.isPublic(it.modifiers)) {
                    // not public.
                    throw IllegalStateException("@Listens can only be annotate on public methods, but method: $it")
                } else {
                    MethodListenerFunction(it, dependBeanFactory, filterManager, converterManager)
                }
            }
        }
    }
}
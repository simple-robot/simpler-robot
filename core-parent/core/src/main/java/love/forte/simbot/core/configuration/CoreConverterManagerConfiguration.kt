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

package love.forte.simbot.core.configuration

import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.common.utils.annotation.AnnotationUtil
import love.forte.common.utils.convert.Converter
import love.forte.common.utils.convert.ConverterManager
import love.forte.common.utils.convert.ConverterManagerBuilder
import love.forte.common.utils.convert.HutoolConverterManagerBuilderImpl
import java.lang.reflect.Type

import love.forte.simbot.annotation.Converter as ConverterAnnotation

/**
 *
 * [ConverterManager] 配置类。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class CoreConverterManagerConfiguration {


    /**
     * 依赖工厂，用于获取所有的类型转化器。
     */
    @Depend
    private lateinit var dependBeanFactory: DependBeanFactory


    /**
     * 获取一个转化器builder。
     */
    @CoreBeans
    fun coreConverterManagerBuilder(): ConverterManagerBuilder = HutoolConverterManagerBuilderImpl()


    /**
     * 通过builder获取转化器。
     */
    @CoreBeans
    fun coreConverterManager(builder: ConverterManagerBuilder): ConverterManager {

        val converterType = Converter::class.java

        dependBeanFactory.allBeans.mapNotNull {
            val type = dependBeanFactory.getType(it)
            if (converterType.isAssignableFrom(type)) {
                // if convert. annotation?
                val annotation: ConverterAnnotation? =
                    AnnotationUtil.getAnnotation(type, ConverterAnnotation::class.java)

                val types: Array<Type> =
                    annotation?.value?.takeIf { ca -> ca.isNotEmpty() }?.map { ca -> ca.java }?.toTypedArray()
                        ?: runCatching {
                            val method = type.getMethod("convert", Any::class.java)
                            arrayOf(method.genericReturnType)
                        }.getOrDefault(emptyArray())

                types to it
            } else null
        }.forEach { (targets, name) ->
            targets.forEach {
                builder.register(it, dependBeanFactory[name] as Converter<*>)
            }
        }


        return builder.build()
    }


}

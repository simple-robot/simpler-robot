/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     HutoolConverterImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.utils.convert

import cn.hutool.core.convert.AbstractConverter
import cn.hutool.core.convert.ConverterRegistry
import java.lang.reflect.Type
import cn.hutool.core.convert.Converter as HutoolConverter


/**
 * [ConverterManagerBuilder] 的基于 **hutool-convertUtil** 的实现
 */
public open class HutoolConverterManagerBuilderImpl : ConverterManagerBuilder {

    private lateinit var registry: ConverterRegistry

    /**
     * 向当前的 [registry] 中注册一个转化器
     */
    override fun register(target: Type, converter: Converter<*>): ConverterManagerBuilder =
        this.apply {
            if(!::registry.isInitialized){
                registry = ConverterRegistry()
            }
            registry.putCustom(target, converter.asHutoolConvert())
        }

    /**
     * 构建要一个 [ConverterManager]
     */
    override fun build(): ConverterManager {
        return if(::registry.isInitialized){
            HutoolConverterManagerImpl(registry)
        }else{
            HutoolConverterManagerImpl(ConverterRegistry.getInstance())
        }
    }
}


/**
 * hutool 的 [AbstractConverter] 实现类
 */
public open class HutoolConverterImpl<T>(private val convert: (Any) -> T?) : AbstractConverter<T>() {
    /**
     * 内部转换器，被 [AbstractConverter.convert] 调用，实现基本转换逻辑<br></br>
     * 内部转换器转换后如果转换失败可以做如下操作，处理结果都为返回默认值：
     *
     * <pre>
     * 1、返回`null`
     * 2、抛出一个[RuntimeException]异常 </pre>
     *
     *
     * @param value 值
     * @return 转换后的类型
     */
    override fun convertInternal(value: Any?): T? = value?.let { convert(value) }
}


/**
 * 转化类型manager, 基于 [ConverterRegistry] 实现
 */
public open class HutoolConverterManagerImpl(private val converterRegistry: ConverterRegistry) :
    ConverterManager {
    /**
     * 进行值转化
     */
    override fun <T> convert(target: Type, value: Any?, defValue: T?): T? {
        return converterRegistry.convert(target, value, defValue, true)
    }

    /**
     * 获取某目标的转化器
     */
    override fun <T> getConverterByTarget(target: Type): Converter<T> {
        return converterRegistry.getConverter<T>(target, true).asConverter()
    }
}


/**
 * 将 [Converter] 接口实例 转化为 [HutoolConverter] 接口实例
 */
private fun <T> Converter<T>.asHutoolConvert(): HutoolConverter<T> =
    HutoolConverterImpl { convert(it) }

/**
 * 将 [HutoolConverter] 接口实例 转化为 [Converter] 接口实例
 */
private fun <T> HutoolConverter<T>.asConverter(): Converter<T> =
    Converter<T> { convert(it, null) }










/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConverterManagerConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.utils.convert.ConverterManager
import love.forte.common.utils.convert.ConverterManagerBuilder
import love.forte.common.utils.convert.HutoolConverterManagerBuilderImpl

/**
 *
 * [ConverterManager] 配置类。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class CoreConverterManagerConfiguration {

    /**
     * 获取一个转化器builder。
     */
    @CoreBeans
    fun coreConverterManagerBuilder(): ConverterManagerBuilder = HutoolConverterManagerBuilderImpl()


    /**
     * 通过builder获取转化器。
     */
    @CoreBeans
    fun coreConverterManager(builder: ConverterManagerBuilder): ConverterManager = builder.build()


}

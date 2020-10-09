/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreConfigConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.configuration.ConfigurationParserManagerBuilder
import love.forte.common.configuration.impl.LinkedConfigurationParserManagerBuilder
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.utils.convert.ConverterManager

/**
 *
 * 注入配置相关的内容。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class CoreConfigConfiguration {


    @CoreBeans
    fun coreConfigurationManagerBuilder(converterManager: ConverterManager): ConfigurationParserManagerBuilder {
        return LinkedConfigurationParserManagerBuilder(converterManager)
    }

}
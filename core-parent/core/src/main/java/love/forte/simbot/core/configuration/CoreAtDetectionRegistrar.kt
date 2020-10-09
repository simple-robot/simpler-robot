/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CodeAtDetectionRegistrar.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.PrePass
import love.forte.simbot.core.filter.AtDetectionRegistrar
import love.forte.simbot.core.filter.CatAtDetectionFactory

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class CoreAtDetectionRegistrar {


    /**
     * 注册一个 [CatAtDetectionFactory]。
     */
    @PrePass
    fun registerAtDetectionFactory(registrar: AtDetectionRegistrar) {
        registrar.registryAtDetection(CatAtDetectionFactory)
    }

}
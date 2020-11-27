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

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.PrePass
import love.forte.simbot.constant.PriorityConstant
import love.forte.simbot.core.filter.CatAtDetectionFactory
import love.forte.simbot.filter.AtDetectionRegistrar

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans("coreAtDetectionRegistrar")
public class CoreAtDetectionRegistrar {


    /**
     * 注册一个 [CatAtDetectionFactory]。
     */
    @PrePass(priority = PriorityConstant.CORE_TENTH)
    fun registerAtDetectionFactory(registrar: AtDetectionRegistrar) {
        registrar.registryAtDetection(CatAtDetectionFactory)
    }

}
/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreMethodListenerRegistrar.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.PostPass


@ConfigBeans
@AsConfig(prefix = "core")
public class CoreMethodListenerRegistrar {


    @field:ConfigInject("scanPackages", orDefault = [""])
    lateinit var scanPackages: List<String>


    /**
     * 扫描并注册监听函数。
     */
    @PostPass
    fun registerMethodListenerFunctions() {
        println("post!")
        TODO("register listener.")
    }


}
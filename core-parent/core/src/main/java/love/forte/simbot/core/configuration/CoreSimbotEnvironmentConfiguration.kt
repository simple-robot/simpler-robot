/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreSimbotEnvironmentConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.simbot.*

/**
 *
 * 配置 [SimbotEnvironment]
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class CoreSimbotEnvironmentConfiguration {


    @Depend
    private lateinit var resourceEnvironment: SimbotResourceEnvironment

    @Depend
    private lateinit var argsEnvironment: SimbotArgsEnvironment

    @Depend
    private lateinit var packageScanEnvironment: SimbotPackageScanEnvironment


    @CoreBeans
    fun coreSimbotEnvironment(): SimbotEnvironment {
        return CoreSimbotEnvironment(
            resourceEnvironment, argsEnvironment, packageScanEnvironment
        )
    }

}
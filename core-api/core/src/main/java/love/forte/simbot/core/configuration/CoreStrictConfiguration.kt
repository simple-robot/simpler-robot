/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.core.strict.CoreStrictManager
import love.forte.simbot.core.strict.StrictManager
import love.forte.simbot.core.strict.init


/**
 *
 * @author ForteScarlet
 */
@ConfigBeans
@AsCoreConfig
public class CoreStrictConfiguration {
    private companion object : TypedCompLogger(CoreStrictConfiguration::class.java)

    @ConfigInject("strict", orIgnore = true)
    var strict: Boolean = true


    @Beans
    fun strictManager(): StrictManager {
        return CoreStrictManager.apply {
            init {
                coreStrict = strict
                logger.debug("Core strict mode: {}", coreStrict)

            }
        }
    }

}

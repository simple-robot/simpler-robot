/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreBotRegistrar.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.PostPass


@ConfigBeans
public class CoreBotRegistrar {


    /**
     * 注册bot。
     */
    @PostPass
    public fun registerBots(){
        TODO("注册bot。")
    }


}
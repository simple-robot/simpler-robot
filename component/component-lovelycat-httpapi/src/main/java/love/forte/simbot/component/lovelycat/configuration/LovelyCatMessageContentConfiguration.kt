/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatMessageContentConfiguration.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.component.lovelycat.configuration

import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.component.lovelycat.message.LovelyCatMessageContentBuilderFactory


@ConfigBeans("lovelyCatMessageContentConfiguration")
public class LovelyCatMessageContentConfiguration {

    @Beans
    public fun lovelyCatMessageContentBuilderFactory() =
        LovelyCatMessageContentBuilderFactory

}
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

import love.forte.catcode.CatCodeUtil
import love.forte.catcode.CatDecoder
import love.forte.catcode.CatEncoder
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.SpareBeans

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans("coreCatConfiguration")
class CoreCatConfiguration {



    @SpareBeans("coreCatCodeDecoder")
    fun coreCatCodeDecoder(): CatDecoder = CatDecoder

    @SpareBeans("coreCatCodeEncoder")
    fun coreCatCodeEncoder(): CatEncoder = CatEncoder

    @SpareBeans("coreCatCodeUtil")
    fun coreCatCodeUtil(): CatCodeUtil = CatCodeUtil



}
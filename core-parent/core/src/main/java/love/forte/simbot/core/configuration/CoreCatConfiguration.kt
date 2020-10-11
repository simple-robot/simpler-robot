/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreCatConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.catcode.CatCodeUtil
import love.forte.catcode.CatDecoder
import love.forte.catcode.CatEncoder
import love.forte.common.ioc.annotation.ConfigBeans

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
class CoreCatConfiguration {



    @CoreBeans
    fun coreCatCodeDecoder(): CatDecoder = CatDecoder

    @CoreBeans
    fun coreCatCodeEncoder(): CatEncoder = CatEncoder

    @CoreBeans
    fun coreCatCodeUtil(): CatCodeUtil = CatCodeUtil



}
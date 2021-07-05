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

package love.forte.test.listener

import love.forte.common.ioc.annotation.Beans
import love.forte.simbot.annotation.Filter
import love.forte.simbot.annotation.Filters
import love.forte.simbot.annotation.OnPrivate
import love.forte.simbot.filter.MatchType


/**
 *
 * @author ForteScarlet
 */
@Beans
class TestListener {

    @OnPrivate
    @Filters(value = [Filter("name{{name}}", matchType = MatchType.REGEX_MATCHES)])
    suspend fun myFilter(name: String) {
        println("Name is: $name")
    }


}


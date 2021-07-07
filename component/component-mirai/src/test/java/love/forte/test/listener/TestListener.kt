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
import love.forte.simbot.filter.BaseListenerFilterProcessor
import love.forte.simbot.filter.FilterData
import love.forte.simbot.filter.MatchType


/**
 *
 * @author ForteScarlet
 */
@Beans
class TestListener {

    @OnPrivate
    @Filters(
        value = [
            Filter(
                value = "name{{name}}",
                matchType = MatchType.REGEX_MATCHES,
                processor = MyProcessorFilter::class
            )
        ]
    )
    fun myFilter(name: String) {
        println("Name is: $name")
    }




}


class MyProcessorFilter : BaseListenerFilterProcessor() {
    override fun test(data: FilterData): Boolean {
        println("filter : -> $filter")
        println("filters: -> $filters")
        println("data   : -> $data")
        println()
        return true
    }

    override fun getFilterValue(name: String, text: String): String? {
        return if (name == "name") "Name!" else null
    }
}

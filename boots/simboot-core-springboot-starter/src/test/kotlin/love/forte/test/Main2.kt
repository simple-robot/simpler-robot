/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.test

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver


fun main() {

    val resource = ClassPathResource("simbot-bots/**.bot")

    val resolver = PathMatchingResourcePatternResolver()

    val resources = resolver.getResources("classpath:simbot-bots/**.bot")

    for (r in resources) {
        println(r)
        println(r.filename)
    }

    println("====")

    val resources2 = resolver.getResources("simbot-bots/**.bot")

    for (r in resources2) {
        println(r)
        println(r.filename)
    }

}
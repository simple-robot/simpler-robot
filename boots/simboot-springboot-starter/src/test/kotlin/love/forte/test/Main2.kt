/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
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
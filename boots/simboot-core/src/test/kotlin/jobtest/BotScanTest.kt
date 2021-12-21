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

package jobtest

import love.forte.simboot.core.internal.ResourcesScanner
import love.forte.simboot.core.internal.toList
import love.forte.simboot.core.internal.visitJarEntry
import love.forte.simboot.core.internal.visitPath
import love.forte.simbot.BotVerifyInfo
import love.forte.simbot.asBotVerifyInfo
import java.util.*
import kotlin.io.path.bufferedReader


fun main() {

    val loader = Thread.currentThread().contextClassLoader

    println("C.bot: " + loader.getResource("simbot-bots/C.bot"))
    println("C.bot: " + loader.getResource("simbot-bots"))
    println("C.bot: " + loader.getResource("simbot-bots\\C.bot"))
    println("C.bot: " + loader.getResource("simbot-bots\\c.bot"))

    ResourcesScanner<BotVerifyInfo>()
        .scan("")
        .glob("simbot-bots/**.bot")
        .visitJarEntry { _, url ->
            println("Jar : $url")
            sequenceOf(
                url.openStream().bufferedReader().use { reader ->
                    Properties().also { p -> p.load(reader) }
                }.asBotVerifyInfo()
            )
        }
        .visitPath { (path, resource) ->
            println("path: $path, $resource")
            sequenceOf(
                path.bufferedReader().use { reader ->
                    Properties().also { p -> p.load(reader) }
                }.asBotVerifyInfo()
            )
        }
        .toList(false)
}
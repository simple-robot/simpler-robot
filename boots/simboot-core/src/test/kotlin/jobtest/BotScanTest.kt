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
import java.nio.file.Files
import java.util.*
import kotlin.io.path.bufferedReader
import kotlin.io.path.toPath


fun main() {

    val loader = Thread.currentThread().contextClassLoader

    val path0 = loader.getResource("").toURI().toPath()
    println(path0)
    Files.list(path0).forEach { p -> println("p = [${p}]")
    }
    val path = loader.getResource("simbot-bots")!!.toURI().toPath()

    println(path)
    Files.list(path).forEach { p -> println("p = [${p}]")
    }


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
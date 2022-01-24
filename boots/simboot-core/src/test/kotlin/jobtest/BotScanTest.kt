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

package jobtest

import love.forte.simboot.core.internal.ResourcesScanner
import love.forte.simboot.core.internal.toList
import love.forte.simboot.core.internal.visitJarEntry
import love.forte.simboot.core.internal.visitPath
import love.forte.simbot.BotVerifyInfo
import love.forte.simbot.asBotVerifyInfo
import java.nio.file.Files
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
                url.asBotVerifyInfo()
            )
        }
        .visitPath { (path, resource) ->
            println("path: $path, $resource")
            sequenceOf(
                path.asBotVerifyInfo()
            )
        }
        .toList(false)
}
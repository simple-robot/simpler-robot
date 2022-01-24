/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
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
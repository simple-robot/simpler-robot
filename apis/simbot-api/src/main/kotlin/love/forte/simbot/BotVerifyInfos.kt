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

@file:JvmName("BotVerifyInfoUtil")

package love.forte.simbot

import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.inputStream


public fun URL.asBotVerifyInfo(): BotVerifyInfo {
    return URLBotVerifyInfo(this)
}


private class URLBotVerifyInfo(
    private val url: URL,
) : BotVerifyInfo {
    override val infoName: String get() = url.toString()

    override fun inputStream(): InputStream = url.openStream()
}


public fun Path.asBotVerifyInfo(): BotVerifyInfo {
    return PathBotVerifyInfo(this)
}

private class PathBotVerifyInfo(private val path: Path) : BotVerifyInfo {
    override val infoName: String get() = path.toString()
    override fun inputStream(): InputStream = path.inputStream()
}


public inline fun <T> BotVerifyInfo.tryResolveVerifyInfo(
    initErr: () -> Throwable,
    vararg decoders: (InputStream) -> T
): Result<T> {
    lateinit var err: Throwable

    decoders.forEachIndexed { index, decoder ->
        val inp = inputStream()
        try {
            val result = decoder(inp)
            return Result.success(result)
        } catch (e: Throwable) {
            if (index == 0) {
                err = initErr()
            }
            err.addSuppressed(e)
        } finally {
            inp.close()
        }
    }

    return Result.failure(err)
}

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

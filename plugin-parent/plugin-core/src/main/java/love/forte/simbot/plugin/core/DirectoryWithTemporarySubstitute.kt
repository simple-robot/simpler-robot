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

package love.forte.simbot.plugin.core

import java.io.IOException
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.*


/**
 *
 * 通过临时文件夹进行的引用 [Path].
 * 对于一个 [DirectoryWithTemporarySubstitute], 其实现于 [Path] 的一切操作均来源于 [替代品][temporarySubstitute],
 * 而可以通过 [realPath] 来得到此替代品所代表的真实 [Path].
 *
 * 每次执行 [sync] 的时候，都会将 [realPath] 覆盖更新到 [temporarySubstitute] 处（或被删除），
 * 同步的时候会同步目录下的文件列表，但是不会递归同步，也不会同步文件夹，仅仅只同步当前层级。
 *
 *
 *
 * 需要保证 [realPath] 是文件夹类型。
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public class DirectoryWithTemporarySubstitute(override val realPath: Path, override val temporarySubstitute: Path) :
    Path by temporarySubstitute, PathWithTemporarySubstitute {

    override fun toString(): String = "$realPath (temp in $temporarySubstitute)"

    /**
     * 执行文件同步，即将 [真实文件][realPath] 同步至 [临时文件][temporarySubstitute].
     * @throws IOException 对文件的操作可能会存在I/O异常, like [createDirectories]、[copyTo]、[deleteIfExists]
     */
    @Throws(IOException::class)
    @Synchronized
    override fun sync() {
        if (realPath.exists()) {
            val existNameMap: MutableMap<String, Path>? = temporarySubstitute.takeIf { it.exists() }?.useDirectoryEntries {
                it.map { p -> p.name to p }.toList().toMap(
                    mutableMapOf())
            }
            realPath.useDirectoryEntries {
                it.forEach { entry ->
                    val copyToPath = temporarySubstitute / entry.relativeTo(realPath)
                    entry.copyTo(copyToPath.apply { parent?.createDirectories() },
                        StandardCopyOption.COPY_ATTRIBUTES,
                        StandardCopyOption.REPLACE_EXISTING
                    )
                    existNameMap?.remove(entry.name)
                }
            }
            if (existNameMap != null && existNameMap.isNotEmpty()) {
                existNameMap.values.forEach {
                    it.deleteIfExists()
                }
            }

        } else {
            // 不存在, 删除
            temporarySubstitute.deleteDeep() // .deleteIfExists()
        }
    }

    override fun cleanTemp() {
        temporarySubstitute.deleteDeep()
    }

}


internal fun Path.deleteDeep() {
    if (!exists()) return

    if (isRegularFile()) {
        deleteIfExists()
    }

    if (isDirectory()) {
        useDirectoryEntries {
            for (path in it) {
                path.deleteDeep()
            }
        }
    }
    deleteIfExists()
}

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

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.isDirectory
import kotlin.io.path.relativeTo


/**
 *
 * 通过临时文件进行的引用 [Path].
 * 对于一个 [PathWithTemporarySubstitute], 其实现于 [Path] 的一切操作均来源于 `替代品 (temporarySubstitute)`,
 * 而可以通过 [realPath] 来得到此替代品所代表的真实 [Path].
 *
 * 每次执行 [sync] 的时候，都会将 [realPath] 覆盖更新到 `temporarySubstitute` 处（或被删除）。
 *
 * @see FileWithTemporarySubstitute
 * @see DirectoryWithTemporarySubstitute
 *
 * @author ForteScarlet
 */
public sealed interface PathWithTemporarySubstitute : Path {

    val realPath: Path

    val temporarySubstitute: Path

    /**
     * 执行替身同步。
     */
    fun sync()

    /**
     * 清理替补文件
     */
    fun cleanTemp()

}





/**
 * 根据一个相对目录，构建 [Path] 对应的 [PathWithTemporarySubstitute].
 */
fun Path.withTemporarySubstitute(relative: Path, tempRootDir: Path): PathWithTemporarySubstitute {
    val tempPath = tempRootDir / this.relativeTo(relative)
    return if (this.isDirectory()) DirectoryWithTemporarySubstitute(this, tempPath) else FileWithTemporarySubstitute(this, tempPath)

}
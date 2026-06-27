/*
 *     Copyright (c) 2023-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package changelog

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * 生成 `.changelog` 目录下的子 changelog 文件。
 */
abstract class GenerateSubChangelogTask : DefaultTask() {
    @get:Input
    abstract val tag: Property<String>

    @get:Input
    abstract val versions: MapProperty<String, String>

    @get:OutputDirectory
    abstract val subChangelogDir: DirectoryProperty

    init {
        group = "documentation"
        subChangelogDir.convention(project.layout.projectDirectory.dir(".changelog"))
    }

    @TaskAction
    fun action() {
        val tag = tag.get()
        val versions = versions.get()

        val subChangelogDirFile = subChangelogDir.get()
        subChangelogDirFile.asFile.mkdirs()

        val subChangeLogFile = subChangelogDirFile.file("$tag.md")
        subChangeLogFile.asFile.also {
            if (!it.exists()) {
                it.createNewFile()
            }

            if (versions.isNotEmpty()) {
                it.printWriter(Charsets.UTF_8).use { writer ->
                    writer.println("**版本信息**")
                    writer.println()
                    writer.println("> 部分相关的构建版本信息")
                    writer.println()
                    writer.println("| 依赖 | 版本 |")
                    writer.println("| ---- | ---- |")
                    versions.forEach { (k, v) ->
                        writer.println("| $k | `v$v` |")
                    }
                    writer.println()
                }
            }
        }
    }
}

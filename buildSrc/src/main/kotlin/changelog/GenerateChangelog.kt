/*
 *     Copyright (c) 2023-2025. ForteScarlet.
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
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import java.io.FileWriter
import java.util.*

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
                    writer.println("")
                    writer.println("> 部分相关的构建版本信息")
                    writer.println("")
                    writer.println("| 依赖 | 版本 |")
                    writer.println("| ---- | ---- |")
                    versions.forEach { (k, v) ->
                        writer.println("| $k | `v$v` |")
                    }
                    writer.println("")
                }
            }
        }

    }
}

/**
 * 根据 git 提交记录更新 CHANGELOG 文件。
 */
@Suppress("MaxLineLength", "ArgumentListWrapping")
abstract class GenerateChangelogTask : DefaultTask() {
    private data class CommitTagLogs(val logs: LinkedList<CommitLog> = LinkedList(), val compare: String? = null)

    private data class CommitLog(
        val message: String,
        val hashes: LinkedList<String> = LinkedList(),
        val pre: String?,
        val compare: String? = null,
    )

    @get:Input
    abstract val newestTag: Property<String>

    @get:OutputDirectory
    abstract val tempOutputDir: DirectoryProperty

    @get:OutputFile
    abstract val changelogOutput: RegularFileProperty

    init {
        group = "documentation"
        tempOutputDir.convention(project.layout.buildDirectory.dir("tmp/updateChangelog"))
        changelogOutput.convention(project.rootProject.layout.projectDirectory.file("CHANGELOG.md"))
    }

    @TaskAction
    fun action() {
        val newestTag = newestTag.get()
        logger.info("Generate change log for {} ...", newestTag)
        val tempOutput = tempOutputDir.file("CHANGELOG.tmp${System.currentTimeMillis()}.md").get()
        val tempFile = tempOutput.asFile
        if (!(tempFile.exists())) {
            tempFile.createNewFile()
        }
        // Write empty
        tempFile.writeText("")

        val tagList = ByteArrayOutputStream().use { commandOutput ->
            project.rootProject.exec {
                commandLine("git", "tag", "--sort=-committerdate")
                standardOutput = commandOutput
            }

            val tagSet = linkedSetOf<String>()

            for (lineTag in commandOutput.toString().lines()) {
                if (!lineTag.startsWith('v')) {
                    continue
                }

                if (lineTag.contains("preview", true) || lineTag.contains("dev", true)) {
                    continue
                }

                tagSet.add(lineTag)
            }

            tagSet
        }

        val tags = tagList.toList()

        val commitLogs = LinkedHashMap<String, CommitTagLogs>()
        val excludes = listOf("release", "submodule", "ci", "chore", "doc")

        fun recordTagLogLines(tag: String, command: String?) {
            val tagLogLines = ByteArrayOutputStream().use { output ->
                if (command != null) {
                    project.rootProject.exec {
                        val commandList = mutableListOf(
                            "git",
                            "log",
                            "--no-merges",
                            "--oneline", // 以简洁的一行格式显示提交信息
                            "--abbrev-commit", // 使用短提交哈希值
                            command
                        )
                        commandLine(commandList)
                        standardOutput = output
                    }
                }

                output.toString().lines()
            }
            // previous = tag

            val commitTagLogs = commitLogs.computeIfAbsent(tag) {
                CommitTagLogs(compare = command)
            }

            val commitLogList = commitTagLogs.logs

            for (logLine in tagLogLines) {
                if (logLine.isBlank()) continue
                val split = logLine.trim().split(" ", limit = 2)
                val hash = split[0]
                val message = split.getOrNull(1)?.trim() ?: continue

                if (excludes.any { message.startsWith(it) }) {
                    continue
                }


                fun add(pre: String?) {
                    commitLogList.addLast(
                        CommitLog(
                            message = message,
                            pre = pre,
                            compare = command
                        ).apply {
                            hashes.add(hash)
                        }
                    )
                }

                if (commitLogList.isEmpty()) {
                    // 看看上一个 tag 的
                    val iter = commitLogs.iterator()
                    var previousTagList: CommitTagLogs? = null
                    var previousEntry: Map.Entry<String, CommitTagLogs>? = null
                    while (iter.hasNext()) {
                        val next = iter.next()
                        if (previousEntry?.key == tag) {
                            previousTagList = previousEntry.value
                            break
                        }
                        previousEntry = next
                    }

                    add(previousTagList?.logs?.lastOrNull()?.hashes?.last())
                } else {
                    val last = commitLogList.last()
                    if (last.message == message) {
                        last.hashes.addFirst(hash)
                    } else {
                        add(last.hashes.last())
                    }
                }
            }
        }

        val firstTag = tags.firstOrNull()
        if (firstTag != null) {
            recordTagLogLines(newestTag, "$firstTag..HEAD")
        }

        tags.forEach { println("\t$it") }

        val iter = tags.listIterator()
        while (iter.hasNext()) {
            // tag的内容，是上一个tag..当前这个tag, 也就是 previousTag..currentTag
            val currentTag = iter.next()
            if (iter.hasNext()) {
                val previousTag = tags[iter.nextIndex()]
                recordTagLogLines(currentTag, "$previousTag..$currentTag")
            } else {
                // 最后也就是最初的tag
                // recordTagLogLines(currentTag, currentTag)
                // 直接忽略它
            }

        }

        FileWriter(tempFile, true).buffered().use { writer ->
            writer.appendLine("# CHANGELOG")
            writer.appendLine("> 由自动任务基于Git提交记录生成，详细更新内容请参考对应版本的 release 。")
            writer.appendLine()

            for ((tag, commitTagLogs) in commitLogs) {
                val commitLogList = commitTagLogs.logs

                writer.appendLine("## $tag")
                writer.appendLine()
                writer.appendLine(
                    "> Release & Pull Notes: " +
                        link(tag, "https://github.com/simple-robot/simpler-robot/releases/tag/$tag")
                )
                val compare = commitTagLogs.compare?.replace("HEAD", newestTag)
                if (compare != null) {
                    writer.appendLine(">")
                    writer.appendLine(
                        "> Commit compare: " +
                            link(compare, "https://github.com/simple-robot/simpler-robot/compare/$compare")
                    )
                }
                writer.appendLine()
                for ((message, hashList, preHash) in commitLogList) {
                    val firstHash = hashList[0]
                    val hashLink = if (hashList.size == 1) {
                        link("`$firstHash`", "https://github.com/simple-robot/simpler-robot/commit/$firstHash")
                    } else {
                        val pre = firstHash
                        val post: String = preHash ?: "HEAD"
                        link(
                            "`$pre..${hashList.last()}`",
                            "https://github.com/simple-robot/simpler-robot/compare/$pre..$post"
                        )
                    }
                    writer.appendLine("- $hashLink: $message")
                }
                writer.newLine()
            }
        }

        val changelogFile = changelogOutput.asFile.get()
        if (!changelogFile.exists()) {
            changelogFile.createNewFile()
        }

        tempFile.copyTo(changelogFile, overwrite = true)
        tempFile.deleteOnExit()
    }

    private fun link(name: String, link: String): String {
        return "[$name]($link)"
    }
}

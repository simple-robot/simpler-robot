/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package changelog

import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileWriter
import java.io.RandomAccessFile
import java.nio.file.Files
import java.util.*

data class CommitLog(val message: String, val hash: MutableList<String>, val pre: String?)

fun Project.generateChangelog(tag: String) {
    println("Generate change log for $tag ...")
    // configurations.runtimeClasspath
    val changelogDir = rootProject.file(".changelog").also {
        it.mkdirs()
    }

    val file = File(changelogDir, "$tag.md")
    if (!file.exists()) {
        file.createNewFile()
    }

    val rootChangelogFile = rootProject.file("CHANGELOG.md").also {
        if (!file.exists()) {
            file.createNewFile()
        }
    }

    // 获取上一个tag
    var firstTag: String? = null
    var currentTag = false
    val lastTag = ByteArrayOutputStream().use { output ->
        rootProject.exec {
            commandLine("git", "tag", "--sort=-committerdate")
            standardOutput = output
        }

        var lastTag: String? = null

        for (lineTag in output.toString().lines()) {
            if (!lineTag.startsWith('v')) {
                continue
            }

            if (lineTag == tag) {
                currentTag = true
                continue
            }

            if (firstTag == null) {
                firstTag = lineTag
            }

            if (currentTag) {
                // first after current tag
                lastTag = lineTag
                break
            }

        }

        if (lastTag == null) {
            lastTag = firstTag
        }

        lastTag
    }



    ByteArrayOutputStream().use { output ->
        rootProject.exec {
            val commandList = mutableListOf("git", "log", "--no-merges", "--oneline").apply {
                if (lastTag != null) {
                    if (currentTag) add("$lastTag..$tag") else add("$lastTag..HEAD")
                }
            }
            commandLine(commandList)
            standardOutput = output
        }


        val lines = LinkedList<CommitLog>()
        // 不要:
        val excludes = listOf("release", "submodule", "ci", "chore")
        val match = Regex("((?!(${excludes.joinToString("|")}))[a-zA-Z0-9-_]+)(\\(.+\\))?: *.+")

        output.toString()
            .lines()
            .asReversed()
            .asSequence()
            .filter { line ->
                line.isNotEmpty()
            }.mapNotNull { line ->
                val split = line.trim().split(" ", limit = 2)
                val hash = split[0]
                val message = split.getOrNull(1)?.trim() ?: return@mapNotNull null

                hash to message
            }.filter { (_, message) ->
                if (message.startsWith("release")) {
                    return@filter false
                }

                match.matches(message)
            }.forEach { (hash, message) ->
                fun add(pre: String?) {
                    lines.addLast(CommitLog(message, mutableListOf(hash), pre))
                }

                if (lines.isEmpty()) {
                    add(null)
                } else {
                    val last = lines.last
                    if (last.message == message) {
                        last.hash.add(0, hash)
                    } else {
                        add(last.hash.last())
                    }
                }
            }

        val tmpDir = rootProject.buildDir.resolve("tmp/changelog").apply { mkdirs() }

        val tmpFile =
            Files.createTempFile(tmpDir.toPath(), "changelog", "tmp").toFile()

        // copy source file to tmp file
        RandomAccessFile(rootChangelogFile, "r").channel.use { srcChannel ->
            RandomAccessFile(tmpFile, "rw").channel.use { destChannel ->
                srcChannel.transferTo(0, srcChannel.size(), destChannel)
            }
        }

        FileWriter(rootChangelogFile).buffered().use { writer ->
            writer.appendLine("# $tag")
            writer.appendLine(
                """
                
                > Release & Pull Notes: [$tag](https://github.com/simple-robot/simpler-robot/releases/tag/$tag) 
                
            """.trimIndent()
            )

            lines.asReversed()
                .forEach { (message, hashList, preHash) ->
                    if (hashList.size == 1) {
                        writer.appendLine("- $message ([`${hashList[0]}`](https://github.com/simple-robot/simpler-robot/commit/${hashList[0]}))")
                    } else {
                        val pre = hashList[0]
                        val post: String = preHash ?: lastTag ?: "HEAD"
                        writer.appendLine("- $message ([`$pre..${hashList.last()}`](https://github.com/simple-robot/simpler-robot/compare/$pre..$post))")
                        writer.newLine()
                        writer.appendLine("<details><summary>$pre..$post</summary>")
                        writer.newLine()
                        hashList.forEach { hash ->
                            writer.appendLine("    - [`$hash`](https://github.com/simple-robot/simpler-robot/commit/$hash))")
                        }
                        writer.newLine()
                        writer.appendLine("</details>")


                        /*
                        <details><summary>ec1e591a..cd1a6df5</summary>

                                - 1

                        - 2
                        - 3

                        </details>
                         */


                    }
                }

            writer.newLine()
        }

        tmpFile.inputStream().use { input ->
            FileWriter(rootChangelogFile, true).use { writer ->
                var skip = false
                input.bufferedReader().use { r ->
                    r.lineSequence().forEach { oldLine ->
                        when {
                            skip && oldLine.startsWith('#') && oldLine != "# $tag" -> {
                                skip = false
                            }

                            oldLine.trim() == "# $tag" -> {
                                skip = true
                            }

                            !skip -> {
                                writer.appendLine(oldLine)
                            }
                        }
                    }
                }
            }
        }
    }

}

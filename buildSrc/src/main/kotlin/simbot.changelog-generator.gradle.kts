/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (即 simple robot的v3版本，因此亦可称为 simple-robot v3 、simbot v3 等) 的一部分。
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


tasks.create("createChangelog") {
    group = "documentation"
    doFirst {
        val realVersion = rootProject.version.toString()
        val version = "v$realVersion"
        println("Generate change log for $version ...")
        // configurations.runtimeClasspath
        val changelogDir = rootProject.file(".changelog").also {
            it.mkdirs()
        }
        
        val file = File(changelogDir, "$version.md")
        if (!file.exists()) {
            file.createNewFile()
            
            val autoGenerateText = buildString {
                appendLine("<details>")
                appendLine("<summary><b>仓库参考</b></summary>")
                appendLine()
                
                appendLine("| **模块** | **search.maven** |")
                appendLine("|---------|------------------|")
                
                rootProject.subprojects
                    .filter { project ->
                        project.extensions.findByType<PublishingExtension>() != null
                    }.forEach { project ->
                        // not publishable
                        val multiplatformExtension =
                            project.extensions.findByName("kotlin") as? KotlinMultiplatformExtension
                        if (multiplatformExtension != null) {
                            //val group = P.findProjectDetailByGroup(project.group.toString())
                            repoRowMulti(
                                multiplatformExtension,
                                project.name,
                                project.group.toString(),
                                project.name,
                                realVersion
                            )
                        } else {
                            repoRow(project.name, project.group.toString(), project.name, realVersion)
                        }
                    }

//                repoRowMulti(simbotLoggerKotlin, "simbot-logger", "love.forte.simbot", "simbot-logger", realVersion)
//                repoRow("simbot-api", "love.forte.simbot", "simbot-api", realVersion)
//                repoRow("simbot-core", "love.forte.simbot", "simbot-core", realVersion)
//                repoRow("simboot-api", "love.forte.simbot.boot", "simboot-api", realVersion)
//                repoRow("simboot-core", "love.forte.simbot.boot", "simboot-core", realVersion)
//                repoRow("simboot-core-annotation", "love.forte.simbot.boot", "simboot-core-annotation", realVersion)
//                repoRow(
//                    "simboot-core-spring-boot-starter",
//                    "love.forte.simbot.boot",
//                    "simboot-core-spring-boot-starter",
//                    realVersion
//                )
                
                appendLine()
                appendLine("</details>")
            }
            
            file.writeText(autoGenerateText)
        }
    }
}



fun StringBuilder.repoRow(moduleName: String, group: String, id: String, version: String) {
    append("| ").append(moduleName)
    append(" | [")
    append("v").append(version)
    append("](https://search.maven.org/artifact/")
    append(group).append("/").append(id).append("/").append(version).append("/jar)  |")
    appendLine()
}

private val multiPlatformType = setOf(
    KotlinPlatformType.common,
    KotlinPlatformType.jvm,
    KotlinPlatformType.js,
)

fun StringBuilder.repoRowMulti(
    kotlin: KotlinMultiplatformExtension,
    moduleName: String,
    group: String,
    id: String,
    version: String,
) {
    kotlin.targets.filter {
        it.platformType in multiPlatformType
    }.sortedBy {
        it.platformType
    }.forEach {
        when (it.platformType) {
            KotlinPlatformType.common ->
                repoRow(moduleName, group, id, version)
            
            else ->
                repoRow("$moduleName-${it.targetName}", group, "$id-${it.targetName.toLowerCase()}", version)
        }
        
    }
    
}

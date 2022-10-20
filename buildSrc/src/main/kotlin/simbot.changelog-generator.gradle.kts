/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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
        
        val simbotLoggerKotlin =
            project(":simbot-logger").extensions.getByName<KotlinMultiplatformExtension>(
                "kotlin"
            )
        
        val file = File(changelogDir, "$version.md")
        if (!file.exists()) {
            file.createNewFile()
            
            val autoGenerateText = buildString {
                appendLine("**仓库参考:**").appendLine()
                
                appendLine("<details>")
                appendLine("<summary><b>仓库参考</b></summary>")
                appendLine()
                
                appendLine("| **模块** | **repo1.maven** | **search.maven** |")
                appendLine("|---------|-----------------|------------------|")
                repoRowMulti(simbotLoggerKotlin, "simbot-logger", "love.forte.simbot", "simbot-logger", realVersion)
                repoRow("simbot-api", "love.forte.simbot", "simbot-api", realVersion)
                repoRow("simbot-core", "love.forte.simbot", "simbot-core", realVersion)
                repoRow("simboot-api", "love.forte.simbot.boot", "simboot-api", realVersion)
                repoRow("simboot-core", "love.forte.simbot.boot", "simboot-core", realVersion)
                repoRow("simboot-core-annotation", "love.forte.simbot.boot", "simboot-core-annotation", realVersion)
                repoRow("simboot-core-spring-boot-starter", "love.forte.simbot.boot", "simboot-core-spring-boot-starter", realVersion)
                
                appendLine()
                appendLine("</details>")
            }
            
            // val autoGenerateText0 = """
            //     **仓库参考:**
            //
            //     | **模块** | **repo1.maven** | **search.maven** |
            //     |---------|-----------------|------------------|
            //     ${repoRow("simbot-api", "love.forte.simbot", "simbot-api", realVersion)}
            //     ${repoRow("simbot-core", "love.forte.simbot", "simbot-core", realVersion)}
            //     ${repoRow("simbot-logger-jvm", "love.forte.simbot", "simbot-logger-jvm", realVersion)}
            //     ${repoRow("simbot-logger-js", "love.forte.simbot", "simbot-logger-js", realVersion)}
            //     ${repoRow("simbot-logger-macosx64", "love.forte.simbot", "simbot-logger-macosx64", realVersion)}
            //     ${repoRow("simbot-logger-linuxx64", "love.forte.simbot", "simbot-logger-linuxx64", realVersion)}
            //     ${repoRow("simbot-logger-mingwx64", "love.forte.simbot", "simbot-logger-mingwx64", realVersion)}
            //     ${repoRow("simboot-api", "love.forte.simbot.boot", "simboot-api", realVersion)}
            //     ${repoRow("simboot-core", "love.forte.simbot.boot", "simboot-core", realVersion)}
            //     ${repoRow("simboot-core-annotation", "love.forte.simbot.boot", "simboot-core-annotation", realVersion)}
            //     ${
            //     repoRow(
            //         "simboot-core-spring-boot-starter",
            //         "love.forte.simbot.boot",
            //         "simboot-core-spring-boot-starter",
            //         realVersion
            //     )
            // }
            //
            //
            //
            //
            // """.trimIndent()
            
            
            file.writeText(autoGenerateText)
        }
        
        /*
                        **仓库参考:**
                
                * [simbot-api: $version](https://repo1.maven.org/maven2/love/forte/simbot/simbot-api/$realVersion)
                * [simbot-core: $version](https://repo1.maven.org/maven2/love/forte/simbot/simbot-core/$realVersion)
                * [simbot-logger: $version](https://repo1.maven.org/maven2/love/forte/simbot/simbot-logger/$realVersion)
                * [simboot-api: $version](https://repo1.maven.org/maven2/love/forte/simbot/boot/simboot-api/$realVersion)
                * [simboot-core: $version](https://repo1.maven.org/maven2/love/forte/simbot/boot/simboot-core/$realVersion)
                * [simboot-core-annotation: $version](https://repo1.maven.org/maven2/love/forte/simbot/boot/simboot-core-annotation/$realVersion)
                * [simboot-core-spring-boot-starter: $version](https://repo1.maven.org/maven2/love/forte/simbot/boot/simboot-core-spring-boot-starter/$realVersion)
                
         */
        
        //                 ## 组件更新
        //                 相关组件会在后续三日内跟进更新
        //                 - [mirai组件](https://github.com/simple-robot/simbot-component-mirai/releases)
        //                 - [腾讯频道组件](https://github.com/simple-robot/simbot-component-tencent-guild/releases)
        //                 - [开黑啦组件](https://github.com/simple-robot/simbot-component-kaiheila/releases)
        
    }
}

// fun repoRow(moduleName: String, group: String, id: String, version: String): String {
//     return "| $moduleName | [$moduleName: v$version](https://repo1.maven.org/maven2/${
//         group.replace(
//             ".",
//             "/"
//         )
//     }/${
//         id.replace(
//             ".",
//             "/"
//         )
//     }/$version) | [$moduleName: v$version](https://search.maven.org/artifact/$group/$id/$version/jar)  |"
// }
//
// fun repoRowMulti(
//     kotlin: KotlinMultiplatformExtension,
//     moduleName: String,
//     group: String,
//     id: String,
//     version: String,
// ): String {
//     kotlin.targets.forEach {
//         println("simbot-logger target: $it")
//     }
//
//     return repoRow(moduleName, group, id, version)
// }


fun StringBuilder.repoRow(moduleName: String, group: String, id: String, version: String) {
    append("| ").append(moduleName).append(" | [").append(moduleName).append(": v").append(version)
    append("](https://repo1.maven.org/maven2/")
    append(group.replace(".", "/"))
    append('/')
    append(id.replace(".", "/"))
    append('/')
    append(version)
    append(") | [")
    append(moduleName).append(": v").append(version)
    append("](https://search.maven.org/artifact/")
    append(group).append("/").append(id).append("/").append(version).append("/jar)  |")
    appendLine()
    // "| $moduleName | [$moduleName: v$version](https://repo1.maven.org/maven2/${
    //     group.replace(
    //         ".",
    //         "/"
    //     )
    // }/${
    //     id.replace(
    //         ".",
    //         "/"
    //     )
    // }/$version) | [$moduleName: v$version](https://search.maven.org/artifact/$group/$id/$version/jar)  |"
}

fun StringBuilder.repoRowMulti(
    kotlin: KotlinMultiplatformExtension,
    moduleName: String,
    group: String,
    id: String,
    version: String,
) {
    kotlin.targets.filter {
        it.platformType != KotlinPlatformType.common
    }.sortedBy {
        it.platformType
    }.forEach {
        repoRow("$moduleName-${it.targetName}", group, "$id-${it.targetName.toLowerCase()}", version)
    }
    
}
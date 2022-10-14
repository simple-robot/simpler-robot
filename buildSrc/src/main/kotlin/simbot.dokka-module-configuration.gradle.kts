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

import love.forte.gradle.common.core.repository.Repositories
import java.net.URL

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

plugins {
    id("org.jetbrains.dokka")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri(Repositories.Snapshot.URL)
        mavenContent {
            snapshotsOnly()
        }
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        version = P.Simbot.versionWithoutSnapshot
        configureEach {
            skipEmptyPackages.set(true)
            jdkVersion.set(8)
            reportUndocumented.set(true)
            if (project.file("Module.md").exists()) {
                includes.from("Module.md")
            }
            displayName.set(project.name)
            
            perPackageOption {
                matchingRegex.set(""".*\.internal.*""") // will match all .internal packages and sub-packages
                suppress.set(true)
            }
    
            sourceLink {
                // Unix based directory relative path to the root of the project (where you execute gradle respectively).
                localDirectory.set(file("src/main/kotlin"))
                val baseUrl = "https://github.com/forteScarlet/simpler-robot"
                val modelPath = project.path.replace(":", "/")
                // URL showing where the source code can be accessed through the web browser
                remoteUrl.set(URL(
                    "$baseUrl/tree/v3-dev$modelPath/src/main/kotlin"))
                // Suffix which is used to append the line number to the URL. Use #L for GitHub
                remoteLineSuffix.set("#L")
            }
        }
    }
}


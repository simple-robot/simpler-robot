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

import org.jetbrains.dokka.DokkaConfiguration
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

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
        version = P.Simbot.versionWithoutSnapshot
        documentedVisibilities.set(listOf(DokkaConfiguration.Visibility.PUBLIC, DokkaConfiguration.Visibility.PROTECTED))
        jdkVersion.set(8)
        if (project.file("Module.md").exists()) {
            includes.from("Module.md")
        } else if (project.file("README.md").exists()) {
            includes.from("README.md")
        }
    
        
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            val relativeTo = projectDir.relativeTo(rootProject.projectDir)
            remoteUrl.set(URL("${P.HOMEPAGE}/tree/v3-main/$relativeTo/src"))
            remoteLineSuffix.set("#L")
        }
    
        perPackageOption {
            matchingRegex.set(".*internal.*") // will match all .internal packages and sub-packages
            suppress.set(true)
        }
    
        fun externalDocumentation(docUrl: URL, suffix: String = "package-list") {
            externalDocumentationLink {
                url.set(docUrl)
                packageListUrl.set(URL(docUrl, "${docUrl.path}/$suffix"))
            }
        }
    
        // kotlin-coroutines doc
        externalDocumentation(URL("https://kotlinlang.org/api/kotlinx.coroutines"))
    
        // kotlin-serialization doc
        externalDocumentation(URL("https://kotlinlang.org/api/kotlinx.serialization"))
        
        // SLF4J
        externalDocumentation(URL("https://www.slf4j.org/apidocs"))
        
        // Spring Framework
        externalDocumentation(URL("https://docs.spring.io/spring-framework/docs/current/javadoc-api"), "element-list")
        
        // Spring Boot
//        externalDocumentation(URL("https://docs.spring.io/spring-boot/docs/current/api/element-list"))
    
        
        
    }
}


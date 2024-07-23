/*
 *     Copyright (c) 2022-2024. ForteScarlet.
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

import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import java.net.URI
import java.time.Year


plugins {
    id("org.jetbrains.dokka")
}

tasks.named("dokkaHtml").configure {
    tasks.findByName("kaptKotlin")?.also { kaptKotlinTask ->
        dependsOn(kaptKotlinTask)
    }
    tasks.findByName("kspKotlin")?.also { kspKotlinTask ->
        dependsOn(kspKotlinTask)
    }
    tasks.findByName("kspKotlinJvm")?.also { kspKotlinTask ->
        dependsOn(kspKotlinTask)
    }
}
tasks.named("dokkaHtmlPartial").configure {
    tasks.findByName("kaptKotlin")?.also { kaptKotlinTask ->
        dependsOn(kaptKotlinTask)
    }
    tasks.findByName("kspKotlin")?.also { kspKotlinTask ->
        dependsOn(kspKotlinTask)
    }
    tasks.findByName("kspKotlinJvm")?.also { kspKotlinTask ->
        dependsOn(kspKotlinTask)
    }
}

@Suppress("MaxLineLength")
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customAssets = listOf(
            rootProject.file(".simbot/dokka-assets/logo-icon.svg"),
            rootProject.file(".simbot/dokka-assets/logo-icon-light.svg"),
        )
        customStyleSheets = listOf(rootProject.file(".simbot/dokka-assets/css/kdoc-style.css"))
        if (!isSimbotLocal()) {
            templatesDir = rootProject.file(".simbot/dokka-templates")
        }
        footerMessage = "© 2021-${Year.now().value} <a href='https://github.com/simple-robot'>Simple Robot</a>. All rights reserved."
        separateInheritedMembers = true
        mergeImplicitExpectActualDeclarations = true
        homepageLink = P.HOMEPAGE
    }

    if (isSimbotLocal()) {
        logger.info("Is 'SIMBOT_LOCAL', offline")
        offlineMode.set(true)
    }

    dokkaSourceSets.configureEach {
        skipEmptyPackages.set(true)
        suppressGeneratedFiles.set(false)

        version = P.Simbot.version
        documentedVisibilities.set(
            listOf(
                DokkaConfiguration.Visibility.PUBLIC,
                DokkaConfiguration.Visibility.PROTECTED
            )
        )

        val jdkVersionValue = project.tasks.withType(JavaCompile::class.java).firstOrNull()
            ?.targetCompatibility?.toInt().also {
                logger.info("project {} found jdkVersionValue: {}", project, it)
            } ?: JVMConstants.KT_JVM_TARGET_VALUE

        jdkVersion.set(jdkVersionValue)
        val moduleFile = project.file("Module.md")
        if (moduleFile.exists()) {
            if (moduleFile.length() > 0) {
                includes.from("Module.md")
            }
        }

        sourceLink {
            localDirectory.set(File(projectDir, "src")) // .resolve("src")
            val relativeTo = projectDir.relativeTo(rootProject.projectDir).toString()
                .replace('\\', '/')
            // remoteUrl.set(URI.create("${P.HOMEPAGE}/tree/v4-dev/$relativeTo/src/").toURL())
            remoteUrl.set(URI.create("${P.HOMEPAGE}/tree/v4-dev/$relativeTo/src/").toURL())
            remoteLineSuffix.set("#L")
        }

        perPackageOption {
            matchingRegex.set(".*internal.*") // will match all .internal packages and sub-packages
            suppress.set(true)
        }

        fun externalDocumentation(docUrl: URI, suffix: String = "package-list") {
            externalDocumentationLink {
                url.set(docUrl.toURL())
                packageListUrl.set(docUrl.resolve(suffix).toURL())
            }
        }

        if (!isSimbotLocal()) {
            // kotlin-coroutines doc
            externalDocumentation(URI.create("https://kotlinlang.org/api/kotlinx.coroutines/"))

            // kotlin-serialization doc
            externalDocumentation(URI.create("https://kotlinlang.org/api/kotlinx.serialization/"))

            // ktor
            externalDocumentation(URI.create("https://api.ktor.io/"))

            // SLF4J
            externalDocumentation(URI.create("https://www.slf4j.org/apidocs/"), "element-list")

            // Spring Framework
            externalDocumentation(
                URI.create("https://docs.spring.io/spring-framework/docs/current/javadoc-api/"),
                "element-list"
            )

            // Spring Boot
            externalDocumentation(
                URI.create("https://docs.spring.io/spring-boot/docs/current/api/"),
                "element-list"
            )
        }

    }
}


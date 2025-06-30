/*
 *     Copyright (c) 2025. ForteScarlet.
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

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.DokkaSourceSetSpec
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import java.io.File
import java.net.URI
import java.time.Year
import kotlin.jvm.optionals.getOrElse


fun DokkaExtension.configSourceSets(project: Project) {
    dokkaSourceSets.configureEach {
        skipEmptyPackages.set(true)
        suppressGeneratedFiles.set(false)
        documentedVisibilities(
            VisibilityModifier.Public,
            VisibilityModifier.Protected
        )
        val targetCompatibility = project.tasks.withType(JavaCompile::class.java).firstOrNull()?.targetCompatibility

        logger.info("project {} found jdkVersionValue: {}", project, targetCompatibility)

        when (targetCompatibility) {
            "1.8" -> {
                jdkVersion.set(8)
            }
            null -> {
                // Do nothing here.
            }
            else -> {
                jdkVersion.set(targetCompatibility.toInt())
            }
        }

        configModuleMdInclude(project)

        perPackageOption {
            matchingRegex.set(".*internal.*") // will match all .internal packages and sub-packages
            suppress.set(true)
        }

        if (!isSimbotLocal()) {
            configSourceLink(project)
            configExternalDocumentations(project)
        }
    }
}

fun DokkaSourceSetSpec.configModuleMdInclude(project: Project) {
    val moduleFile = project.file("Module.md")
    if (moduleFile.exists() && moduleFile.length() > 0) {
        includes.from("Module.md")
    }
}

fun DokkaSourceSetSpec.configSourceLink(project: Project) {
    sourceLink {
        localDirectory.set(File(project.projectDir, "src"))
        val relativeTo = project.projectDir.relativeTo(project.rootProject.projectDir).toString()
            .replace('\\', '/')
        remoteUrl.set(URI.create("${P.HOMEPAGE}/tree/dev/main/$relativeTo/src"))
        remoteLineSuffix.set("#L")
    }
}

fun DokkaSourceSetSpec.configExternalDocumentations(project: Project) {
    fun externalDocumentation(name: String, docUrl: URI, suffix: String = "package-list") {
        externalDocumentationLinks.register(name) {
            url.set(docUrl)
            packageListUrl.set(docUrl.resolve(suffix))
        }
    }

    // kotlin-coroutines doc
    externalDocumentation(
        "kotlinx.coroutines",
        URI.create("https://kotlinlang.org/api/kotlinx.coroutines/")
    )

    // kotlin-serialization doc
    externalDocumentation(
        "kotlinx.serialization",
        URI.create("https://kotlinlang.org/api/kotlinx.serialization/")
    )

    // ktor
    externalDocumentation(
        "ktor",
        URI.create("https://api.ktor.io/")
    )

    // SLF4J
    externalDocumentation(
        "slf4j",
        URI.create("https://www.slf4j.org/apidocs/"),
        "element-list"
    )

    // Spring Framework
    // TODO 准确的版本号?
    externalDocumentation(
        "spring-framework",
        URI.create("https://docs.spring.io/spring-framework/docs/current/javadoc-api/"),
        "element-list"
    )

    val versionCatalog: VersionCatalog = project.versionCatalog()

    val springBootV3Version = versionCatalog.findVersion("spring-boot-v3")
        .map { it.toString() }
        .getOrElse { "current" }

    println("springBootV3Version: $springBootV3Version")

    // Spring Boot
    externalDocumentation(
        "spring-boot",
        URI.create("https://docs.spring.io/spring-boot/docs/$springBootV3Version/api/"),
        "element-list"
    )
}

fun DokkaHtmlPluginParameters.configHtmlCustoms(project: Project) {
    customAssets.from(
        project.rootProject.file(".simbot/dokka-assets/logo-icon.svg"),
        project.rootProject.file(".simbot/dokka-assets/logo-icon-light.svg"),
    )

    customStyleSheets.from(project.rootProject.file(".simbot/dokka-assets/css/kdoc-style.css"))

    if (!isSimbotLocal()) {
        templatesDir.set(project.rootProject.file(".simbot/dokka-templates"))
    }

    val now = Year.now().value
    footerMessage.set(
        "© 2024-$now " +
            "<a href='https://github.com/simple-robot'>Simple Robot</a>. All rights reserved."
    )

    separateInheritedMembers.set(true)
    mergeImplicitExpectActualDeclarations.set(true)
    homepageLink.set(P.HOMEPAGE)
}

private fun ExtensionAware.versionCatalog(name: String = "libs"): VersionCatalog {
    return extensions.getByType<VersionCatalogsExtension>().named(name)
}

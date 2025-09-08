/*
 *     Copyright (c) 2024-2025. ForteScarlet.
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

import changelog.GenerateChangelogTask
import changelog.GenerateSubChangelogTask
import love.forte.plugin.suspendtrans.gradle.SuspendTransformPluginExtension

plugins {
    idea
    id("org.jetbrains.dokka")
    // id("simbot.dokka-multi-module")
    id("com.github.gmazzo.buildconfig") version "5.6.8" apply false
    alias(libs.plugins.detekt)
    id("simbot.nexus-publish")
    id("love.forte.plugin.suspend-transform") apply false
    // alias(libs.plugins.suspendTransform) apply false

    // https://www.jetbrains.com/help/qodana/code-coverage.html
    // https://github.com/Kotlin/kotlinx-kover
    alias(libs.plugins.kotlinxKover)

    alias(libs.plugins.kotlinxBinaryCompatibilityValidator)


}

setupGroup(P.Simbot)

// https://github.com/detekt/detekt/blob/main/build.gradle.kts

repositories {
    mavenCentral()
    love.forte.gradle.common.core.repository.Repositories.Snapshot.Default.apply {
        configMaven {
            mavenContent {
                snapshotsOnly()
            }
        }
    }
    // mavenLocal()
}

val root = project

allprojects {
    group = P.GROUP
    version = if (isSnapshot()) P.NEXT_SNAPSHOT_VERSION else P.VERSION
    description = P.DESCRIPTION
}

subprojects {
    repositories {
        mavenCentral()
        love.forte.gradle.common.core.repository.Repositories.Snapshot.Default.apply {
            configMaven {
                mavenContent {
                    snapshotsOnly()
                }
            }
        }
        // mavenLocal()
    }

    afterEvaluate {
        applyKover(root)

        if (plugins.hasPlugin(libs.plugins.suspendTransform.get().pluginId)) {
            configureSuspendTransform()
        }
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${libs.versions.detekt.get()}")
}

//region config detekt
detekt {
    source.setFrom(subprojects.map { it.projectDir.absoluteFile })
    config.setFrom(rootDir.resolve("config/detekt/detekt.yml"))
    baseline = rootDir.resolve("config/detekt/baseline.xml")
    // buildUponDefaultConfig = true
    parallel = true
    reportsDir = rootProject.layout.buildDirectory.dir("reports/detekt").get().asFile
    if (!isCi) {
        autoCorrect = true
    }
    basePath = projectDir.absolutePath
}

// https://detekt.dev/blog/2019/03/03/configure-detekt-on-root-project/
tasks.detekt {
    // internal 处理器不管
    exclude("internal-processors/**")
    // tests 不管
    exclude("tests/**")

    include("**/src/*Main/kotlin/**/*.kt")
    include("**/src/*Main/kotlin/**/*.java")
    include("**/src/*Main/java/**/*.kt")
    include("**/src/*Main/java/**/*.java")
    include("**/src/main/kotlin/**/*.kt")
    include("**/src/main/kotlin/**/*.java")
    include("**/src/main/java/**/*.kt")
    include("**/src/main/java/**/*.java")

    exclude("**/src/*/resources/")
    exclude("**/build/")
    exclude("**/*Test/kotlin/")
    exclude("**/*Test/java/")
    exclude("**/test/kotlin/")
    exclude("**/test/java/")
}

fun Project.applyKover(rp: Project) {
    val hasKt =
        plugins.hasPlugin("org.jetbrains.kotlin.jvm") ||
            plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")


    if (hasKt) {
        apply(plugin = "org.jetbrains.kotlinx.kover")
        rp.dependencies {
            kover(project(path))
        }
    }
}
//endregion

apiValidation {
    ignoredPackages.add("*.internal.*")

    ignoredProjects.addAll(
        listOf(
            "interface-uml-processor",
            "simbot-test",
            "tests",
            "spring-boot-starter-test",
            "simbot-processor-class-builder-test"
        )
    )

    // 实验性和内部API可能无法保证二进制兼容
    nonPublicMarkers.addAll(
        listOf(
            "love.forte.simbot.annotations.ExperimentalSimbotAPI",
            "love.forte.simbot.annotations.InternalSimbotAPI",
            "love.forte.simbot.resource.ExperimentalIOResourceAPI",
            "love.forte.simbot.extension.continuous.session.ExperimentalContinuousSessionAPI"
        ),
    )

    ignoredClasses.add("love.forte.simbot.suspendrunner.SuspendMarker")
    ignoredClasses.add("love.forte.simbot.suspendrunner.SuspendMarker.Container")
    ignoredClasses.add("love.forte.simbot.suspendrunner.SuspendMarker\$Container")

    apiDumpDirectory = "api"
}

idea {
    module {
        isDownloadSources = true
    }
}

// https://kotlinlang.org/docs/js-project-setup.html#node-js
// rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
//     rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().apply {
//         // CI 中配置环境，不再单独下载
//         // if (isCi) {
//         // download = false
//         // }
//     }
//     // "true" for default behavior
// }

// Changelog

tasks.register<GenerateSubChangelogTask>("createChangelog") {
    tag = "v${P.VERSION}"
    versions.put("Kotlin", libs.versions.kotlin.get())
}

tasks.register<GenerateChangelogTask>("updateChangelog") {
    newestTag = "v${P.VERSION}"
}

// region Suspend Transform configs
fun Project.configureSuspendTransform() {
    extensions.configure<SuspendTransformPluginExtension>("suspendTransformPlugin") {
        includeRuntime = false
        includeAnnotation = false
        addSimbotJvmTransforms()
    }
}
// endregion

// region Dokka
subprojects {
    afterEvaluate {
        val p = this
        if (plugins.hasPlugin(libs.plugins.dokka.get().pluginId)) {
            dokka {
                configSourceSets(p)
                pluginsConfiguration.html {
                    configHtmlCustoms(p)
                }
            }
            rootProject.dependencies.dokka(p)
        }
    }
}

dokka {
    moduleName = "Simple Robot"

    dokkaPublications.all {
        if (isSimbotLocal()) {
            logger.info("Is 'SIMBOT_LOCAL', offline")
            offlineMode = true
        }
    }

    configSourceSets(project)

    pluginsConfiguration.html {
        configHtmlCustoms(project)
    }
}
// endregion

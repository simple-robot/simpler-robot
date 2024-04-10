/*
 *     Copyright (c) 2024. ForteScarlet.
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

import io.gitlab.arturbosch.detekt.Detekt
import love.forte.gradle.common.core.project.setup

plugins {
    idea
    id("simbot.dokka-multi-module")
    id("com.github.gmazzo.buildconfig") version "4.1.2" apply false
    alias(libs.plugins.detekt)
    id("simbot.nexus-publish")
    id("simbot.changelog-generator")

    // https://www.jetbrains.com/help/qodana/code-coverage.html
    // https://github.com/Kotlin/kotlinx-kover
    id("org.jetbrains.kotlinx.kover") version "0.7.6"

}

setup(P.Simbot)

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
    mavenLocal()
}

val root = project

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
        mavenLocal()
    }

    afterEvaluate {
        if (plugins.hasPlugin("io.gitlab.arturbosch.detekt")) {
            return@afterEvaluate
        }

        fun Project.hasKtP(): Boolean {
            return plugins.findPlugin("org.jetbrains.kotlin.jvm") != null ||
                plugins.findPlugin("org.jetbrains.kotlin.multiplatform") != null
        }

        if (hasKtP()) {
//            apply(plugin = "io.gitlab.arturbosch.detekt")
            // applyDetekt()
            if ("gradle" !in name) {
                useK2()
                logger.info("Enable K2 for {}", this)
            }
        }

        applyKover(root)
    }

}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${libs.versions.detekt.get()}")
}

// config detekt
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
tasks.withType<Detekt>().configureEach {
    // internal 处理器不管
    exclude("internal-processors/**")

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

idea {
    module {
        isDownloadSources = true
    }
}



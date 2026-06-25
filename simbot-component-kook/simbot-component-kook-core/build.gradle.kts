/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

import com.google.devtools.ksp.gradle.KspAATask
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("simbot.kook.kotlin-multiplatform-convention")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    id("love.forte.plugin.suspend-transform")
    alias(libs.plugins.ksp)
    `simbot-maven-publish`
}

configJavaCompileWithModule("simbot.component.kook.core")

kotlin {
    compilerOptions {
        optIn.addAll(
            "love.forte.simbot.kook.ExperimentalKookApi",
            "love.forte.simbot.kook.InternalKookApi",
            "love.forte.simbot.component.kook.blacklist.ExperimentalBlacklistApi"
        )
    }

    configKotlinJvm()

    js {
        configJs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        configWasmJs()
    }

    applyTier123(supportKtorClient = true)

    sourceSets {
        commonMain {
            kotlin.srcDir(project.layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))

            dependencies {
                implementation(project(":simbot-api"))
                api(project(":simbot-component-kook:simbot-component-kook-stdlib"))
                api(project(":simbot-commons:simbot-common-annotations"))
                // ktor
                api(libs.ktor.client.contentNegotiation)
                api(libs.ktor.client.ws)
            }
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("reflect"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.serialization.json)
            implementation(project(":simbot-api"))
            implementation(project(":simbot-cores:simbot-core"))
            implementation(project(":simbot-commons:simbot-common-core"))
            implementation(libs.ktor.client.mock)
        }

        jvmTest.dependencies {
            implementation(libs.ktor.client.java)
            implementation(project(":simbot-logger-slf4j2-impl"))
        }

        jsMain.dependencies {
            implementation(project(":simbot-api"))
        }

        nativeMain.dependencies {
            implementation(project(":simbot-api"))
        }

        mingwTest.dependencies {
            implementation(libs.ktor.client.winhttp)
        }
    }
}

dependencies {
    add("kspJvm", project(":internal-processors:kook-api-reader"))
    add("kspCommonMainMetadata", project(":internal-processors:kook-message-element-processor"))
}

ksp {
    arg("kook.api.reader.enable", (!isCi).toString())
    arg("kook.api.finder.event.output", rootDir.resolve("generated-docs/kook-core-event-list.md").absolutePath)
    arg("kook.api.finder.event.class", "love.forte.simbot.component.kook.event.KookEvent")
}

// https://github.com/google/ksp/issues/963#issuecomment-2919262595
tasks.withType<KspAATask>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

tasks.sourcesJar.configure {
    dependsOn("kspCommonMainKotlinMetadata")
}

dokka {
    dokkaSourceSets.configureEach {
        suppressGeneratedFiles = false
    }
}

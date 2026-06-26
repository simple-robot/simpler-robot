/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("simbot.kotlin-multiplatform")
    id("simbot.onebot.kotlin-multiplatform-convention")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    id("love.forte.plugin.suspend-transform")
    alias(libs.plugins.ksp)
    `simbot-maven-publish`
}

configJavaCompileWithModule("simbot.component.onebot11v.message")

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        optIn.addAll(
            "love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI",
            "love.forte.simbot.component.onebot.common.annotations.ExperimentalOneBotAPI"
        )
        freeCompilerArgs.addAll(
            "-Xconsistent-data-class-copy-visibility"
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

    applyTier123()

    sourceSets {
        commonMain {
            kotlin.srcDir(project.layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))

            dependencies {
                implementation(project(":simbot-api"))
                api(project(":simbot-component-onebot:simbot-component-onebot-common"))
                api(project(":simbot-commons:simbot-common-annotations"))

                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.io.core)
                implementation(libs.jetbrains.annotations)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":simbot-cores:simbot-core"))
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        jvmMain {
            dependencies {
                compileOnly(project(":simbot-api"))
                compileOnly(libs.jetbrains.annotations)
            }
        }

        jvmTest.dependencies {
            implementation(libs.log4j.api)
            implementation(libs.log4j.core)
            implementation(libs.log4j.slf4j2)
        }
    }
}

// https://github.com/google/ksp/issues/963#issuecomment-2919262595
dependencies {
    kspCommonMainMetadata(project(":internal-processors:onebot-include-component-message-elements-processor"))
}

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

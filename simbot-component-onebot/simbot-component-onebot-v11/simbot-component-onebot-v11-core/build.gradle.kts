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
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    id("love.forte.plugin.suspend-transform")
    alias(libs.plugins.ksp)
}

configJavaCompileWithModule("simbot.component.onebot11v.core")
apply(plugin = "simbot-maven-publish")

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        optIn.addAll(
            "love.forte.simbot.annotations.InternalSimbotAPI",
            "love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI",
            "love.forte.simbot.component.onebot.common.annotations.ExperimentalOneBotAPI",
        )
    }

    configKotlinJvm()

    js(IR) {
        configJs()
    }

    applyTier123(supportKtorClient = true)

    sourceSets {
        commonMain {
            kotlin.srcDir(project.layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))

            dependencies {
                // JVM compileOnly
                implementation(project(":simbot-api"))
                implementation(libs.jetbrains.annotations)

                api(project(":simbot-commons:simbot-common-annotations"))
                api(project(":simbot-commons:simbot-common-atomic"))

                api(project(":simbot-component-onebot:simbot-component-onebot-common"))
                api(project(":simbot-component-onebot:simbot-component-onebot-v11:simbot-component-onebot-v11-common"))
                api(project(":simbot-component-onebot:simbot-component-onebot-v11:simbot-component-onebot-v11-message"))
                api(project(":simbot-component-onebot:simbot-component-onebot-v11:simbot-component-onebot-v11-event"))

                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.json)
                api(libs.ktor.client.core)
                api(libs.ktor.client.ws)
                api(project(":simbot-logger"))
                api(project(":simbot-commons:simbot-common-suspend-runner"))
                api(libs.ktor.client.contentNegotiation)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":simbot-cores:simbot-core"))
                implementation(kotlin("test"))
                implementation(libs.ktor.client.mock)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        jvmMain {
            dependencies {
                compileOnly(project(":simbot-api"))
                compileOnly(libs.ktor.client.contentNegotiation)
                compileOnly(libs.jetbrains.annotations)
            }
        }

        jvmTest.dependencies {
            implementation(libs.log4j.api)
            implementation(libs.mockk)
            implementation(libs.log4j.core)
            implementation(libs.log4j.slf4j2)
            implementation(libs.kotlinx.coroutines.reactor)
            implementation(libs.ktor.client.java)
            implementation(libs.ktor.server.netty)
            implementation(libs.ktor.server.ws)
        }

        appleTest.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        mingwTest.dependencies {
            implementation(libs.ktor.client.winhttp)
        }
        linuxTest.dependencies {
            implementation(libs.ktor.client.cio)
        }
    }
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

tasks.withType<DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
        suppressGeneratedFiles.set(false)
    }
}

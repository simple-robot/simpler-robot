/*
 *     Copyright (c) 2026. ForteScarlet.
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

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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


plugins {
    id("simbot.kook.kotlin-multiplatform-convention")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    alias(libs.plugins.ksp)
    `simbot-maven-publish`
}

configJavaCompileWithModule("simbot.component.kook.api")

kotlin {
    compilerOptions {
        optIn.addAll(
            "love.forte.simbot.kook.ExperimentalKookApi",
            "love.forte.simbot.kook.InternalKookApi"
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
            dependencies {
                api(libs.kotlinx.coroutines.core)

                api(project(":simbot-logger"))
                api(project(":simbot-commons:simbot-common-apidefinition"))
                api(project(":simbot-commons:simbot-common-suspend-runner"))
                api(project(":simbot-commons:simbot-common-core"))
                api(project(":simbot-commons:simbot-common-annotations"))

                api(libs.ktor.client.core)
                api(libs.ktor.client.contentNegotiation)
                api(libs.kotlinx.serialization.json)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.ktor.client.mock)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.ktor.client.cio)
                implementation(libs.log4j.api)
                implementation(libs.log4j.core)
                implementation(libs.log4j.slf4j2)
            }
        }

        jsMain.dependencies {
            api(libs.ktor.client.js)
        }

        mingwTest.dependencies {
            implementation(libs.ktor.client.winhttp)
        }
    }
}

dependencies {
    add("kspJvm", project(":internal-processors:kook-api-reader"))
}

ksp {
    arg("kook.api.reader.enable", (!isCi).toString())
    arg("kook.api.finder.api.output", rootDir.resolve("generated-docs/kook-api-list.md").absolutePath)
    arg("kook.api.finder.event.output", rootDir.resolve("generated-docs/kook-event-list.md").absolutePath)
}

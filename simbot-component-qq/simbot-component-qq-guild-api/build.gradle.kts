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

import com.google.devtools.ksp.gradle.KspAATask
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("simbot.kotlin-multiplatform")
    id("simbot.qq.kotlin-multiplatform-convention")
    kotlin("plugin.serialization")
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp)
    id("simbot-maven-publish")
}

configJavaCompileWithModule("simbot.component.qqguild.api")

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes",
            "-Xconsistent-data-class-copy-visibility"
        )
        optIn.add("love.forte.simbot.qguild.QGInternalApi")
        optIn.add("love.forte.simbot.qguild.ApiModelConstructor")
        optIn.add("kotlin.ExperimentalVersionOverloading")
    }

    configKotlinJvm()

    js {
        configJs()
    }

    applyTier123(supportKtorClient = true)

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        configWasmJs()
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(project.layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))

            dependencies {
                api(libs.kotlinx.coroutines.core)

                api(project(":simbot-logger"))
                api(project(":simbot-commons:simbot-common-apidefinition"))
                api(project(":simbot-commons:simbot-common-suspend-runner"))
                api(project(":simbot-commons:simbot-common-core"))
                implementation(project(":simbot-commons:simbot-common-annotations"))

                api(libs.ktor.client.core)
                api(libs.ktor.client.contentNegotiation)
                api(libs.kotlinx.serialization.core)
                api(libs.kotlinx.serialization.json)
            }
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            // implementation(libs.kotlinx.coroutines.debug)
            implementation(libs.kotlinx.coroutines.test)
            // https://ktor.io/docs/http-client-testing.html
            implementation(libs.ktor.client.mock)
        }

        jvmTest.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.log4j.api)
            implementation(libs.log4j.core)
            implementation(libs.log4j.slf4j2)
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
    // add("kspJvm", project(":internal-processors:qq-api-reader"))
    kspCommonMainMetadata(project(":internal-processors:qq-intents-processor"))
    kspCommonMainMetadata(project(":internal-processors:qq-dispatch-serializer-processor"))
}

ksp {
    arg("qg.api.reader.enable", (!isCi).toString())
    arg("qg.api.finder.api.output", rootDir.resolve("generated-docs/qq-api-list.md").absolutePath)
    arg("qg.api.finder.event.output", rootDir.resolve("generated-docs/qq-event-list.md").absolutePath)
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

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

import com.google.devtools.ksp.gradle.KspAATask
import love.forte.gradle.common.kotlin.multiplatform.applyTier1
import love.forte.gradle.common.kotlin.multiplatform.applyTier2
import love.forte.gradle.common.kotlin.multiplatform.applyTier3
import love.forte.plugin.suspendtrans.gradle.SuspendTransPluginConstants
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import java.time.Instant

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("love.forte.plugin.suspend-transform")
    alias(libs.plugins.ksp)
    id("org.jetbrains.dokka")
    id("com.github.gmazzo.buildconfig")
    `simbot-maven-publish`
}

configJavaCompileWithModule("simbot.api")
// apply(plugin = "simbot-maven-publish")

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    configKotlinJvm(JVMConstants.KT_JVM_TARGET_VALUE)

    js(IR) {
        configJs()
    }

    applyTier1()
    applyTier2()
    applyTier3()

    // wasm?
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        configWasmJs()
    }

//    wasmWasi()

    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes"
        )
        optIn.addAll(
            "love.forte.simbot.resource.ResourceImplementation",
            "love.forte.simbot.message.OfflineImageImplementation",
        )
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(project.layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))

            dependencies {
                api(project(":simbot-commons:simbot-common-annotations"))
                implementation(libs.jetbrains.annotations)
                implementation(project(":simbot-logger"))

                api(project(":simbot-commons:simbot-common-streamable"))
                api(project(":simbot-commons:simbot-common-suspend-runner"))
                api(project(":simbot-commons:simbot-common-core"))
                api(project(":simbot-commons:simbot-common-collection"))
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.io.core)
                implementation(libs.kotlinx.serialization.json)
                // suspend reversal annotations

            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.test)
                implementation(kotlin("test"))
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.properties)

                implementation(
                    SuspendTransPluginConstants.ANNOTATION_GROUP +
                        ":" + SuspendTransPluginConstants.ANNOTATION_NAME +
                        ":" + SuspendTransPluginConstants.ANNOTATION_VERSION
                )
            }
        }



        jvmMain {
            dependencies {
                compileOnly(libs.kotlinx.coroutines.reactive)
                compileOnly(libs.kotlinx.coroutines.reactor)
                compileOnly(libs.kotlinx.coroutines.rx2)
                compileOnly(libs.kotlinx.coroutines.rx3)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.mockk)
                implementation(project(":simbot-logger-slf4j2-impl"))
                implementation(libs.kotlinx.coroutines.reactive)
                implementation(libs.kotlinx.coroutines.reactor)
                implementation(libs.kotlinx.coroutines.rx2)
                implementation(libs.kotlinx.coroutines.rx3)
                implementation(libs.ktor.client.core)

                implementation(kotlin("test-junit5"))
                implementation(kotlin("reflect"))
                implementation(libs.ktor.client.cio)
            }
        }

        jsTest.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.ktor.client.core)
        }
    }
}

dependencies {
    // add("kspJvm", libs.suspend.reversal.processor)
    "kspJvm"(project(":internal-processors:interface-uml-processor"))
    kspCommonMainMetadata(project(":simbot-processors:simbot-processor-message-element-polymorphic-include"))
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

ksp {
    // arg("simbot.internal.processor.uml.enable", (!isCi).toString())
    arg("simbot.internal.processor.uml.enable", "false")
    arg("simbot.internal.processor.uml.target", "love.forte.simbot.event.Event")
    // arg("simbot.internal.processor.uml.target", "love.forte.simbot.definition.Actor")
    arg("simbot.internal.processor.uml.output", rootDir.resolve("generated-docs/event-uml.md").absolutePath)
    // arg("simbot.internal.processor.uml.output", rootDir.resolve("generated-docs/actor-uml.md").absolutePath)

    // simbot-processor-message-element-polymorphic-include
    arg("simbot.processor.message-element-polymorphic-include.localOnly", "true")
    arg("simbot.processor.message-element-polymorphic-include.outputPackage", "love.forte.simbot.message")
    arg("simbot.processor.message-element-polymorphic-include.visibility", "internal")
}

// BuildConfig for the current version
// love.forte.simbot.annotations.InternalSimbotAPI
buildConfig {
    useKotlinOutput {
        topLevelConstants = false
        internalVisibility = false
    }

    className.set("SimbotBuiltin")
    packageName.set("love.forte.simbot")
    buildConfigField<String>("VERSION", P.Simbot.version)
    buildConfigField<String>("BUILD_KOTLIN_VERSION", libs.versions.kotlin)
    buildConfigField<Boolean>("IS_SNAPSHOT", isSnapshot())
    buildConfigField<String>("BUILD_AT", Instant.now().toString())
    documentation.set("Auto-generated simbot built-in constants.")
}

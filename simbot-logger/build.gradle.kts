/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

import logger.loggerFrameworkAttribute
import logger.loggerFrameworkAttributeSlf4j

plugins {
    `simbot-simple-project-setup`
    `simbot-multiplatform-maven-publish`
    id("simbot.dokka-module-configuration")
}


kotlin {
    explicitApi()

    fun configJvm(name: String, attrValue: String, withJava: Boolean = false) {
        jvm(name) {
            attributes.attribute(loggerFrameworkAttribute, attrValue)
            compilations.all {
                kotlinOptions {
                    jvmTarget = "1.8"
                    javaParameters = true
                    freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
                }
            }
            if (withJava) {
                withJava()
            }
            testRuns["test"].executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    configJvm("jvm", loggerFrameworkAttributeSlf4j, withJava = true)
//    configJvm("jul", loggerFrameworkAttributeJUL)

    js(IR) {
        browser()
        nodejs()
    }

    // tier1
    linuxX64()
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()

    // tier2
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()

    // tier3
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    mingwX64()
    watchosDeviceArm64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        named("jvmMain") {
            dependencies {
                api(libs.slf4j.api)
            }
        }
    }
}


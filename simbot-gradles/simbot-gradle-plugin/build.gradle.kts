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

import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    `java-library`
    kotlin("jvm")
    id("org.jetbrains.dokka")
    `java-gradle-plugin`
}

// configJavaCompileWithModule("simbot.gradle.suspendtransforms")
// apply(plugin = "simbot-maven-publish")

kotlin {
    explicitApi()
    configJavaToolchain(JVMConstants.KT_JVM_TARGET_VALUE)
    // Gradle BuildSrc 友好
    coreLibrariesVersion = "1.9.0"
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_1_9
        languageVersion = KotlinVersion.KOTLIN_1_9
    }
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(kotlin("gradle-plugin"))
    compileOnly(kotlin("gradle-plugin-api"))
    // compileOnly(libs.suspend.transform.gradle)
}

gradlePlugin {
    plugins {
        create("SimbotBasicPlugin") {
            id = "love.forte.simbot"
            implementationClass = "love.forte.simbot.gradle.plugin.basic.SimbotBasicPlugin"
            description = "Simple Robot basic Gradle plugin"
            displayName = "Simple Robot basic Gradle plugin"
        }

        create("SimbotDevPlugin") {
            id = "love.forte.simbot.dev"
            implementationClass = "love.forte.simbot.gradle.plugin.dev.SimbotDevPlugin"
            description = "Simple Robot Gradle plugin for developing, e.g. develop a component library."
            displayName = "Simple Robot dev Gradle plugin"
        }
    }
}

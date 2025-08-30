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

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

configJavaCompileWithModule(jvmVersion = "11")
apply(plugin = "simbot-maven-publish")

kotlin {
    explicitApi()
    configKotlinJvm(11)
}

repositories {
    mavenLocal {
        content {
            includeGroup("love.forte.codegentle")
        }
        metadataSources {
            mavenPom()
            artifact()
        }
        isAllowInsecureProtocol = true
        metadataSources.artifact()
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    implementation(libs.ksp)
    // implementation(libs.kotlinPoet.ksp)

    implementation("love.forte.codegentle:codegentle-kotlin:0.0.1-SNAPSHOT")
    implementation("love.forte.codegentle:codegentle-kotlin-ksp:0.0.1-SNAPSHOT")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

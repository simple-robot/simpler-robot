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
    `kotlin-dsl`
    idea
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
}

val kotlinVersion: String = libs.versions.kotlin.get()

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation(kotlin("power-assert", kotlinVersion))
    // compileOnly(kotlin("compiler", kotlinVersion))
    // compileOnly(kotlin("compiler-embeddable", kotlinVersion))
    implementation(libs.dokka.plugin)

    // see https://github.com/gradle-nexus/publish-plugin
    implementation(libs.gradleNexusPublishPlugin)

    // see https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html#configure-the-project
    // see https://github.com/vanniktech/gradle-maven-publish-plugin
    // see https://plugins.gradle.org/plugin/com.vanniktech.maven.publish
    implementation(libs.maven.publish)

    // suspend transform
    implementation(libs.suspend.transform.gradle)

    // gradle common
    implementation(libs.bundles.gradle.common)
}

idea {
    module {
        isDownloadSources = true
    }
}


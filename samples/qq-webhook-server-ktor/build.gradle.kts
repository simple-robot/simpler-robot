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

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("simbot.kotlin-jvm")
    alias(libs.plugins.ktor)
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
    jvmToolchain(JVMConstants.KT_JVM_TARGET_VALUE)
    compilerOptions {
        javaParameters = true
        jvmTarget.set(JvmTarget.fromTarget(JVMConstants.KT_JVM_TARGET_VALUE.toString()))
    }
}

configJavaCompileWithModule()

application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(project(":simbot-component-qq:simbot-component-qq-guild-core"))
    implementation(project(":simbot-cores:simbot-core"))

    val ktorVersion = "2.3.12"
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")

    val logbackVersion = "1.5.18"
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation(kotlin("test-junit5"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

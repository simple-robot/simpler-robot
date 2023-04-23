/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    id("simbot.simple-module-conventions")
    `simbot-jvm-maven-publish`
    kotlin("plugin.serialization")
    kotlin("kapt")
    id("simbot.suspend-transform-configure")
    id("com.github.gmazzo.buildconfig") version "3.1.0"
}

dependencies {
    api(kotlin("reflect"))
    api(project(":simbot-logger"))
    api(project(":simbot-annotations"))
    api(project(":simbot-util-suspend-transformer"))

    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.jdk8)
    api(libs.kotlinx.serialization.core)

    api(libs.slf4j.api)
    compileOnly(libs.jetbrains.annotations)

    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.properties)
    compileOnly(libs.charleskorn.kaml)
    compileOnly(libs.kotlinx.serialization.protobuf)
    compileOnly(libs.kotlinx.coroutines.reactive)
    compileOnly(libs.kotlinx.coroutines.reactor)
    compileOnly(libs.kotlinx.coroutines.rx2)
    compileOnly(libs.kotlinx.coroutines.rx3)

    // logger
    testImplementation(project(":simbot-logger-slf4j-impl"))

    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.serialization.properties)
    testImplementation(libs.charleskorn.kaml)
    testImplementation(libs.kotlinx.serialization.protobuf)
    testImplementation(libs.kotlinx.serialization.cbor)
    testImplementation(libs.kotlinx.serialization.hocon)
    testImplementation(libs.openjdk.jmh.core)
    testImplementation(libs.openjdk.jmh.generator.annprocess)
    kaptTest(libs.openjdk.jmh.generator.annprocess)
    testAnnotationProcessor(libs.openjdk.jmh.generator.annprocess)
}


buildConfig {
    useKotlinOutput()
    packageName.set("love.forte.simbot")
    className.set("SimbotInformation")
    
    buildConfigField("String", "VERSION", "\"${project.version}\" // auto-generated")
    buildConfigField("boolean", "IS_SNAPSHOT", "${isSnapshot()} // auto-generated")
}

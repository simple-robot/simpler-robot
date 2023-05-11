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
}


dependencies {
    // simbot-core 使用 logger
    api(project(":simbot-logger"))
    api(project(":simbot-api"))
    api(libs.slf4j.api)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.jdk8)
    api(libs.kotlinx.serialization.core)

    compileOnly(libs.kotlinx.coroutines.reactive)
    compileOnly(libs.kotlinx.coroutines.reactor)
    compileOnly(libs.kotlinx.coroutines.rx2)
    compileOnly(libs.kotlinx.coroutines.rx3)
    compileOnly(libs.jetbrains.annotations)

    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.serialization.properties)
    testImplementation(libs.kotlinx.serialization.protobuf)
    testImplementation(libs.kotlinx.coroutines.reactor)
    testImplementation(libs.kotlinx.lincheck)
    testImplementation(libs.slf4j.nop)
}

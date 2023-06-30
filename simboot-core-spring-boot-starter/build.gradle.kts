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
    id("simbot.boot-module-conventions")
    `simbot-jvm-maven-publish`
    kotlin("plugin.serialization")
    kotlin("kapt")
}

dependencies {
    api(project(":simboot-core"))
    
    api(libs.javax.inject)
    api(libs.spring.boot.logging)

    compileOnly(libs.spring.boot.autoconfigure)
    compileOnly(libs.spring.boot.configuration.processor)
    annotationProcessor(libs.spring.boot.configuration.processor)
    kapt(libs.spring.boot.configuration.processor)

    compileOnly(libs.javax.annotation.api)
    compileOnly(project(":simbot-util-annotation-tool"))
    
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.serialization.properties)
    testImplementation(libs.kotlinx.serialization.protobuf)
    testImplementation(libs.spring.boot.aop)
    testImplementation(libs.spring.boot.autoconfigure)
    testImplementation(libs.spring.boot.configuration.processor)
    // testImplementation(libs.spring.boot.webflux)

    // @Suppress("VulnerableLibrariesLocal")
    testImplementation("love.forte.simbot.component:simbot-component-mirai-core:3.1.0.0-M8")
    // testImplementation(V.Kotlinx.Coroutines.Reactor.notation)
}


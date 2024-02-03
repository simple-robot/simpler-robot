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
}

dependencies {
    api(project(":simboot-api"))
    api(libs.javax.inject)
    
    compileOnly(libs.javax.annotation.api)
    compileOnly(project(":simbot-util-annotation-tool"))
    compileOnly(libs.spring.boot.autoconfigure)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.serialization.properties)
    testImplementation(libs.kotlinx.serialization.protobuf)
    testImplementation(libs.javax.annotation.api)
    testImplementation(project(":simbot-util-annotation-tool"))
    testImplementation(libs.spring.boot.autoconfigure)
    
}
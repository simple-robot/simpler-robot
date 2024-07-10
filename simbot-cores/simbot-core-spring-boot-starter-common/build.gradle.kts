/*
 *     Copyright (c) 2024. ForteScarlet.
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
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("simbot.dokka-module-configuration")
}

configJavaCompileWithModule("simbot.spring.common")
apply(plugin = "simbot-jvm-maven-publish")

kotlin {
    explicitApi()
    configKotlinJvm(JVMConstants.KT_JVM_TARGET_VALUE)
}

dependencies {
    compileOnly(project(":simbot-commons:simbot-common-annotations"))
    api(project(":simbot-quantcat:simbot-quantcat-common"))
    api(project(":simbot-cores:simbot-core"))

    compileOnly(libs.javax.annotation.api)

    testImplementation(libs.kotlinx.serialization.json)
}


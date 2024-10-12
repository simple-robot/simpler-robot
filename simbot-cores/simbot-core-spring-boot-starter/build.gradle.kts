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
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("kapt")
    id("simbot.dokka-module-configuration")
}

configJavaCompileWithModule("simbot.spring.starter", jvmVersion = "17")
apply(plugin = "simbot-jvm-maven-publish")

kotlin {
    explicitApi()
    configKotlinJvm(jdkVersion = 17)
}


dependencies {
    implementation(platform(libs.spring.boot.v3.dependencies))
    implementation(project(":simbot-logger"))

    implementation(project(":simbot-commons:simbot-common-annotations"))
    api(project(":simbot-quantcat:simbot-quantcat-common"))
    api(project(":simbot-cores:simbot-core"))
    api(project(":simbot-cores:simbot-core-spring-boot-starter-common"))
    api(kotlin("reflect"))
    api(libs.kotlinx.serialization.json)

    compileOnly(libs.spring.boot.v3.logging)
    compileOnly(libs.spring.boot.v3.autoconfigure)
    compileOnly(libs.spring.boot.v3.configuration.processor)
    annotationProcessor(libs.spring.boot.v3.configuration.processor)
    kapt(libs.spring.boot.v3.configuration.processor)

    compileOnly(libs.javax.annotation.api)

    testImplementation(kotlin("test"))
    testImplementation(project(":simbot-commons:simbot-common-annotations"))
    testImplementation(project(":simbot-test"))
    testImplementation(libs.spring.boot.v3.test)
    testImplementation(libs.spring.boot.v3.aop)
    testImplementation(libs.spring.boot.v3.autoconfigure)
    testImplementation(libs.spring.boot.v3.configuration.processor)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

/*
 * Copyright (c) 2023 ForteScarlet.
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
    id("com.github.gmazzo.buildconfig")
}


tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        javaParameters = true
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
    }
}

dependencies {
    api(project(":simbot-logger"))
    api("com.lmax:disruptor:3.4.4")
}

buildConfig {
    useKotlinOutput {
        internalVisibility = true
    }
    packageName.set("love.forte.simbot.logger.slf4j")
    className.set("SLF4JInformation")
    var slf4jVersion = libs.versions.slf4j.get()
    val last = slf4jVersion.lastIndexOf('.')
    if (last >= 0) {
        slf4jVersion = slf4jVersion.replaceRange(last, slf4jVersion.length, ".99")
    }

    buildConfigField("String", "VERSION", "\"$slf4jVersion\" // auto-generated")

}

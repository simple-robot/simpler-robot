/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import love.forte.gradle.common.core.project.setup

plugins {
    `java-library`
    kotlin("jvm")
    // id("com.github.gmazzo.buildconfig")
    id("simbot.dokka-module-configuration")
}

setup(P.SimbotGradle)

configJavaCompileWithModule("simbot.gradle.suspendtransforms")
apply(plugin = "simbot-jvm-maven-publish")

kotlin {
    explicitApi()
    configJavaToolchain(JVMConstants.KT_JVM_TARGET_VALUE)
}

val suspendTransformVersion = "0.6.0-beta3"

dependencies {
    api("love.forte.plugin.suspend-transform:suspend-transform-plugin-gradle:$suspendTransformVersion")
}

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

/*
 *     Copyright (c) 2023-2024. ForteScarlet.
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

plugins {
    `java-library`
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
}

setup(P.SimbotLogger)

configJavaCompileWithModule("simbot.logger.slf4j2impl")
apply(plugin = "simbot.dokka-module-configuration")
apply(plugin = "simbot-jvm-maven-publish")

kotlin {
    explicitApi()
    configJavaToolchain(JVMConstants.KT_JVM_TARGET_VALUE)
}

dependencies {
    api(project(":simbot-logger"))
    api(libs.lmax.disruptor)
}

buildConfig {
    useKotlinOutput {
        internalVisibility = true
    }
    packageName.set("love.forte.simbot.logger.slf4j2")
    className.set("SLF4JInformation")
    var slf4jVersion = libs.versions.slf4j.get()
    val last = slf4jVersion.lastIndexOf('.')
    if (last >= 0) {
        slf4jVersion = slf4jVersion.replaceRange(last, slf4jVersion.length, ".99")
    }

    buildConfigField("String", "VERSION", "\"$slf4jVersion\" // auto-generated")

}

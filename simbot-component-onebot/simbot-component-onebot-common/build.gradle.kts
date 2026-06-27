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

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("simbot.kotlin-multiplatform")
    id("simbot.onebot.kotlin-multiplatform-convention")
    alias(libs.plugins.dokka)
    id("simbot-maven-publish")
}

configJavaCompileWithModule("simbot.component.onebot.common")

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        optIn.addAll(
            "love.forte.simbot.annotations.InternalSimbotAPI",
            "love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI"
        )
    }

    configKotlinJvm()

    js {
        configJs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        configWasmJs()
    }

    applyTier123()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":simbot-api"))
            api(project(":simbot-commons:simbot-common-annotations"))
        }

        commonTest.dependencies {
            implementation(project(":simbot-cores:simbot-core"))
            implementation(kotlin("test"))
        }

        jvmTest.dependencies {
            implementation(libs.log4j.api)
            implementation(libs.log4j.core)
            implementation(libs.log4j.slf4j2)
        }
    }
}

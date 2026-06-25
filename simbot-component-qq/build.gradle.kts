/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

import love.forte.gradle.common.core.project.setup
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    kotlin("multiplatform") apply false
}

subprojects {
    group = P.GROUP_COMPONENT
    setup(P.SimbotComponent)

    @OptIn(ExperimentalAbiValidation::class)
    afterEvaluate {
        // Configures ABI validation for JVM or multiplatform projects
        when {
            plugins.hasPlugin("org.jetbrains.kotlin.jvm") -> {
                extensions.configure<KotlinJvmProjectExtension>("kotlin") {
                    extensions.configure<AbiValidationExtension>("abiValidation") {
                        configQQAbiValidation()
                    }
                }
            }

            plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> {
                extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
                    // TODO
                    // extensions.configure<AbiValidationMultiplatformExtension>("abiValidation") {
                    //     configQQAbiValidation()
                    // }
                }
            }
        }
    }
}

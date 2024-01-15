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
    idea
    id("simbot.dokka-multi-module")
    id("com.github.gmazzo.buildconfig") version "4.1.2" apply false
    id("io.gitlab.arturbosch.detekt")
    id("simbot.nexus-publish")
    id("simbot.changelog-generator")
}

setup(P.Simbot)

// https://github.com/detekt/detekt/blob/main/build.gradle.kts

repositories {
    mavenCentral()
    love.forte.gradle.common.core.repository.Repositories.Snapshot.Default.apply {
        configMaven {
            mavenContent {
                snapshotsOnly()
            }
        }
    }
    mavenLocal()
}

subprojects {
    repositories {
        mavenCentral()
        love.forte.gradle.common.core.repository.Repositories.Snapshot.Default.apply {
            configMaven {
                mavenContent {
                    snapshotsOnly()
                }
            }
        }
        mavenLocal()
    }

    afterEvaluate {
        if (plugins.hasPlugin("io.gitlab.arturbosch.detekt")) {
            return@afterEvaluate
        }

        fun Project.hasKtP(): Boolean {
            return plugins.findPlugin("org.jetbrains.kotlin.jvm") != null ||
                    plugins.findPlugin("org.jetbrains.kotlin.multiplatform") != null
        }

        if (hasKtP()) {
//            apply(plugin = "io.gitlab.arturbosch.detekt")
            applyDetekt()
        }
    }
}

fun Project.applyDetekt() {
//     apply(plugin = "io.gitlab.arturbosch.detekt")
//
//     detekt {
// //        buildUponDefaultConfig = true
//         config.from(rootProject.projectDir.resolve(".detekt/config/detekt.yml"))
//         baseline = rootProject.projectDir.resolve(".detekt/baseline/detekt-baseline.xml")
//         // "detekt-baseline.xml"
//         dependencies {
//             detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")
//         }
//     }
}

idea {
    module {
        isDownloadSources = true
    }
}



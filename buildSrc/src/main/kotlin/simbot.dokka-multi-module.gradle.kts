/*
 *     Copyright (c) 2023-2024. ForteScarlet.
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

import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import java.time.Year


/*
使用在根配置，配置dokka多模块
 */

plugins {
    id("org.jetbrains.dokka")
}

tasks.named<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>("dokkaHtmlMultiModule") {
    moduleName.set("Simple Robot")
    outputDirectory.set(rootProject.file("build/dokka/html"))

    if (isSimbotLocal()) {
        logger.info("Is 'SIMBOT_LOCAL', offline")
        offlineMode.set(true)
    }

    @Suppress("MaxLineLength")
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customAssets = listOf(
            rootProject.file(".simbot/dokka-assets/logo-icon.svg"),
            rootProject.file(".simbot/dokka-assets/logo-icon-light.svg"),
        )
        customStyleSheets = listOf(rootProject.file(".simbot/dokka-assets/css/kdoc-style.css"))
        if (!isSimbotLocal()) {
            templatesDir = rootProject.file(".simbot/dokka-templates")
        }
        footerMessage = "© 2021-${Year.now().value} <a href='https://github.com/simple-robot'>Simple Robot</a>. All rights reserved."
        separateInheritedMembers = true
        mergeImplicitExpectActualDeclarations = true
    }
}


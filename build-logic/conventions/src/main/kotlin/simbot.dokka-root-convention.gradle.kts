/*
 *     Copyright (c) 2026. ForteScarlet.
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

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters

private val dokkaPluginId = "org.jetbrains.dokka"

private val dokkaExcludedProjectPaths = setOf(
    ":simbot-test",
)

private fun Project.shouldIncludeInRootDokka(): Boolean {
    return path !in dokkaExcludedProjectPaths &&
        !path.startsWith(":internal-processors:") &&
        !path.startsWith(":samples:") &&
        !path.startsWith(":tests:")
}

private fun DokkaExtension.configureSimbotDokka(project: Project) {
    dokkaPublications.all {
        if (isSimbotLocal()) {
            project.logger.info("Is 'SIMBOT_LOCAL', offline")
            offlineMode = true
        } else if (isSimbotTest()) {
            project.logger.info("Is 'SIMBOT_TEST', offline")
            offlineMode = true
        }
    }

    configSourceSets(project)

    pluginsConfiguration.named(
        DokkaHtmlPluginParameters.DOKKA_HTML_PARAMETERS_NAME,
        DokkaHtmlPluginParameters::class.java
    ) {
        configHtmlCustoms(project)
    }
}

subprojects {
    plugins.withId(dokkaPluginId) {
        val p = this@subprojects
        if (p.shouldIncludeInRootDokka()) {
            p.extensions.configure<DokkaExtension> {
                configureSimbotDokka(p)
            }

            val applied = rootProject.dependencies.add(
                "dokka",
                rootProject.dependencies.project(mapOf("path" to p.path))
            )
            logger.lifecycle("Applied Dokka for subproject {}: {}", p, applied)
        }
    }
}

plugins.withId(dokkaPluginId) {
    extensions.configure<DokkaExtension> {
        moduleName = "Simple Robot"
        configureSimbotDokka(project)
    }
}

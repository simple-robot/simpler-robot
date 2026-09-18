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

plugins {
    id("simbot-maven-publish")
}

private val qqComponentRelocationArtifactIds = mapOf(
    "simbot-component-qq-api" to "simbot-component-qq-guild-api",
    "simbot-component-qq-stdlib" to "simbot-component-qq-guild-stdlib",
    "simbot-component-qq-core" to "simbot-component-qq-guild-core",
    "simbot-component-qq-internal-ed25519" to "simbot-component-qq-guild-internal-ed25519",
)

/*
 * Vanniktech excludes publications with the PluginMarkerMaven suffix from its KMP artifactId
 * rewriting. Reuse that convention so an old artifactId is not mistaken for a new platform id.
 */
private val relocationPublicationNameSuffix = "RelocationPluginMarkerMaven"

private val oldArtifactId = requireNotNull(qqComponentRelocationArtifactIds[project.name]) {
    "The QQ relocation publishing convention does not support project '$path'."
}

configureQqComponentRelocations(oldArtifactId)

/**
 * Publishes relocation-only POMs for the historical QQ component coordinates.
 *
 * Kotlin Multiplatform creates one publication for the root module and one for each target. The
 * target suffix is copied from the current publication so every historical platform coordinate
 * points to its corresponding new coordinate without publishing a duplicate artifact.
 */
private fun Project.configureQqComponentRelocations(oldArtifactId: String) {
    val publications = extensions.getByType<PublishingExtension>().publications
    val currentProjectName = name

    afterEvaluate {
        publications.withType<MavenPublication>().toList().forEach { sourcePublication ->
            if (sourcePublication.name.endsWith("PluginMarkerMaven")) {
                return@forEach
            }

            val newArtifactId = sourcePublication.artifactId
            val artifactSuffix = newArtifactId.removePrefix(currentProjectName)
            require(artifactSuffix.isEmpty() || artifactSuffix.startsWith("-")) {
                "Unexpected publication artifactId '$newArtifactId' for project '$path'."
            }

            val relocatedArtifactId = oldArtifactId + artifactSuffix
            publications.register<MavenPublication>(
                "${sourcePublication.name}$relocationPublicationNameSuffix",
            ) {
                groupId = project.group.toString()
                artifactId = relocatedArtifactId
                version = project.version.toString()
                pom {
                    distributionManagement {
                        relocation {
                            groupId = project.group.toString()
                            artifactId = newArtifactId
                            version = project.version.toString()
                            message =
                                "The artifact has moved to ${project.group}:$newArtifactId:${project.version}."
                        }
                    }
                }
                // The global KMP publishing configuration adds documentation artifacts to every
                // Maven publication. A relocation coordinate must publish its POM only.
                setArtifacts(emptyList<Any>())
            }
        }
    }
}

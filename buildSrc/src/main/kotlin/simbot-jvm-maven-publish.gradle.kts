/*
 *     Copyright (c) 2022-2025. ForteScarlet.
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

import love.forte.gradle.common.core.Gpg
import love.forte.gradle.common.publication.configure.configPublishMaven
import love.forte.gradle.common.publication.configure.publishingExtension
import love.forte.gradle.common.publication.configure.setupPom
import utils.checkPublishConfigurable

plugins {
    signing
    `maven-publish`
    id("org.jetbrains.dokka")
}

// if (!isCi || isLinux) {


checkPublishConfigurable {
    val isSnapshot = isSnapshot()
    val jarSources by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    val jarJavadoc by tasks.registering(Jar::class) {
        if (!(isSnapshot || isSimbotLocal())) {
            dependsOn(tasks.dokkaGeneratePublicationHtml)
            from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
        }
        archiveClassifier.set("javadoc")
    }

    publishing {
        repositories {
            mavenLocal()
            if (isSnapshot) {
                configPublishMaven(SnapshotRepository)
            } else {
                configPublishMaven(ReleaseRepository)
            }
        }

        publications {
            create<MavenPublication>("simbotDist") {
                from(components.getByName("java"))
                artifacts {
                    artifact(jarSources)
                    artifact(jarJavadoc)
                }

                setupPom(project.name, P.Simbot)
                pom {
                    issueManagement {
                        system.set("GitHub Issues")
                        url.set("https://github.com/simple-robot/simpler-robot/issues")
                    }
                }
                // showMaven()
            }
        }
    }

    signing {
        val gpg = Gpg.ofSystemPropOrNull() ?: return@signing
        val (keyId, secretKey, password) = gpg
        useInMemoryPgpKeys(keyId, secretKey, password)
        sign(publishingExtension.publications)
    }
}

fun MavenPublication.showMaven() {
    val pom = pom
    // // show project info
    logger.lifecycle(
        """
        |=======================================================
        |= jvm.maven.name:            {}
        |= jvm.maven.groupId:         {}
        |= jvm.maven.artifactId:      {}
        |= jvm.maven.version:         {}
        |= jvm.maven.pom.description: {}
        |= jvm.maven.pom.name:        {}
        |=======================================================
        """.trimIndent(),
        name,
        groupId,
        artifactId,
        version,
        pom.description.get(),
        pom.name.get(),
    )
}

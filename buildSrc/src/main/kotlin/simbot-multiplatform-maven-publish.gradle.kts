/*
 *     Copyright (c) 2022-2024. ForteScarlet.
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

/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (即 simple robot的v3版本，因此亦可称为 simple-robot v3 、simbot v3 等) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */


plugins {
    signing
    `maven-publish`
}

val p = project
val isSnapshot = isSnapshot()

val jarJavadoc by tasks.registering(Jar::class) {
    group = "documentation"
    archiveClassifier.set("javadoc")
    if (!(isSnapshot || isSimbotLocal())) {
        dependsOn(tasks.dokkaHtml)
        from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    }
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
        withType<MavenPublication> {
            artifacts {
                artifact(jarJavadoc)
            }

            setupPom(project.name, P.Simbot)
            pom {
                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/simple-robot/simpler-robot/issues")
                }
            }
            showMaven()
        }
    }
}

signing {
    val gpg = Gpg.ofSystemPropOrNull() ?: return@signing
    val (keyId, secretKey, password) = gpg
    useInMemoryPgpKeys(keyId, secretKey, password)
    sign(publishingExtension.publications)
}

// multiplatformConfigPublishing {
//     project = P.findProjectDetailByGroup(p.group.toString()) ?: error("Unknown project group: ${p.group}")
//     isSnapshot = project.version.toString().contains("SNAPSHOT", true)
//     releasesRepository = ReleaseRepository
//     snapshotRepository = SnapshotRepository
//     gpg = Gpg.ofSystemPropOrNull()
//
//     val jarJavadoc by tasks.registering(Jar::class) {
//         group = "documentation"
//         archiveClassifier.set("javadoc")
//         if (!(isSnapshot || isSnapshot() || isSimbotLocal())) {
//             dependsOn(tasks.dokkaHtml)
//             from(tasks.dokkaHtml.flatMap { it.outputDirectory })
//         }
//     }
//
//     artifact(jarJavadoc)
//
//     if (isSimbotLocal()) {
//         logger.info("Is 'SIMBOT_LOCAL', mainHost set as null")
//         mainHost = null
//     }
//
//     publicationsFromMainHost += listOf("wasm", "wasm32", "wasm_js")
//     mainHostSupportedTargets += listOf("wasm", "wasm32", "wasm_js")
// }

// TODO see https://github.com/gradle-nexus/publish-plugin/issues/208#issuecomment-1465029831
val signingTasks: TaskCollection<Sign> = tasks.withType<Sign>()
tasks.withType<PublishToMavenRepository>().configureEach {
    mustRunAfter(signingTasks)
}

fun MavenPublication.showMaven() {
    val pom = pom
    // // show project info
    logger.lifecycle(
        """
        |=======================================================
        |= multiplatform.maven.name:            {}
        |= multiplatform.maven.groupId:         {}
        |= multiplatform.maven.artifactId:      {}
        |= multiplatform.maven.version:         {}
        |= multiplatform.maven.pom.description: {}
        |= multiplatform.maven.pom.name:        {}
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

internal val TaskContainer.dokkaHtml: TaskProvider<org.jetbrains.dokka.gradle.DokkaTask>
    get() = named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaHtml")

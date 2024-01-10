/*
 *     Copyright (c) 2022-2024. ForteScarlet.
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

import love.forte.gradle.common.core.Gpg
import love.forte.gradle.common.publication.configure.jvmConfigPublishing

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
    id("signing")
    id("maven-publish")
}


//if (!isCi || (isLinux && checkPublishConfigurable().isPublishConfigurable)) {
val p = project

jvmConfigPublishing {
    val groupProject = P::class.sealedSubclasses.mapNotNull { it.objectInstance }.associateBy { obj -> obj.group }
    project = groupProject[p.group] ?: error("unknown project group: ${p.group}")
    
    publicationName = "simbotDist"
    
    val jarSources by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    
    val jarJavadoc by tasks.registering(Jar::class) {
        dependsOn(tasks.dokkaHtml)
        from(tasks.dokkaHtml.flatMap { it.outputDirectory })
        archiveClassifier.set("javadoc")
    }
    
    artifact(jarSources)
    artifact(jarJavadoc)
    
    isSnapshot = project.version.toString().contains("SNAPSHOT", true)
    releasesRepository = ReleaseRepository
    snapshotRepository = SnapshotRepository
    gpg = Gpg.ofSystemPropOrNull()

}

// TODO see https://github.com/gradle-nexus/publish-plugin/issues/208#issuecomment-1465029831
val signingTasks: TaskCollection<Sign> = tasks.withType<Sign>()
tasks.withType<PublishToMavenRepository>().configureEach {
    mustRunAfter(signingTasks)
}

show()
//}

fun show() {
    //// show project info
    logger.info(
        """
        |=======================================================
        |= project.group:       {}
        |= project.name:        {}
        |= project.version:     {}
        |= project.description: {}
        |=======================================================
    """.trimIndent(),
        group, name, version, description
    )
}


internal val TaskContainer.dokkaJavadoc: TaskProvider<org.jetbrains.dokka.gradle.DokkaTask>
    get() = named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaJavadoc")

internal val TaskContainer.dokkaHtml: TaskProvider<org.jetbrains.dokka.gradle.DokkaTask>
    get() = named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaHtml")

inline val Project.sourceSets: SourceSetContainer
    get() = extensions.getByName("sourceSets") as SourceSetContainer

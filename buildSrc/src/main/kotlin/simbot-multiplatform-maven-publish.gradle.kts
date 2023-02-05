/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
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
 */

import love.forte.gradle.common.core.Gpg
import love.forte.gradle.common.core.property.systemProp
import love.forte.gradle.common.publication.configure.MavenMultiplatformPublishingConfigExtensions
import love.forte.gradle.common.publication.configure.multiplatformConfigPublishing
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

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
    kotlin("multiplatform")
    signing
    `maven-publish`
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}

val p = project

multiplatformConfigPublishing {

    val groupProject = P::class.sealedSubclasses.mapNotNull { it.objectInstance }.associateBy { obj -> obj.group }

    project = groupProject[p.group] ?: error("unknown project group: ${p.group}")

    val jarJavadoc by tasks.registering(Jar::class) {
        group = "documentation"
        archiveClassifier.set("javadoc")
        from(tasks.findByName("dokkaHtml"))
    }

    artifact(jarJavadoc)
    isSnapshot = project.version.toString().contains("SNAPSHOT", true)
    releasesRepository = ReleaseRepository
    snapshotRepository = SnapshotRepository
    gpg = Gpg.ofSystemPropOrNull()

    if (systemProp("SIMBOT_LOCAL").toBoolean()) {
        mainHost = null
    }
//    else {
//
//        mainHostSupportedTargets = mainHost?.supports(hostManager) ?: emptySet()
//    }

}

fun KonanTarget.supports(hostManager: HostManager): Set<String> {
    return hostManager.enabledByHost[this]?.mapTo(mutableSetOf()) { target -> target.name } ?: emptySet()
}

show()

fun show() {
    //// show project info
    logger.info(
        """
        |=======================================================
        |= project.group:       {}
        |= project.name:        {}
        |= project.version:     {}
        |= project.description: {}
        |= os.name:             {}
        |=======================================================
    """.trimIndent(),
        group, name, version, description, systemProp("os.name")
    )
}


inline val Project.sourceSets: SourceSetContainer
    get() = extensions.getByName("sourceSets") as SourceSetContainer

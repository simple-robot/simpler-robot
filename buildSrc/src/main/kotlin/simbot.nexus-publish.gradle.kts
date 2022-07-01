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

import java.time.Duration

/*
 *  Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-kaiheila 的一部分。
 *
 *  simbot-component-kaiheila 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-kaiheila 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

val isSnapshotOnly = systemProp("snapshotOnly", "simbot.snapshotOnly") != null
val isReleaseOnly = systemProp("releaseOnly", "simbot.releaseOnly") != null

val isPublishConfigurable = when {
    isSnapshotOnly -> P.Simbot.isSnapshot
    isReleaseOnly -> !P.Simbot.isSnapshot
    else -> true
}

println("isSnapshotOnly: $isSnapshotOnly")
println("isReleaseOnly: $isReleaseOnly")
println("isPublishConfigurable: $isPublishConfigurable")


if (isPublishConfigurable) {
    val sonatypeUsername: String? = systemProp("OSSRH_USER")
    val sonatypePassword: String? = systemProp("OSSRH_PASSWORD")
    
    if (sonatypeUsername == null || sonatypePassword == null) {
        println("[WARN] - sonatype.username or sonatype.password is null, cannot config nexus publishing.")
    }
    
    nexusPublishing {
        packageGroup.set(project.group.toString())

        useStaging.set(
            project.provider { !project.version.toString().endsWith("SNAPSHOT", ignoreCase = true) }
        )

        transitionCheckOptions {
            maxRetries.set(60)
            delayBetween.set(Duration.ofSeconds(30))
        }

        repositories {
            sonatype {
                nexusUrl.set(uri(Sonatype.Central.URL))
                snapshotRepositoryUrl.set(uri(Sonatype.Snapshot.URL))
                username.set(sonatypeUsername)
                password.set(sonatypePassword)
            }
        }
    }
    
    
    println("[nexus-publishing-configure] - [$name] configured.")
}





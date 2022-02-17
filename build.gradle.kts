/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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
 *
 */


plugins {
    kotlin("jvm") version "1.6.10" apply false
    kotlin("plugin.serialization") version "1.6.10" apply false
    id("org.jetbrains.dokka") // version "1.6.10" apply false
    `maven-publish`
    signing
    // see https://github.com/gradle-nexus/publish-plugin
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    idea
}



group = P.Simbot.GROUP
version = P.Simbot.VERSION

repositories {
    mavenLocal()
    mavenCentral()
}

val secretKeyRingFileKey = "signing.secretKeyRingFile"

subprojects {
    println("ROOT SUB: $this")
    group = P.Simbot.GROUP
    version = P.Simbot.VERSION
    apply(plugin = "maven-publish")
    apply(plugin = "java")
    apply(plugin = "signing")

    repositories {
        mavenLocal()
        mavenCentral()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
        }
    }


    afterEvaluate {
        if (name in publishNeed) {

            configurePublishing(name)
            println("[publishing-configure] - [$name] configured.")
            // set gpg file path to root
            // val secretKeyRingFile = local().getProperty(secretKeyRingFileKey) ?: throw kotlin.NullPointerException(secretKeyRingFileKey)
            val secretRingFile = File(project.rootDir, "ForteScarlet.gpg")
            extra[secretKeyRingFileKey] = secretRingFile
            setProperty(secretKeyRingFileKey, secretRingFile)

            signing {
                // val key = local().getProperty("signing.keyId")
                // val password = local().getProperty("signing.password")
                // this.useInMemoryPgpKeys(key, password)
                sign(publishing.publications)
            }

        }
        // else {
        //     // only local
        //     configurePublishingLocal(name)
        //     println("[publishing-local-configure] - [$name] configured.")
        // }
    }


}



// /**
//  * config dokka output.
//  */
// fun Project.configDokka() {
//     tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>() {
//         outputDirectory.set(rootProject.file("dokkaOutput/${project.name}"))
//         println("$this Dokka output dir: ${outputDirectory.get()}")
//     }
// }

fun org.jetbrains.dokka.gradle.AbstractDokkaTask.configOutput(format: String) {
    outputDirectory.set(rootProject.file("dokka/$format/"))
}

tasks.dokkaHtmlMultiModule.configure {
    configOutput("html")
}
tasks.dokkaGfmMultiModule.configure {
    configOutput("gfm")
}


// nexus staging


val sonatypeUsername: String? = extra.getIfHas("sonatype.username")?.toString()
val sonatypePassword: String? = extra.getIfHas("sonatype.password")?.toString()

println("sonatypeUsername: $sonatypeUsername")

if (sonatypeUsername != null && sonatypePassword != null) {
    nexusPublishing {
        packageGroup.set(P.Simbot.GROUP)

        useStaging.set(
            project.provider { !project.version.toString().endsWith("SNAPSHOT", ignoreCase = true) }
        )

        transitionCheckOptions {
            maxRetries.set(20)
            delayBetween.set(java.time.Duration.ofSeconds(5))
        }
        repositories {
            sonatype {
                snapshotRepositoryUrl.set(uri(Sonatype.`snapshot-oss`.URL))
                username.set(sonatypeUsername)
                password.set(sonatypePassword)
            }
        }
    }
}

// idea
idea {
    module {
        isDownloadSources = true
    }
}
/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */


plugins {
    kotlin("jvm") version "1.6.0" apply false
    kotlin("multiplatform") version "1.6.0" apply false
    kotlin("plugin.serialization") version "1.6.0" apply false
    id("org.jetbrains.dokka") version "1.5.30" apply false
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

    plugins.findPlugin("org.jetbrains.dokka")?.let {
        configDokka()
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



/**
 * config dokka output.
 */
fun Project.configDokka() {
    tasks.named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaHtml") {
        val root = rootProject.rootDir
        outputDirectory.set(File(root, "dokkaOutput/${project.name}"))
    }
}


// nexus staging


val credentialsUsername: String? = extra.get("credentials.username")?.toString()
val credentialsPassword: String? = extra.get("credentials.password")?.toString()

println("credentialsUsername: $credentialsUsername")

if (credentialsUsername != null && credentialsPassword != null) {
    nexusPublishing {
        packageGroup.set(P.Simbot.GROUP)

        repositories {
            sonatype {
                username.set(credentialsUsername)
                password.set(credentialsPassword)
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
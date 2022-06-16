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
 */


plugins {
    kotlin("jvm") version V.Kotlin.VERSION apply false
    kotlin("plugin.serialization") version V.Kotlin.VERSION apply false
    id("org.jetbrains.dokka")
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

val isSnapshotOnly = System.getProperty("snapshotOnly") != null
val isReleaseOnly = System.getProperty("releaseOnly") != null

val isPublishConfigurable = when {
    isSnapshotOnly -> P.Simbot.isSnapshot
    isReleaseOnly -> !P.Simbot.isSnapshot
    else -> true
}

println("isSnapshotOnly: $isSnapshotOnly")
println("isReleaseOnly: $isReleaseOnly")
println("isPublishConfigurable: $isPublishConfigurable")


val secretKeyRingFileKey = "signing.secretKeyRingFile"



subprojects {
    group = P.Simbot.GROUP
    version = P.Simbot.VERSION
    apply(plugin = "java")

    repositories {
        mavenLocal()
        mavenCentral()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
            freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")

        }
    }

    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
        dokkaSourceSets {
            configureEach {
                skipEmptyPackages.set(true)
                jdkVersion.set(8)
                reportUndocumented.set(true)
                perPackageOption {
                    matchingRegex.set(""".*\.internal.*""") // will match all .internal packages and sub-packages
                    suppress.set(true)
                }
            }
        }
    }

    if (isPublishConfigurable && name in publishNeed) {
        apply(plugin = "maven-publish")
        apply(plugin = "signing")
        afterEvaluate {
            configurePublishing(name)
            println("[publishing-configure] - [$name] configured.")

            signing {
                val secretRingFile = rootProject.file("ForteScarlet.gpg")
                extra[secretKeyRingFileKey] = secretRingFile
                setProperty(secretKeyRingFileKey, secretRingFile)

                sign(publishing.publications)
            }
        }
    }


}



fun org.jetbrains.dokka.gradle.AbstractDokkaTask.configOutput(format: String) {
    moduleName.set("simple-robot")
    outputDirectory.set(rootProject.file("dokka/$format/v$version"))
}

tasks.dokkaHtmlMultiModule.configure {
    configOutput("html")
}
tasks.dokkaGfmMultiModule.configure {
    configOutput("gfm")
}

tasks.register("dokkaHtmlMultiModuleAndPost") {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    dependsOn("dokkaHtmlMultiModule")
    doLast {
        val outDir = rootProject.file("dokka/html")
        val indexFile = File(outDir, "index.html")
        indexFile.createNewFile()
        indexFile.writeText(
            """
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta http-equiv="refresh" content="0;URL='v$version'" />
            </head>
            <body>
            </body>
            </html>
        """.trimIndent()
        )

        // TODO readme
    }
}

// nexus staging

if (isPublishConfigurable) {

    val sonatypeUsername: String? =
        extra.getIfHas("sonatype.username")?.toString() ?: System.getProperty("sonatype.username")
        ?: System.getenv("SONATYPE_USERNAME")

    val sonatypePassword: String? =
        extra.getIfHas("sonatype.password")?.toString() ?: System.getProperty("sonatype.password")
        ?: System.getenv("SONATYPE_PASSWORD")

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
    } else {
        println("[WARN] - sonatype.username or sonatype.password is null, cannot config nexus publishing.")
    }
}


tasks.create("createChangelog") {
    group = "build"
    doFirst {
        val realVersion = rootProject.version
        val version = "v$realVersion"
        println("Generate change log for $version ...")
        // configurations.runtimeClasspath
        val changelogDir = rootProject.file(".changelog").also {
            it.mkdirs()
        }
        val file = File(changelogDir, "$version.md")
        if (!file.exists()) {
            file.createNewFile()
            val autoGenerateText = """
                

                ## 组件更新
                相关组件会在后续跟进更新
                - [mirai组件](https://github.com/simple-robot/simbot-component-mirai/releases)
                - [腾讯频道组件](https://github.com/simple-robot/simbot-component-tencent-guild/releases)
                - [开黑啦组件](https://github.com/simple-robot/simbot-component-kaiheila/releases)

                ## 仓库参考
                
                - [simbot-api: $version](https://repo1.maven.org/maven2/love/forte/simbot/simbot-api/$realVersion)
                - [simbot-core: $version](https://repo1.maven.org/maven2/love/forte/simbot/simbot-core/$realVersion)
                - [simbot-logger: $version](https://repo1.maven.org/maven2/love/forte/simbot/simbot-logger/$realVersion)
                - [simboot-api: $version](https://repo1.maven.org/maven2/love/forte/simbot/boot/simboot-api/$realVersion)
                - [simboot-core: $version](https://repo1.maven.org/maven2/love/forte/simbot/boot/simboot-core/$realVersion)
                - [simboot-core-annotation: $version](https://repo1.maven.org/maven2/love/forte/simbot/boot/simboot-core-annotation/$realVersion)
                - [simboot-core-spring-boot-starter: $version](https://repo1.maven.org/maven2/love/forte/simbot/boot/simboot-core-spring-boot-starter/$realVersion)

                
            """.trimIndent()


            file.writeText(autoGenerateText)
        }


    }
}

// idea
idea {
    module {
        isDownloadSources = true
    }
}

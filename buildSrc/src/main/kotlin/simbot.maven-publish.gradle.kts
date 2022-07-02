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

import gradle.kotlin.dsl.accessors._ef8df8565a6e8c0564755ef1bcb196f5.sourceSets

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

val isSnapshotOnly = (System.getProperty("snapshotOnly") ?: System.getenv(Env.SNAPSHOT_ONLY))?.equals("true", true) == true
val isReleaseOnly = (System.getProperty("releaseOnly") ?: System.getenv(Env.RELEASES_ONLY))?.equals("true", true) == true

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
    
    val jarSources by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    
    val jarJavadoc by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }
    
    publishing {
        publications {
            create<MavenPublication>("dist") {
                from(components["java"])
                artifact(jarSources)
                artifact(jarJavadoc)
                
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                
                pom {
                    show()
                    
                    name.set("${project.group}:${project.name}")
                    description.set(project.description ?: P.Simbot.DESCRIPTION)
                    url.set("https://github.com/ForteScarlet/simpler-robot")
                    licenses {
                        license {
                            name.set("GNU GENERAL PUBLIC LICENSE, Version 3")
                            url.set("https://www.gnu.org/licenses/gpl-3.0-standalone.html")
                        }
                        license {
                            name.set("GNU LESSER GENERAL PUBLIC LICENSE, Version 3")
                            url.set("https://www.gnu.org/licenses/lgpl-3.0-standalone.html")
                        }
                    }
                    scm {
                        url.set("https://github.com/ForteScarlet/simpler-robot")
                        connection.set("scm:git:https://github.com/ForteScarlet/simpler-robot.git")
                        developerConnection.set("scm:git:ssh://git@github.com/ForteScarlet/simpler-robot.git")
                    }
                    
                    setupDevelopers()
                }
            }
            
            
            
            repositories {
                mavenLocal()
                configPublishMaven(Sonatype.Central, sonatypeUsername, sonatypePassword)
                configPublishMaven(Sonatype.Snapshot, sonatypeUsername, sonatypePassword)
            }
        }
    }
    
    
    signing {
        val keyId = System.getenv("GPG_KEY_ID")
        val secretKey = System.getenv("GPG_SECRET_KEY")
        val password = System.getenv("GPG_PASSWORD")
        
        setRequired {
            !project.version.toString().endsWith("SNAPSHOT")
        }
        
        useInMemoryPgpKeys(keyId, secretKey, password)
        
        sign(publishing.publications["dist"])
    }
    
    
    println("[publishing-configure] - [$name] configured.")
}


fun RepositoryHandler.configPublishMaven(sonatype: Sonatype, username: String?, password: String?) {
    maven {
        name = sonatype.name
        url = uri(sonatype.url)
        credentials {
            this.username = username
            this.password = password
        }
    }
}

/**
 * 配置开发者/协作者信息。
 *
 */
fun MavenPom.setupDevelopers() {
    developers {
        developer {
            id.set("forte")
            name.set("ForteScarlet")
            email.set("ForteScarlet@163.com")
            url.set("https://github.com/ForteScarlet")
        }
        developer {
            id.set("forliy")
            name.set("ForliyScarlet")
            email.set("ForliyScarlet@163.com")
            url.set("https://github.com/ForliyScarlet")
        }
    }
}

// afterEvaluate {
//     show()
// }

fun show() {
    //// show project info
    println("========================================================")
    println("== project.group:       $group")
    println("== project.name:        $name")
    println("== project.version:     $version")
    println("== project.description: $description")
    println("========================================================")
}
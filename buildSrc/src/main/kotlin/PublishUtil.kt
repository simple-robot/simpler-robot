/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:Suppress("NOTHING_TO_INLINE")

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*


inline fun Project.configurePublishing(artifactId: String) {

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    // val sourcesJar = tasks["sourcesJar"]
    val javadocJar = tasks.register("javadocJar", Jar::class) {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        archiveClassifier.set("javadoc")
    }

    publishing {
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])

                groupId = project.group.toString()
                setArtifactId(artifactId)
                version = project.version.toString()

                setupPom(project = project)
                showPom()

                artifact(sourcesJar)
                artifact(javadocJar.get())
            }
        }

        repositories {
            mavenLocal()
            maven {
                if (version.toString().contains("SNAPSHOTS", true)) {
                    // snapshot
                    name = Sonatype.`snapshot-oss`.NAME
                    url = uri(Sonatype.`snapshot-oss`.URL)
                } else {
                    name = Sonatype.oss.NAME
                    url = uri(Sonatype.oss.URL)
                }

                val username0 = extra.get("sonatype.username")?.toString() ?: run {
                    println("[WARN] Cannot found sonatype.username from extra for $artifactId")
                    return@maven
                }

                val password0 = extra.get("sonatype.password")?.toString()
                    ?: throw NullPointerException("sonatype-password")

                credentials {
                    username = username0
                    password = password0
                }
            }
        }

    }

}

fun Project.configurePublishingLocal(artifactId: String) {

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    // val sourcesJar = tasks["sourcesJar"]
    val javadocJar = tasks.register("javadocJar", Jar::class) {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        archiveClassifier.set("javadoc")
    }

    publishing {
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])

                groupId = project.group.toString()
                setArtifactId(artifactId)
                version = project.version.toString()

                setupPom(project = project)
                showPom()

                artifact(sourcesJar)
                artifact(javadocJar.get())
            }
        }

        repositories {
            mavenLocal()
        }

    }

}

fun MavenPublication.showPom() {
    println("======= pom $artifactId =======")
    println("groupId:    $groupId")
    println("artifactId: $artifactId")
    println("version:    $version")
    println("complete:   $groupId:$artifactId:$version")
    println()
}


fun MavenPublication.setupPom(project: Project) {
    val vcs = "https://github.com/ForteScarlet/simpler-robot"
    pom {
        scm {
            url.set(vcs)
            connection.set("scm:$vcs.git")
            developerConnection.set("scm:${vcs.replace("https:", "git:")}.git")
        }

        issueManagement {
            system.set("GitHub Issue Management")
            url.set("$vcs/issues")
        }

        licenses {
            license {
                name.set("GNU LESSER GENERAL PUBLIC LICENSE, Version 3")
                url.set("https://github.com/ForteScarlet/simpler-robot/blob/master/LICENSE")
            }
        }

        developers {
            developer {
                id.set("forte")
                name.set("ForteScarlet")
                email.set("ForteScarlet@163.com")
            }
        }

        withXml {
            val root = asNode()
            root.appendNode("description", project.description)
            root.appendNode("name", project.name)
            root.appendNode("url", vcs)
        }



    }

}


inline val Project.sourceSets: SourceSetContainer
    get() = extensions.getByName("sourceSets") as SourceSetContainer


fun Project.publishing(configure: PublishingExtension.() -> Unit) {
    (this as ExtensionAware).extensions.configure("publishing", configure)
}


@Suppress("ClassName")
object Sonatype {
    object oss {
        const val NAME = "oss"
        const val URL = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    }

    object `snapshot-oss` {
        const val NAME = "snapshot-oss"
        const val URL = "https://oss.sonatype.org/content/repositories/snapshots/"

    }
}
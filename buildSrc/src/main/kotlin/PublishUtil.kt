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

                groupId = rootProject.group.toString()
                setArtifactId(artifactId)
                version = project.version.toString()

                setupPom(project = project)

                artifact(sourcesJar)
                artifact(javadocJar.get())
            }
        }

        repositories {
            mavenLocal()
            maven {
                if (version.toString().endsWith("SNAPSHOTS", true)) {
                    // snapshot
                    name = Sonatype.`snapshot-oss`.NAME
                    url = uri(Sonatype.`snapshot-oss`.URL)
                } else {
                    name = Sonatype.oss.NAME
                    url = uri(Sonatype.oss.URL)
                }

                val username0 = local().getProperty("sonatype.username")
                    ?: throw NullPointerException("snapshots-sonatype-username")
                val password0 = local().getProperty("sonatype.password")
                    ?: throw NullPointerException("snapshots-sonatype-password")

                credentials {
                    username = username0
                    password = password0
                }
            }
        }

    }

}

fun Project.configurePublishingLocal(artifactId: String) {
// val sourcesJar by tasks.registering(Jar::class) {
    //     archiveClassifier.set("sources")
    //     from(sourceSets["main"].allSource)
    // }
    // // val sourcesJar = tasks["sourcesJar"]
    // val javadocJar = tasks.register("javadocJar", Jar::class) {
    //     @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    //     archiveClassifier.set("javadoc")
    // }
    //
    // publishing {
    //     publications {
    //         register("mavenJava", MavenPublication::class) {
    //             from(components["java"])
    //
    //             groupId = rootProject.group.toString()
    //             version = project.version.toString()
    //
    //             setupPom(project = project)
    //
    //             artifact(sourcesJar)
    //             artifact(javadocJar.get())
    //         }
    //     }
    //
    //     repositories {
    //         mavenLocal().also {
    //             println(it.name)
    //             println(it.url)
    //         }
    //     }
    // }
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
                name.set("The Apache License, Version 2.0")
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
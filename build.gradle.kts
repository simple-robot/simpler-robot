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
    // `maven-publish` apply false
    // signing
    // see https://github.com/gradle-nexus/publish-plugin
    // id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}



group = P.Simbot.GROUP
version = P.Simbot.VERSION

repositories {
    mavenLocal()
    mavenCentral()
}

subprojects {
    group = P.Simbot.GROUP
    version = P.Simbot.VERSION
    apply(plugin = "maven-publish")
    apply(plugin = "java")

    repositories {
        mavenLocal()
        mavenCentral()
    }

    plugins.findPlugin("org.jetbrains.dokka")?.let {
        configDokka()
    }


    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    // val sourcesJar = tasks["sourcesJar"]
    val javadocJar = tasks.register("javadocJar", Jar::class) {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        archiveClassifier.set("javadoc")
    }

    if (name in publishNeed) {
        publishing {
            publications {
                register("mavenJava", MavenPublication::class) {
                    from(components["java"])

                    groupId = rootProject.group.toString()
                    artifactId = artifactId
                    version = project.version.toString()

                    setupPom(project = project)

                    artifact(sourcesJar)
                    artifact(javadocJar.get())
                }
            }

            repositories {
                mavenLocal().also {
                    println(it.name)
                    println(it.url)
                }
            }
        }
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





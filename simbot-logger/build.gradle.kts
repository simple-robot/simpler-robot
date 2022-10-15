import love.forte.gradle.common.kotlin.multiplatform.defaultConfig

plugins {
    kotlin("multiplatform")
    `simbot-simple-project-setup`
    id("simbot.dokka-module-configuration")
    `simbot-multiplatform-maven-publish`
}

kotlin {
    defaultConfig {
        sourceSetsConfig = {
            // val commonTest by getting {
            //     dependencies {
            //         implementation(kotlin("test-common"))
            //     }
            // }
            // commonTest {
            //     dependencies {
            //         implementation(kotlin("test"))
            //     }
            // }
            jvmMain {
                dependencies {
                    api(libs.slf4j.api)
                }
            }
        }
    }
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test")) // This brings all the platform dependencies automatically
            }
        }
    }
}

val p = project

// multiplatformConfigPublishing {
//
//     val groupProject = P::class.sealedSubclasses.mapNotNull { it.objectInstance }.associateBy { obj -> obj.group }
//
//     project = groupProject[p.group] ?: error("unknown project group: ${p.group}")
//
//     val jarJavadoc = tasks.create("jarJavadoc", Jar::class) {
//         group = "documentation"
//         archiveClassifier.set("javadoc")
//         from(tasks.findByName("dokkaHtml"))
//     }
//
//     artifact(jarJavadoc)
//     isSnapshot = project.version.toString().contains("SNAPSHOT", true)
//     releasesRepository = ReleaseRepository
//     snapshotRepository = SnapshotRepository
//     gpg = love.forte.gradle.common.core.Gpg.Companion.ofSystemPropOrNull()
//
// }

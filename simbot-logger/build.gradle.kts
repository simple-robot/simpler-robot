import love.forte.gradle.common.core.project.setup
import love.forte.gradle.common.kotlin.multiplatform.defaultConfig
import love.forte.gradle.common.publication.configure.multiplatformConfigPublishing

plugins {
    kotlin("multiplatform")
    signing
    `maven-publish`
}

setup(P.Simbot)

kotlin {
    defaultConfig {
        sourceSetsConfig = {
            commonTest {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
            jvmMain {
                dependencies {
                    api(libs.slf4j.api)
                }
            }
        }
    }
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
    gpg = love.forte.gradle.common.core.Gpg.Companion.ofSystemPropOrNull()
    
}

plugins {
    kotlin("multiplatform") // version "1.5.31"
}

group = "love.forte.commons"
version = "3.0.0-preview"

repositories {
    mavenCentral()
}

kotlin {
    jvm("jvm") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
        }
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns.all {
            executionTask.configure {
                useJUnit()
            }
        }
    }



    sourceSets {
        all {
            println(this.name.toTargetAndSource())
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
            val (target, source) = name.toTargetAndSource()
            kotlin.setSrcDirs(project.srcList(source, target))
            println(kotlin.srcDirs)
        }
        val jvmMain by getting
        val jvmTest by getting

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}

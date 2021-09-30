plugins {
    kotlin("multiplatform") // version "1.5.31"
}

group = "love.forte.commons"
version = "3.0.0-preview"

repositories {
    mavenCentral()
}

kotlin {
    // 严格
    explicitApi()


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
            // Set src dir like xxx/main/kotlin, xxx/test/kotlin
            val (target, source) = name.toTargetAndSource()
            kotlin.setSrcDirs(project.srcList(source, target))
        }

        @Suppress("UNUSED_VARIABLE")
        val jvmMain by getting
        @Suppress("UNUSED_VARIABLE")
        val jvmTest by getting

        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}

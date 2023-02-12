import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    id("simbot.util-module-conventions")
    `simbot-multiplatform-maven-publish`
    id("simbot.dokka-module-configuration")
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                javaParameters = true
                freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
            }
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        browser()
        nodejs()
    }

     val mainPresets = mutableSetOf<KotlinSourceSet>()
     val testPresets = mutableSetOf<KotlinSourceSet>()

     targets {
         presets.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset<*>>()
             .forEach { presets ->
                 val target = fromPreset(presets, presets.name)
                 mainPresets.add(target.compilations["main"].kotlinSourceSets.first())
                 testPresets.add(target.compilations["test"].kotlinSourceSets.first())
             }
     }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

         val nativeMain by creating {
             dependsOn(commonMain)
         }
         val nativeTest by creating {
             dependsOn(commonTest)
         }

         configure(mainPresets) { dependsOn(nativeMain) }
         configure(testPresets) { dependsOn(nativeTest) }

    }

}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets.configureEach {
    
    }
}

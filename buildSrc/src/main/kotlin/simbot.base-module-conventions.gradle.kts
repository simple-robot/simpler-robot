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

plugins {
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    idea
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri(Sonatype.Snapshot.URL)
        mavenContent {
            snapshotsOnly()
        }
    }
}


dependencies {
    testImplementation(kotlin("test-junit5"))
}


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        javaParameters = true
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
    }
}

kotlin {
    explicitApi()
    this.sourceSets.configureEach {
        languageSettings {
            optIn("kotlin.RequiresOptIn")
            optIn("love.forte.simbot.ExperimentalSimbotApi")
        }
    }
}


tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}

configurations.all {
    // check for updates every build
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}


tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            skipEmptyPackages.set(true)
            jdkVersion.set(8)
            reportUndocumented.set(true)
            if (project.file("Module.md").exists()) {
                includes.from("Module.md")
            }
            displayName.set(project.name)
            
            perPackageOption {
                matchingRegex.set(""".*\.internal.*""") // will match all .internal packages and sub-packages
                suppress.set(true)
            }
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}



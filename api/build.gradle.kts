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
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
}

group = "love.forte.simple-robot"
version = "3.0.0-preview"

repositories {
    mavenCentral()
}


kotlin {
    coreLibrariesVersion = "1.6.0"

    // 严格模式
    explicitApi()

    // Jvm
    jvm("jvm") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
            attribute(SimbotAttributes.MODULE_NAME, "api")
        }

        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                javaParameters = true
            }
        }
        testRuns.all {
            executionTask.configure {
                useJUnit()
            }
        }
    }

    js("js"/*, IR*/) {
        nodejs()
        // browser()
        useCommonJs()
        compilations.all {
            kotlinOptions {
                metaInfo = true
            }
        }
        testRuns.all {
            executionTask.configure {
            }
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }

            // Set src dir like xxx/main/kotlin, xxx/test/kotlin
            val (target, source) = name.toTargetAndSource()
            kotlin.srcDir(project.src(source, target))
            resources.srcDir(project.resources(source, target))

        }


        @Suppress("UNUSED_VARIABLE")
        val jvmMain by getting {
            dependencies {
                implementation(V.Kotlin.Reflect.notation) // "org.jetbrains.kotlin:kotlin-reflect:1.5.31"
                implementation(V.Kotlinx.Coroutines.Core.Jvm.notation)
                implementation(V.Slf4j.Api.notation)
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val jvmTest by getting {
            dependencies {
                implementation(V.Kotlin.Test.Junit.notation)
            }
        }
        // @Suppress("UNUSED_VARIABLE")
        // val jsMain by getting

        @Suppress("UNUSED_VARIABLE")
        val jsTest by getting {
            dependencies {
                implementation(V.Kotlin.Test.Js.notation)
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                // implementation(kotlin("stdlib-common"))
                implementation(V.Kotlin.Stdlib.Common.notation)
                implementation(V.Kotlinx.Coroutines.Core.notation)
                implementation(V.Kotlinx.Serialization.Core.notation)
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val commonTest by getting {
            dependencies {
                implementation(V.Kotlin.Test.Common.notation)
                implementation(V.Kotlin.Test.AnnotatoinsCommon.notation)
                implementation(V.Kotlinx.Serialization.Json.notation)
                implementation(V.Kotlinx.Serialization.Protobuf.notation)
                implementation(V.Kotlinx.Serialization.Properties.notation)
                implementation(V.Okio.notation)
            }
        }
    }

    tasks.dokkaHtml {
        val root = rootProject.rootDir
        outputDirectory.set(File(root, "dokkaOutput/${project.name}"))
    }

}

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

val kotlinVersion = "1.6.0"

plugins {
    `kotlin-dsl`
    // kotlin("gradle-plugin")
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

// kotlin {
//     sourceSets.all {
//         languageSettings {
//             optIn("kotlin.Experimental")
//             optIn("kotlin.RequiresOptIn")
//         }
//     }
// }


dependencies {
    // implementation(V.Kotlin.GradlePlugin.notation)
    // implementation(V.Kotlin.CompilerEmbeddable.notation)
    // api("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.6.0")
    // api("org.jetbrains.kotlin", "kotlin-compiler-embeddable", "1.6.0")
    // implementation("com.android.tools.build", "gradle",  "4.1.1")
    // api(gradleApi())
}

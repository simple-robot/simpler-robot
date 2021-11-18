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

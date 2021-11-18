plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

kotlin {
    sourceSets.all {
        languageSettings {
            optIn("kotlin.Experimental")
            optIn("kotlin.RequiresOptIn")
        }
    }
}


dependencies {
    // implementation(V.Kotlin.GradlePlugin.notation)
    // implementation(V.Kotlin.CompilerEmbeddable.notation)
    implementation("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.6.0")
    implementation("org.jetbrains.kotlin", "kotlin-compiler-embeddable", "1.6.0")
    // // implementation("com.android.tools.build", "gradle",  "4.1.1")
    api(gradleApi())
}

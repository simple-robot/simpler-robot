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
    implementation("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.5.31")
    implementation("org.jetbrains.kotlin", "kotlin-compiler-embeddable", "1.5.31")
    // implementation("com.android.tools.build", "gradle",  "4.1.1")
    api(gradleApi())
}
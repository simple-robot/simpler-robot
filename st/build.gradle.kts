import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {
        withJava()
    }
}
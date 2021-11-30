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
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
}


repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api(project(":cores:core"))
    api("love.forte.simple-robot:tencent-guild-core:0.0.1")
    api(V.Ktor.Client.Jvm.Core.notation)
    api(V.Ktor.Client.Jvm.CIO.notation)
    api(V.Ktor.Client.Auth.notation)
    api(V.Ktor.Client.Websockets.notation)
    api(V.Ktor.Client.Serialization.notation)
    api(V.Kotlinx.Serialization.Json.notation)

    testImplementation(V.Kotlin.Test.Junit5.notation)
    // implementation("love.forte.simple-robot:api:3.0.0-PREVIEW")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        javaParameters = true
        jvmTarget = "1.8"
    }
}

kotlin {
    // 严格模式
    explicitApiWarning()


    sourceSets.all {
        languageSettings {
            optIn("kotlin.RequiresOptIn")
        }
    }
}
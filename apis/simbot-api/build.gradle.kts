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
    `maven-publish`
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")

}

dependencies {
    api(V.Kotlinx.Coroutines.Core.Jvm.notation)
    api(V.Slf4j.Api.notation)
    api(V.Kotlinx.Serialization.Core.notation)
    api(V.Kotlin.Reflect.notation)
    compileOnly(V.Jetbrains.Annotations.notation)

    testImplementation(V.Kotlin.Reflect.notation)
    testImplementation(V.Kotlin.Test.Junit5.notation)
    testImplementation(V.Kotlinx.Serialization.Json.notation)
    testImplementation(V.Kotlinx.Serialization.Properties.notation)
    testImplementation(V.Kotlinx.Serialization.Protobuf.notation)
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
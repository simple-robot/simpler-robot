/*
 *  Copyright (c) 2021-2022 ForteScarlet <https://github.com/ForteScarlet>
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

tasks.getByName<Test>("test") {
    useJUnit()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        javaParameters = true
        jvmTarget = "1.8"
    }
}

dependencies {
    api(project(":boots:simboot-api"))
    api("javax.inject:javax.inject:1")

    // compileOnly(project(":simbot-annotation"))

    compileOnly(V.Javax.AnnotationApi.notation)
    compileOnly(P.AnnotationTool.Api.notation)
    compileOnly(V.Spring.Boot.Autoconfigure.notation)
    testImplementation(V.Kotlin.Test.Junit.notation)
    testImplementation(V.Kotlinx.Serialization.Json.notation)
    testImplementation(V.Kotlinx.Serialization.Properties.notation)
    testImplementation(V.Kotlinx.Serialization.Protobuf.notation)
}
repositories {
    mavenCentral()
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


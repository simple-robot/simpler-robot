/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    kotlin("kapt")
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
    api(project(":boots:simboot-core"))
    api(V.Javax.Inject.notation)
    api(P.ForteDI.Spring.notation)

    implementation(V.Spring.Boot.Autoconfigure.notation)
    implementation(V.Spring.Boot.ConfigurationProcessor.notation)
    annotationProcessor(V.Spring.Boot.ConfigurationProcessor.notation)
    kapt(V.Spring.Boot.ConfigurationProcessor.notation)

    compileOnly(V.Javax.AnnotationApi.notation)
    compileOnly(P.AnnotationTool.Api.notation)
    testImplementation(V.Kotlin.Test.Junit.notation)
    testImplementation(V.Kotlinx.Serialization.Json.notation)
    testImplementation(V.Kotlinx.Serialization.Properties.notation)
    testImplementation(V.Kotlinx.Serialization.Protobuf.notation)

    testImplementation(V.Log4j.Api.notation)
    testImplementation(V.Log4j.Core.notation)
    testImplementation(V.Log4j.Slf4jImpl.notation)
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


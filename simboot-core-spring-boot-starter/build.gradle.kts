/*
 * Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

plugins {
    id("simbot.boot-module-conventions")
    `simbot-jvm-maven-publish`
    kotlin("plugin.serialization")
    kotlin("kapt")
}

dependencies {
    api(project(":simboot-core"))
    
    api(libs.javax.inject)
    api(libs.spring.boot.logging)

    compileOnly(libs.spring.boot.autoconfigure)
    compileOnly(libs.spring.boot.configuration.processor)
    annotationProcessor(libs.spring.boot.configuration.processor)
    kapt(libs.spring.boot.configuration.processor)

    compileOnly(libs.javax.annotation.api)
    compileOnly(project(":simbot-util-annotation-tool"))
    
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.serialization.properties)
    testImplementation(libs.kotlinx.serialization.protobuf)
    testImplementation(libs.spring.boot.aop)
    testImplementation(libs.spring.boot.autoconfigure)
    testImplementation(libs.spring.boot.configuration.processor)
    // testImplementation(libs.spring.boot.webflux)

    // @Suppress("VulnerableLibrariesLocal")
    testImplementation("love.forte.simbot.component:simbot-component-mirai-core:3.0.0.0.preview.13.0")
    // testImplementation(V.Kotlinx.Coroutines.Reactor.notation)
}


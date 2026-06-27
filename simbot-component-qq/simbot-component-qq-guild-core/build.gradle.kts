/*
 *     Copyright (c) 2022-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

plugins {
    id("simbot.kotlin-multiplatform")
    id("simbot.qq.kotlin-multiplatform-convention")
    kotlin("plugin.serialization")
    alias(libs.plugins.dokka)
    alias(libs.plugins.suspendTransform)
    id("simbot-maven-publish")
}

configJavaCompileWithModule("simbot.component.qqguild.core")

kotlin {
    compilerOptions {
        optIn.add("love.forte.simbot.qguild.QGInternalApi")
        optIn.add("love.forte.simbot.qguild.ApiModelConstructor")
    }

    configKotlinJvm()

    js {
        configJs()
    }

    applyTier1()
    applyTier2()
    // TODO multiplatform-crypto-libsodium 不支持 watchosX64 target 和 android native targets
    applyTier3(supportKtorClient = true, watchosX64 = false, androidNative = false, watchosDeviceArm64 = false)

    sourceSets {
        commonMain.dependencies {
            // api (compile only for JVM)
            implementation(project(":simbot-api"))

            api(project(":simbot-component-qq:simbot-component-qq-guild-stdlib"))
            api(project(":simbot-commons:simbot-common-annotations"))
            // ktor
            api(libs.ktor.client.contentNegotiation)
            api(libs.ktor.serialization.kotlinxJson)
            api(libs.ktor.client.ws)
            // datetime
            api(libs.kotlinx.datetime)
            // io (runtime scope)
            implementation(libs.kotlinx.io.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("reflect"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.mock)
            implementation(project(":simbot-cores:simbot-core"))
            implementation(project(":simbot-commons:simbot-common-core"))
        }

        jvmTest.dependencies {
            compileOnly(project(":simbot-api"))
            implementation(libs.ktor.client.java)

            implementation(libs.log4j.api)
            implementation(libs.log4j.core)
            implementation(libs.log4j.slf4j2)
        }

        mingwTest.dependencies {
            implementation(libs.ktor.client.winhttp)
        }

        linuxTest.dependencies {
            implementation(libs.ktor.client.cio)
        }

        appleTest.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

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
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    id("love.forte.plugin.suspend-transform")
}

configJavaCompileWithModule("simbot.component.qqguild.stdlib")
apply(plugin = "simbot-maven-publish")

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    compilerOptions {
        optIn.add("love.forte.simbot.qguild.QGInternalApi")
    }

    configKotlinJvm()

    js(IR) {
        configJs()
    }

    applyTier1()
    applyTier2(
        supportKtorClient = true,
        // multiplatform-crypto-libsodium 不支持 watchosX64 target.
        watchosX64 = false
    )
    applyTier3(supportKtorClient = true)

    sourceSets {
        commonMain.dependencies {
            api(project(":simbot-component-qq:simbot-component-qq-guild-api"))
            implementation(project(":simbot-component-qq:simbot-component-qq-guild-internal-ed25519"))
            api(project(":simbot-commons:simbot-common-stage-loop"))
            api(project(":simbot-commons:simbot-common-atomic"))
            api(project(":simbot-commons:simbot-common-core"))
            api(project(":simbot-commons:simbot-common-suspend-runner"))
            api(project(":simbot-commons:simbot-common-annotations"))
            // ktor
            api(libs.ktor.client.contentNegotiation)
            api(libs.ktor.serialization.kotlinxJson)
            api(libs.ktor.client.ws)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }

        jvmTest.dependencies {
            implementation(libs.ktor.client.java)
            implementation(libs.log4j.api)
            implementation(libs.log4j.core)
            implementation(libs.log4j.slf4j2)
            implementation(libs.bouncycastle.bcprov.jdk18on)
        }

        jsMain.dependencies {
            api(libs.ktor.client.js)
        }

        mingwTest.dependencies {
            implementation(libs.ktor.client.winhttp)
        }
    }
}

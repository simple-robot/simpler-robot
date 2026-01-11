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
    id("org.jetbrains.dokka")
}

configJavaCompileWithModule("simbot.component.qqguild.internal.ed25519s")
apply(plugin = "simbot-maven-publish")

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    configKotlinJvm()

    js(IR) {
        configJs {
            // useEsModules()
        }
    }

    applyTier1()
    applyTier2(
        supportKtorClient = true,
        // multiplatform-crypto-libsodium 不支持 watchosX64 target.
        watchosX64 = false,
    )
    applyTier3(
        supportKtorClient = true,
        androidNativeArm32 = false,
        androidNativeArm64 = false,
        androidNativeX64 = false,
        androidNativeX86 = false,
        watchosDeviceArm64 = false,
    )

    sourceSets {
        commonMain.dependencies {
            implementation(project(":simbot-logger"))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }

        jvmMain.dependencies {
            implementation(libs.i2p.crypto.eddsa)
            compileOnly(libs.bouncycastle.bcprov.jdk18on)
        }

        jvmTest.dependencies {
            implementation(libs.i2p.crypto.eddsa)
            implementation(libs.bouncycastle.bcprov.jdk18on)
        }

        jsMain.dependencies {
            implementation(libs.libsodium.bindings)
            implementation(npm("libsodium-wrappers-sumo", "0.7.13"))
        }

        nativeMain.dependencies {
            implementation(libs.libsodium.bindings)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

dokka {
    dokkaSourceSets.all {
        suppress = true
    }
}

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
    alias(libs.plugins.dokka)
    id("simbot-maven-publish")
}

configJavaCompileWithModule("simbot.component.qqguild.internal.ed25519s")

kotlin {
    configKotlinJvm()

    js {
        configJs {
            // useEsModules()
        }
    }

    applyTier1()
    applyTier2()
    // TODO multiplatform-crypto-libsodium 不支持 watchosX64 target 和 android native targets
    applyTier3(supportKtorClient = true, watchosX64 = false, androidNative = false, watchosDeviceArm64 = false)

    sourceSets {
        commonMain.dependencies {
            api(project(":simbot-logger"))
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

/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

import org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithPresetFunctions
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

// TODO 库级别的模块，支持所有层级依赖；
//  但应用级别的模块，尤其是 qg 或有 ktor 依赖的模块，只支持部分。

/**
 * 应用 Kotlin/Native Tier 1 目标平台。
 *
 * @see <a href="https://kotlinlang.org/docs/native-target-support.html#tier-1">Kotlin/Native Tier 1</a>
 */
@Suppress("UNUSED_PARAMETER")
fun KotlinTargetContainerWithPresetFunctions.applyTier1(): List<KotlinNativeTarget> {
    return buildList {
        add(macosArm64())
        add(iosSimulatorArm64())
        add(iosArm64())
    }
}

/**
 * 应用 Kotlin/Native Tier 2 目标平台。
 *
 * @see <a href="https://kotlinlang.org/docs/native-target-support.html#tier-2">Kotlin/Native Tier 2</a>
 */
fun KotlinTargetContainerWithPresetFunctions.applyTier2(): List<KotlinNativeTarget> {
    return buildList {
        add(linuxX64())
        add(linuxArm64())
        // Apple macOS hosts
        add(watchosSimulatorArm64())
        add(watchosArm32())
        add(watchosArm64())
        add(tvosSimulatorArm64())
        add(tvosArm64())
    }
}

/**
 * 应用 Kotlin/Native Tier 3 目标平台。
 *

 * @param supportKtorServer 是否基于 Ktor Server 支持过滤目标平台
 * @param supportKtorClient 是否基于 Ktor Client 支持过滤目标平台
 *
 * @see <a href="https://kotlinlang.org/docs/native-target-support.html#tier-3">Kotlin/Native Tier 3</a>
 */
@Suppress("DEPRECATION")
fun KotlinTargetContainerWithPresetFunctions.applyTier3(
    supportKtorServer: Boolean = false,
    supportKtorClient: Boolean = false,
    watchosX64: Boolean = true,
    watchosDeviceArm64: Boolean = true,
    androidNative: Boolean = true,
): List<KotlinNativeTarget> {
    return buildList {
        if (androidNative) {
            add(androidNativeArm32())
            add(androidNativeArm64())
            add(androidNativeX86())
            add(androidNativeX64())
        }
        add(mingwX64())
        // Apple macOS hosts
        if (watchosDeviceArm64) {
            add(watchosDeviceArm64())
        }
        add(macosX64())
        add(iosX64())
        if (watchosX64) {
            add(watchosX64())
        }
        add(tvosX64())
    }
}

/**
 * 应用所有 Kotlin/Native 层级的目标平台（Tier 1、2 和 3）。
 *
 * @param supportKtorServer 是否基于 Ktor Server 支持过滤目标平台
 * @param supportKtorClient 是否基于 Ktor Client 支持过滤目标平台
 *
 * @see applyTier1
 * @see applyTier2
 * @see applyTier3
 */
fun KotlinTargetContainerWithPresetFunctions.applyTier123(
    supportKtorServer: Boolean = false,
    supportKtorClient: Boolean = false,
): List<KotlinNativeTarget> {
    return buildList {
        addAll(applyTier1())
        addAll(applyTier2())
        addAll(applyTier3(supportKtorServer, supportKtorClient))
    }
}

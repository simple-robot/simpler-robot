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

/**
 * Kotlin/Native 目标平台的层级（Tier）分类。
 *
 * @see <a href="https://kotlinlang.org/docs/native-target-support.html">Kotlin/Native target support</a>
 */
object NativeTargets {
    /**
     * Kotlin/Native 目标平台的层级枚举。
     *
     * @see <a href="https://kotlinlang.org/docs/native-target-support.html">Kotlin/Native target support</a>
     */
    enum class Tier {
        TIER1, TIER2, TIER3
    }

    /**
     * Native 目标平台枚举值及其层级分类。
     */
    enum class Value(
        val tier: Tier,
        val targetName: String,
    ) {
        // Tier 1
        MACOS_X64(Tier.TIER1, "macosX64"),
        MACOS_ARM64(Tier.TIER1, "macosArm64"),
        IOS_SIMULATOR_ARM64(Tier.TIER1, "iosSimulatorArm64"),
        IOS_X64(Tier.TIER1, "iosX64"),

        // Tier 2
        LINUX_X64(Tier.TIER2, "linuxX64"),
        LINUX_ARM64(Tier.TIER2, "linuxArm64"),
        WATCHOS_SIMULATOR_ARM64(Tier.TIER2, "watchosSimulatorArm64"),
        WATCHOS_X64(Tier.TIER2, "watchosX64"),
        WATCHOS_ARM32(Tier.TIER2, "watchosArm32"),
        WATCHOS_ARM64(Tier.TIER2, "watchosArm64"),
        TVOS_SIMULATOR_ARM64(Tier.TIER2, "tvosSimulatorArm64"),
        TVOS_X64(Tier.TIER2, "tvosX64"),
        TVOS_ARM64(Tier.TIER2, "tvosArm64"),
        IOS_ARM64(Tier.TIER2, "iosArm64"),

        // Tier 3
        ANDROID_NATIVE_ARM32(Tier.TIER3, "androidNativeArm32"),
        ANDROID_NATIVE_ARM64(Tier.TIER3, "androidNativeArm64"),
        ANDROID_NATIVE_X86(Tier.TIER3, "androidNativeX86"),
        ANDROID_NATIVE_X64(Tier.TIER3, "androidNativeX64"),
        MINGW_X64(Tier.TIER3, "mingwX64"),
        WATCHOS_DEVICE_ARM64(Tier.TIER3, "watchosDeviceArm64"),
        ;
    }
}

/**
 * 应用 Kotlin/Native Tier 1 目标平台。
 *
 * Tier 1 目标平台：
 * - macosX64
 * - macosArm64
 * - iosSimulatorArm64
 * - iosX64
 *
 * @param supportKtorServer 是否基于 Ktor Server 支持过滤目标平台（在 Tier 1 中无影响）
 * @param supportKtorClient 是否基于 Ktor Client 支持过滤目标平台（在 Tier 1 中无影响）
 * @param macosX64 是否包含 macosX64 目标平台
 * @param macosArm64 是否包含 macosArm64 目标平台
 * @param iosSimulatorArm64 是否包含 iosSimulatorArm64 目标平台
 * @param iosX64 是否包含 iosX64 目标平台
 * @param configureEach 每个目标平台的配置块
 *
 * @see <a href="https://kotlinlang.org/docs/native-target-support.html#tier-1">Kotlin/Native Tier 1</a>
 */
@Suppress("UNUSED_PARAMETER")
inline fun KotlinTargetContainerWithPresetFunctions.applyTier1(
    supportKtorServer: Boolean = false,
    supportKtorClient: Boolean = false,
    macosX64: Boolean = true,
    macosArm64: Boolean = true,
    iosSimulatorArm64: Boolean = true,
    iosX64: Boolean = true,
    crossinline configureEach: KotlinNativeTarget.(targetValue: NativeTargets.Value) -> Unit = { }
) {
    if (macosX64) {
        macosX64 {
            configureEach(NativeTargets.Value.MACOS_X64)
        }
    }
    if (macosArm64) {
        macosArm64 {
            configureEach(NativeTargets.Value.MACOS_ARM64)
        }
    }
    if (iosSimulatorArm64) {
        iosSimulatorArm64 {
            configureEach(NativeTargets.Value.IOS_SIMULATOR_ARM64)
        }
    }
    if (iosX64) {
        iosX64 {
            configureEach(NativeTargets.Value.IOS_X64)
        }
    }
}

/**
 * 应用 Kotlin/Native Tier 2 目标平台。
 *
 * Tier 2 目标平台：
 * - linuxX64
 * - linuxArm64
 * - watchosSimulatorArm64
 * - watchosX64
 * - watchosArm32
 * - watchosArm64
 * - tvosSimulatorArm64
 * - tvosX64
 * - tvosArm64
 * - iosArm64
 *
 * @param supportKtorServer 是否基于 Ktor Server 支持过滤目标平台
 * @param supportKtorClient 是否基于 Ktor Client 支持过滤目标平台
 * @param linuxX64 是否包含 linuxX64 目标平台
 * @param linuxArm64 是否包含 linuxArm64 目标平台（当需要 Ktor 2.x 支持时默认禁用）
 * @param watchosSimulatorArm64 是否包含 watchosSimulatorArm64 目标平台
 * @param watchosX64 是否包含 watchosX64 目标平台
 * @param watchosArm32 是否包含 watchosArm32 目标平台
 * @param watchosArm64 是否包含 watchosArm64 目标平台
 * @param tvosSimulatorArm64 是否包含 tvosSimulatorArm64 目标平台
 * @param tvosX64 是否包含 tvosX64 目标平台
 * @param tvosArm64 是否包含 tvosArm64 目标平台
 * @param iosArm64 是否包含 iosArm64 目标平台
 * @param configureEach 每个目标平台的配置块
 *
 * @see <a href="https://kotlinlang.org/docs/native-target-support.html#tier-2">Kotlin/Native Tier 2</a>
 */
inline fun KotlinTargetContainerWithPresetFunctions.applyTier2(
    supportKtorServer: Boolean = false,
    supportKtorClient: Boolean = false,
    linuxX64: Boolean = true,
    linuxArm64: Boolean = !supportKtorServer && !supportKtorClient,
    watchosSimulatorArm64: Boolean = true,
    watchosX64: Boolean = true,
    watchosArm32: Boolean = true,
    watchosArm64: Boolean = true,
    tvosSimulatorArm64: Boolean = true,
    tvosX64: Boolean = true,
    tvosArm64: Boolean = true,
    iosArm64: Boolean = true,
    crossinline configureEach: KotlinNativeTarget.(targetValue: NativeTargets.Value) -> Unit = { }
) {
    if (linuxX64) {
        linuxX64 {
            configureEach(NativeTargets.Value.LINUX_X64)
        }
    }
    if (linuxArm64) {
        linuxArm64 {
            configureEach(NativeTargets.Value.LINUX_ARM64)
        }
    }
    if (watchosSimulatorArm64) {
        watchosSimulatorArm64 {
            configureEach(NativeTargets.Value.WATCHOS_SIMULATOR_ARM64)
        }
    }
    if (watchosX64) {
        watchosX64 {
            configureEach(NativeTargets.Value.WATCHOS_X64)
        }
    }
    if (watchosArm32) {
        watchosArm32 {
            configureEach(NativeTargets.Value.WATCHOS_ARM32)
        }
    }
    if (watchosArm64) {
        watchosArm64 {
            configureEach(NativeTargets.Value.WATCHOS_ARM64)
        }
    }
    if (tvosSimulatorArm64) {
        tvosSimulatorArm64 {
            configureEach(NativeTargets.Value.TVOS_SIMULATOR_ARM64)
        }
    }
    if (tvosX64) {
        tvosX64 {
            configureEach(NativeTargets.Value.TVOS_X64)
        }
    }
    if (tvosArm64) {
        tvosArm64 {
            configureEach(NativeTargets.Value.TVOS_ARM64)
        }
    }
    if (iosArm64) {
        iosArm64 {
            configureEach(NativeTargets.Value.IOS_ARM64)
        }
    }
}

/**
 * 应用 Kotlin/Native Tier 3 目标平台。
 *
 * Tier 3 目标平台：
 * - androidNativeArm32
 * - androidNativeArm64
 * - androidNativeX86
 * - androidNativeX64
 * - mingwX64
 * - watchosDeviceArm64
 *
 * @param supportKtorServer 是否基于 Ktor Server 支持过滤目标平台
 * @param supportKtorClient 是否基于 Ktor Client 支持过滤目标平台
 * @param androidNativeArm32 是否包含 androidNativeArm32 目标平台（当需要 Ktor 2.x 支持时默认禁用）
 * @param androidNativeArm64 是否包含 androidNativeArm64 目标平台（当需要 Ktor 2.x 支持时默认禁用）
 * @param androidNativeX86 是否包含 androidNativeX86 目标平台（当需要 Ktor 2.x 支持时默认禁用）
 * @param androidNativeX64 是否包含 androidNativeX64 目标平台（当需要 Ktor 2.x 支持时默认禁用）
 * @param mingwX64 是否包含 mingwX64 目标平台（当需要 Ktor Server 支持时默认禁用）
 * @param watchosDeviceArm64 是否包含 watchosDeviceArm64 目标平台（当需要 Ktor 2.x 支持时默认禁用）
 * @param configureEach 每个目标平台的配置块
 *
 * @see <a href="https://kotlinlang.org/docs/native-target-support.html#tier-3">Kotlin/Native Tier 3</a>
 */
inline fun KotlinTargetContainerWithPresetFunctions.applyTier3(
    supportKtorServer: Boolean = false,
    supportKtorClient: Boolean = false,
    androidNativeArm32: Boolean = !supportKtorServer && !supportKtorClient,
    androidNativeArm64: Boolean = !supportKtorServer && !supportKtorClient,
    androidNativeX86: Boolean = !supportKtorServer && !supportKtorClient,
    androidNativeX64: Boolean = !supportKtorServer && !supportKtorClient,
    mingwX64: Boolean = !supportKtorServer,
    watchosDeviceArm64: Boolean = !supportKtorServer && !supportKtorClient,
    crossinline configureEach: KotlinNativeTarget.(targetValue: NativeTargets.Value) -> Unit = { },
) {
    if (androidNativeArm32) {
        androidNativeArm32 {
            configureEach(NativeTargets.Value.ANDROID_NATIVE_ARM32)
        }
    }
    if (androidNativeArm64) {
        androidNativeArm64 {
            configureEach(NativeTargets.Value.ANDROID_NATIVE_ARM64)
        }
    }
    if (androidNativeX86) {
        androidNativeX86 {
            configureEach(NativeTargets.Value.ANDROID_NATIVE_X86)
        }
    }
    if (androidNativeX64) {
        androidNativeX64 {
            configureEach(NativeTargets.Value.ANDROID_NATIVE_X64)
        }
    }
    if (mingwX64) {
        mingwX64 {
            configureEach(NativeTargets.Value.MINGW_X64)
        }
    }
    if (watchosDeviceArm64) {
        watchosDeviceArm64 {
            configureEach(NativeTargets.Value.WATCHOS_DEVICE_ARM64)
        }
    }
}

/**
 * 应用所有 Kotlin/Native 层级的目标平台（Tier 1、2 和 3）。
 *
 * @param supportKtorServer 是否基于 Ktor Server 支持过滤目标平台
 * @param supportKtorClient 是否基于 Ktor Client 支持过滤目标平台
 * @param configureEach 每个目标平台的配置块
 *
 * @see applyTier1
 * @see applyTier2
 * @see applyTier3
 */
inline fun KotlinTargetContainerWithPresetFunctions.applyTier123(
    supportKtorServer: Boolean = false,
    supportKtorClient: Boolean = false,
    crossinline configureEach: KotlinNativeTarget.(targetValue: NativeTargets.Value) -> Unit = { },
) {
    applyTier1(
        supportKtorServer = supportKtorServer,
        supportKtorClient = supportKtorClient,
        configureEach = configureEach
    )
    applyTier2(
        supportKtorServer = supportKtorServer,
        supportKtorClient = supportKtorClient,
        configureEach = configureEach
    )
    applyTier3(
        supportKtorServer = supportKtorServer,
        supportKtorClient = supportKtorClient,
        configureEach = configureEach
    )
}

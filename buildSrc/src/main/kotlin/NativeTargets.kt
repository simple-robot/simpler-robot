/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * see [Official K/N targets](https://kotlinlang.org/docs/native-target-support.html)
 */
object NativeTargets {

    /**
     * [Tier1](https://kotlinlang.org/docs/native-target-support.html#tier-1)
     */
    val tier1 = setOf(
        "linuxX64",
        "macosX64",
        "macosArm64",
        "iosSimulatorArm64",
        "iosX64",
    )

    /**
     * [Tier2](https://kotlinlang.org/docs/native-target-support.html#tier-2)
     */
    val tier2 = setOf(
        "linuxArm64",
        "watchosSimulatorArm64",
        "watchosX64",
        "watchosArm32",
        "watchosArm64",
        "tvosSimulatorArm64",
        "tvosX64",
        "tvosArm64",
        "iosArm64",
    )

    /**
     * [Tier3](https://kotlinlang.org/docs/native-target-support.html#tier-3)
     */
    val tier3 = setOf(
        "androidNativeArm32",
        "androidNativeArm64",
        "androidNativeX86",
        "androidNativeX64",
        "mingwX64",
        "watchosDeviceArm64",
    )

    /**
     * [tier1] + [tier2] + [tier3]
     */
    val allTiers = tier1 + tier2 + tier3

}

/*
val supportTargets = setOf(
        // Tier 3

    )
 */

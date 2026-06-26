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

import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsNodeDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink


inline fun KotlinJsTargetDsl.configJs(
    crossinline configNodejs: KotlinJsNodeDsl.() -> Unit = {},
    block: () -> Unit = {}
) {
    nodejs {
        testTask {
            useMocha {
                timeout = "30s"
            }
        }
        configNodejs()
    }

    binaries.library()
    block()
}

inline fun KotlinWasmJsTargetDsl.configWasmJs(
    wasmModuleName: String? = null,
    crossinline configNodejs: KotlinJsNodeDsl.() -> Unit = {},
    block: () -> Unit = {}
) {
    val resolvedWasmModuleName = wasmModuleName ?: project.name

    nodejs {
        testTask {
            // TODO Mocha test framework for Wasm target is not supported. For KotlinWasmNode used
            // useMocha {
            //     timeout = "30s"
            // }
        }
        configNodejs()
    }

    this.compilerOptions {
        moduleName.set(resolvedWasmModuleName)
    }
    binaries.library()
    project.tasks.withType(KotlinJsIrLink::class.java).configureEach {
        if (name.endsWith("KotlinWasmJs")) {
            compilerOptions.moduleName.set(resolvedWasmModuleName)
        }
    }
    block()
}

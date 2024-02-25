/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension


inline fun KotlinJsTargetDsl.configJs(
    nodeJs: Boolean = true,
    browser: Boolean = true,
    block: () -> Unit = {}
) {
    if (nodeJs) {
        nodejs {
            testTask {
                useMocha {
                    timeout = "10000"
                }
            }
        }
    }

    if (browser) {
        browser {
            testTask{
                useKarma {
                    useChromeHeadless()
                    // useConfigDirectory(File(project.rootProject.projectDir, "karma"))
                }
            }
        }
    }

    binaries.library()
    block()
}


fun Project.configJsTestTasks() {
    // val shouldRunJsBrowserTest = !hasProperty("teamcity") || hasProperty("enable-js-tests")
    // if (shouldRunJsBrowserTest) return
    tasks.findByName("cleanJsBrowserTest")?.apply {
        onlyIf { false }
    }
    tasks.findByName("jsBrowserTest")?.apply {
        onlyIf { false }
    }
}

inline fun KotlinWasmJsTargetDsl.configWasmJs(
    nodeJs: Boolean = true,
    browser: Boolean = true,
    block: () -> Unit = {}
) {
    if (nodeJs && isLinux) {
        // win in candy node `21.0.0-v8-canary202309143a48826a08` is not supported
        // nodejs()
    }

    if (browser) {
        browser {
            testTask{
                useKarma {
                    useChromeHeadless()
                    // useConfigDirectory(File(project.rootProject.projectDir, "karma"))
                }
            }
        }
    }

    binaries.library()
    block()
}

inline fun Project.configWasmJsTest(block: () -> Unit = {}) {
    if (false) {
        // see https://youtrack.jetbrains.com/issue/KT-63014/Running-tests-with-wasmJs-in-1.9.20-requires-Chrome-Canary#focus=Comments-27-8321383.0-0
        rootProject.the<NodeJsRootExtension>().apply {
            // nodeVersion = "21.0.0-v8-canary202309143a48826a08"
            nodeVersion = "21.0.0-v8-canary202309143a48826a08"
            nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
        }

        tasks.withType<org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask>().configureEach {
            args.add("--ignore-engines")
        }
    }

    block()
}

/*
 *     Copyright (c) 2024-2025. ForteScarlet.
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

import changelog.GenerateChangelogTask
import changelog.GenerateSubChangelogTask
import love.forte.plugin.suspendtrans.configuration.ClassInfo
import love.forte.plugin.suspendtrans.configuration.SuspendTransformConfigurations.jvmAsyncAnnotationInfo
import love.forte.plugin.suspendtrans.configuration.SuspendTransformConfigurations.jvmAsyncTransformer
import love.forte.plugin.suspendtrans.configuration.SuspendTransformConfigurations.jvmBlockingAnnotationInfo
import love.forte.plugin.suspendtrans.configuration.SuspendTransformConfigurations.jvmBlockingTransformer
import love.forte.plugin.suspendtrans.configuration.SuspendTransformConfigurations.jvmSyntheticClassInfo
import love.forte.plugin.suspendtrans.gradle.ClassInfoSpec
import love.forte.plugin.suspendtrans.gradle.SuspendTransformPluginExtension
import love.forte.plugin.suspendtrans.gradle.TransformerSpec

plugins {
    idea
    id("org.jetbrains.dokka")
    // id("simbot.dokka-multi-module")
    id("com.github.gmazzo.buildconfig") version "5.5.1" apply false
    alias(libs.plugins.detekt)
    id("simbot.nexus-publish")
    alias(libs.plugins.suspendTransform) apply false

    // https://www.jetbrains.com/help/qodana/code-coverage.html
    // https://github.com/Kotlin/kotlinx-kover
    alias(libs.plugins.kotlinxKover)

    alias(libs.plugins.kotlinxBinaryCompatibilityValidator)


}

setupGroup(P.Simbot)

// https://github.com/detekt/detekt/blob/main/build.gradle.kts

repositories {
    mavenCentral()
    love.forte.gradle.common.core.repository.Repositories.Snapshot.Default.apply {
        configMaven {
            mavenContent {
                snapshotsOnly()
            }
        }
    }
    mavenLocal()
}

val root = project

allprojects {
    group = P.GROUP
    version = if (isSnapshot()) P.NEXT_SNAPSHOT_VERSION else P.VERSION
    description = P.DESCRIPTION
}

subprojects {
    repositories {
        mavenCentral()
        love.forte.gradle.common.core.repository.Repositories.Snapshot.Default.apply {
            configMaven {
                mavenContent {
                    snapshotsOnly()
                }
            }
        }
        mavenLocal()
    }

    afterEvaluate {
        applyKover(root)

        if (plugins.hasPlugin(libs.plugins.suspendTransform.get().pluginId)) {
            configureSuspendTransform()
        }
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${libs.versions.detekt.get()}")
}

//region config detekt
detekt {
    source.setFrom(subprojects.map { it.projectDir.absoluteFile })
    config.setFrom(rootDir.resolve("config/detekt/detekt.yml"))
    baseline = rootDir.resolve("config/detekt/baseline.xml")
    // buildUponDefaultConfig = true
    parallel = true
    reportsDir = rootProject.layout.buildDirectory.dir("reports/detekt").get().asFile
    if (!isCi) {
        autoCorrect = true
    }
    basePath = projectDir.absolutePath
}

// https://detekt.dev/blog/2019/03/03/configure-detekt-on-root-project/
tasks.detekt {
    // internal 处理器不管
    exclude("internal-processors/**")
    // tests 不管
    exclude("tests/**")

    include("**/src/*Main/kotlin/**/*.kt")
    include("**/src/*Main/kotlin/**/*.java")
    include("**/src/*Main/java/**/*.kt")
    include("**/src/*Main/java/**/*.java")
    include("**/src/main/kotlin/**/*.kt")
    include("**/src/main/kotlin/**/*.java")
    include("**/src/main/java/**/*.kt")
    include("**/src/main/java/**/*.java")

    exclude("**/src/*/resources/")
    exclude("**/build/")
    exclude("**/*Test/kotlin/")
    exclude("**/*Test/java/")
    exclude("**/test/kotlin/")
    exclude("**/test/java/")
}

fun Project.applyKover(rp: Project) {
    val hasKt =
        plugins.hasPlugin("org.jetbrains.kotlin.jvm") ||
            plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")


    if (hasKt) {
        apply(plugin = "org.jetbrains.kotlinx.kover")
        rp.dependencies {
            kover(project(path))
        }
    }
}
//endregion

apiValidation {
    ignoredPackages.add("*.internal.*")

    ignoredProjects.addAll(
        listOf(
            "interface-uml-processor",
            "simbot-test",
            "tests",
            "spring-boot-starter-test",
            "simbot-processor-class-builder-test"
        )
    )

    // 实验性和内部API可能无法保证二进制兼容
    nonPublicMarkers.addAll(
        listOf(
            "love.forte.simbot.annotations.ExperimentalSimbotAPI",
            "love.forte.simbot.annotations.InternalSimbotAPI",
            "love.forte.simbot.resource.ExperimentalIOResourceAPI",
        ),
    )

    ignoredClasses.add("love.forte.simbot.suspendrunner.SuspendMarker")
    ignoredClasses.add("love.forte.simbot.suspendrunner.SuspendMarker.Container")
    ignoredClasses.add("love.forte.simbot.suspendrunner.SuspendMarker\$Container")

    apiDumpDirectory = "api"
}

idea {
    module {
        isDownloadSources = true
    }
}

// https://kotlinlang.org/docs/js-project-setup.html#node-js
// rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
//     rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().apply {
//         // CI 中配置环境，不再单独下载
//         // if (isCi) {
//         // download = false
//         // }
//     }
//     // "true" for default behavior
// }

// Changelog

tasks.register<GenerateSubChangelogTask>("createChangelog") {
    tag = "v${P.VERSION}"
    versions.put("Kotlin", libs.versions.kotlin.get())
}

tasks.register<GenerateChangelogTask>("updateChangelog") {
    newestTag = "v${P.VERSION}"
}

// region Suspend Transform configs
object SuspendTransformPlugins {
    // love.forte.simbot.suspendrunner.SuspendTrans
    const val OPT_ANNOTATION_PACKAGE: String = "love.forte.simbot.annotations"
    const val SUSPEND_RUNNER_PACKAGE: String = "love.forte.simbot.suspendrunner"

    const val API4J_NAME: String = "Api4J"
    const val API4JS_NAME: String = "Api4Js"

    const val RUN_IN_BLOCKING_FUN_NAME: String = "$\$runInBlocking"
    const val RUN_IN_ASYNC_FUN_NAME: String = "$\$runInAsyncNullable"
    const val AS_RESERVE_FUN_NAME: String = "$\$asReserve"

    const val SUSPEND_TRANS_ANNO_NAME: String = "SuspendTrans"
    const val SUSPEND_TRANS_PROPERTY_ANNO_NAME: String = "SuspendTransProperty"

    fun SuspendTransformPluginExtension.addSimbotJvmTransforms() {
        transformers {
            fun TransformerSpec.includeApi4J(clear: Boolean = true) {
                if (clear) {
                    syntheticFunctionIncludeAnnotations.clear()
                }
                addSyntheticFunctionIncludeAnnotation {
                    classInfo {
                        packageName.set(OPT_ANNOTATION_PACKAGE)
                        className.set(API4J_NAME)
                    }
                    includeProperty.set(true)
                }
            }

            fun TransformerSpec.blockingFunction() {
                transformFunctionInfo {
                    packageName.set(SUSPEND_RUNNER_PACKAGE)
                    functionName.set(RUN_IN_BLOCKING_FUN_NAME)
                }
            }

            fun TransformerSpec.asyncFunction() {
                transformFunctionInfo {
                    packageName.set(SUSPEND_RUNNER_PACKAGE)
                    functionName.set(RUN_IN_ASYNC_FUN_NAME)
                }
            }

            fun TransformerSpec.reserveFunction() {
                transformFunctionInfo {
                    packageName.set(SUSPEND_RUNNER_PACKAGE)
                    functionName.set(AS_RESERVE_FUN_NAME)
                }
            }

            fun TransformerSpec.suspendTrans(
                baseNameProperty: String,
                suffixProperty: String,
                asPropertyProperty: String,
                defaultSuffix: String,
            ) {
                markAnnotation {
                    classInfo {
                        packageName.set(SUSPEND_RUNNER_PACKAGE)
                        className.set(SUSPEND_TRANS_ANNO_NAME)
                    }

                    this.baseNameProperty.set(baseNameProperty)
                    this.suffixProperty.set(suffixProperty)
                    this.asPropertyProperty.set(asPropertyProperty)
                    this.defaultSuffix.set(defaultSuffix)
                    this.defaultAsProperty.set(false)
                }
            }

            fun TransformerSpec.suspendTransProperty(
                baseNameProperty: String,
                suffixProperty: String,
                asPropertyProperty: String,
                defaultSuffix: String,
            ) {
                markAnnotation {
                    classInfo {
                        packageName.set(SUSPEND_RUNNER_PACKAGE)
                        className.set(SUSPEND_TRANS_PROPERTY_ANNO_NAME)
                    }

                    this.baseNameProperty.set(baseNameProperty)
                    this.suffixProperty.set(suffixProperty)
                    this.asPropertyProperty.set(asPropertyProperty)
                    this.defaultSuffix.set(defaultSuffix)
                    this.defaultAsProperty.set(true)
                }
            }

            fun addJvmSuspendTrans(
                baseNameProperty: String,
                suffixProperty: String,
                asPropertyProperty: String,
                defaultSuffix: String,
                function: TransformerSpec.() -> Unit,
                transformReturnType: (ClassInfoSpec.() -> Unit)?,
                transformReturnTypeGeneric: Boolean,
                copyExcludes: List<ClassInfo>,
            ) {
                addJvm {
                    suspendTrans(
                        baseNameProperty,
                        suffixProperty,
                        asPropertyProperty,
                        defaultSuffix,
                    )

                    transformReturnType?.also { ci ->
                        transformReturnType {
                            ci()
                        }
                    }
                    this.transformReturnTypeGeneric.set(transformReturnTypeGeneric)
                    addOriginFunctionIncludeAnnotation {
                        classInfo {
                            from(jvmSyntheticClassInfo)
                        }
                    }

                    function()
                    includeApi4J()

                    copyAnnotationsToSyntheticFunction.set(true)

                    for (exclude in copyExcludes) {
                        addCopyAnnotationExclude {
                            from(exclude)
                        }
                    }
                }
            }

            fun addJvmSuspendTransProperty(
                baseNameProperty: String,
                suffixProperty: String,
                asPropertyProperty: String,
                defaultSuffix: String,
                function: TransformerSpec.() -> Unit,
                transformReturnType: (ClassInfoSpec.() -> Unit)?,
                transformReturnTypeGeneric: Boolean,
                copyExcludes: List<ClassInfo>,
            ) {
                addJvm {
                    suspendTransProperty(
                        baseNameProperty,
                        suffixProperty,
                        asPropertyProperty,
                        defaultSuffix,
                    )

                    transformReturnType?.also { ci ->
                        transformReturnType {
                            ci()
                        }
                    }
                    this.transformReturnTypeGeneric.set(transformReturnTypeGeneric)
                    addOriginFunctionIncludeAnnotation {
                        classInfo {
                            from(jvmSyntheticClassInfo)
                        }
                    }

                    function()
                    includeApi4J()

                    copyAnnotationsToSyntheticFunction.set(true)
                    copyAnnotationsToSyntheticProperty.set(true)

                    for (exclude in copyExcludes) {
                        addCopyAnnotationExclude {
                            from(exclude)
                        }
                    }
                }
            }

            // @SuspendTrans for blocking
            addJvmSuspendTrans(
                baseNameProperty = "blockingBaseName",
                suffixProperty = "blockingSuffix",
                asPropertyProperty = "blockingAsProperty",
                defaultSuffix = jvmBlockingAnnotationInfo.defaultSuffix,
                function = { blockingFunction() },
                transformReturnType = null,
                transformReturnTypeGeneric = false,
                copyExcludes = jvmBlockingTransformer.copyAnnotationExcludes,
            )

            // @SuspendTrans for Async
            addJvmSuspendTrans(
                baseNameProperty = "asyncBaseName",
                suffixProperty = "asyncSuffix",
                asPropertyProperty = "asyncAsProperty",
                defaultSuffix = jvmAsyncAnnotationInfo.defaultSuffix,
                function = { asyncFunction() },
                transformReturnType = {
                    packageName.set("java.util.concurrent")
                    className.set("CompletableFuture")
                },
                transformReturnTypeGeneric = true,
                copyExcludes = jvmAsyncTransformer.copyAnnotationExcludes,
            )

            // @SuspendTrans for Reserve
            addJvmSuspendTrans(
                baseNameProperty = "reserveBaseName",
                suffixProperty = "reserveSuffix",
                asPropertyProperty = "reserveAsProperty",
                defaultSuffix = "Reserve",
                function = { reserveFunction() },
                transformReturnType = {
                    packageName.set("love.forte.simbot.suspendrunner.reserve")
                    className.set("SuspendReserve")
                },
                transformReturnTypeGeneric = true,
                copyExcludes = jvmAsyncTransformer.copyAnnotationExcludes,
            )

            // @SuspendTrans for blocking
            addJvmSuspendTransProperty(
                baseNameProperty = "blockingBaseName",
                suffixProperty = "blockingSuffix",
                asPropertyProperty = "blockingAsProperty",
                defaultSuffix = "",
                function = { blockingFunction() },
                transformReturnType = null,
                transformReturnTypeGeneric = false,
                copyExcludes = jvmBlockingTransformer.copyAnnotationExcludes,
            )

            // @SuspendTrans for Async
            addJvmSuspendTransProperty(
                baseNameProperty = "asyncBaseName",
                suffixProperty = "asyncSuffix",
                asPropertyProperty = "asyncAsProperty",
                defaultSuffix = jvmAsyncAnnotationInfo.defaultSuffix,
                function = { asyncFunction() },
                transformReturnType = {
                    packageName.set("java.util.concurrent")
                    className.set("CompletableFuture")
                },
                transformReturnTypeGeneric = true,
                copyExcludes = jvmAsyncTransformer.copyAnnotationExcludes,
            )

            // @SuspendTrans for Reserve
            addJvmSuspendTransProperty(
                baseNameProperty = "reserveBaseName",
                suffixProperty = "reserveSuffix",
                asPropertyProperty = "reserveAsProperty",
                defaultSuffix = "Reserve",
                function = { reserveFunction() },
                transformReturnType = {
                    packageName.set("love.forte.simbot.suspendrunner.reserve")
                    className.set("SuspendReserve")
                },
                transformReturnTypeGeneric = true,
                copyExcludes = jvmAsyncTransformer.copyAnnotationExcludes,
            )

            // @JvmAsync
            addJvmAsync {
                includeApi4J()
                asyncFunction()
            }

            // @JvmBlocking
            addJvmBlocking {
                includeApi4J()
                blockingFunction()
            }

        }
    }
}

fun Project.configureSuspendTransform() {
    extensions.configure<SuspendTransformPluginExtension>("suspendTransformPlugin") {
        includeRuntime = false
        includeAnnotation = false
        with(SuspendTransformPlugins) {
            addSimbotJvmTransforms()
        }
        // addJvmTransformers(
        //     // @JvmBlocking
        //     SuspendTransforms.jvmBlockingTransformer,
        //     // @JvmAsync
        //     SuspendTransforms.jvmAsyncTransformer,
        //
        //     // @JvmSuspendTrans
        //     SuspendTransforms.suspendTransTransformerForJvmBlocking,
        //     SuspendTransforms.suspendTransTransformerForJvmAsync,
        //     SuspendTransforms.suspendTransTransformerForJvmReserve,
        //
        //     // @JvmSuspendTransProperty
        //     SuspendTransforms.jvmSuspendTransPropTransformerForBlocking,
        //     SuspendTransforms.jvmSuspendTransPropTransformerForAsync,
        //     SuspendTransforms.jvmSuspendTransPropTransformerForReserve,
        // )

        // addJsTransformers(
        //     SuspendTransforms.suspendTransTransformerForJsPromise,
        // )
    }
}
// endregion

// region Dokka
subprojects {
    afterEvaluate {
        val p = this
        if (plugins.hasPlugin(libs.plugins.dokka.get().pluginId)) {
            dokka {
                configSourceSets(p)
                pluginsConfiguration.html {
                    configHtmlCustoms(p)
                }
            }
            rootProject.dependencies.dokka(p)
        }
    }
}

dokka {
    moduleName = "Simple Robot"

    dokkaPublications.all {
        if (isSimbotLocal()) {
            logger.info("Is 'SIMBOT_LOCAL', offline")
            offlineMode = true
        }
    }

    configSourceSets(project)

    pluginsConfiguration.html {
        configHtmlCustoms(project)
    }
}
// endregion

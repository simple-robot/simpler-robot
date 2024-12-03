/*
 *     Copyright (c) 2024. ForteScarlet.
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

import io.gitlab.arturbosch.detekt.Detekt
import love.forte.plugin.suspendtrans.*
import love.forte.plugin.suspendtrans.gradle.SuspendTransformGradleExtension

plugins {
    idea
    id("simbot.dokka-multi-module")
    id("com.github.gmazzo.buildconfig") version "5.5.1" apply false
    alias(libs.plugins.detekt)
    id("simbot.nexus-publish")
    id("simbot.changelog-generator")
    alias(libs.plugins.suspendTransform) apply false
    // id("love.forte.plugin.suspend-transform") version "2.1.0-0.9.4" apply false


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
        if (plugins.hasPlugin("io.gitlab.arturbosch.detekt")) {
            return@afterEvaluate
        }

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
tasks.withType<Detekt>().configureEach {
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

    this.ignoredProjects.addAll(
        listOf(
            "interface-uml-processor",
            "simbot-test",
            "tests",
            "spring-boot-starter-test",
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

// region Suspend Transform configs
@Suppress("MaxLineLength")
object SuspendTransforms {
    private val javaIncludeAnnotationApi4JClassInfo = ClassInfo("love.forte.simbot.annotations", "Api4J")
    private val javaIncludeAnnotationApi4J = IncludeAnnotation(javaIncludeAnnotationApi4JClassInfo).apply {
        includeProperty = true
    }
    private val javaIncludeAnnotations = listOf(javaIncludeAnnotationApi4J)

    private val jsIncludeAnnotationApi4JsClassInfo = ClassInfo("love.forte.simbot.annotations", "Api4Js")
    private val jsIncludeAnnotationApi4Js = IncludeAnnotation(jsIncludeAnnotationApi4JsClassInfo).apply {
        includeProperty = true
    }
    private val jsIncludeAnnotations = listOf(jsIncludeAnnotationApi4Js)


    private val SuspendReserveClassInfo = ClassInfo(
        packageName = "love.forte.simbot.suspendrunner.reserve",
        className = "SuspendReserve",
    )

    /**
     * JvmBlocking
     */
    val jvmBlockingTransformer = SuspendTransformConfiguration.jvmBlockingTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$runInBlocking"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jvmBlockingTransformer.markAnnotation.classInfo
    )

    /**
     * JvmAsync
     */
    val jvmAsyncTransformer = SuspendTransformConfiguration.jvmAsyncTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$runInAsyncNullable"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jvmAsyncTransformer.markAnnotation.classInfo
    )

    /**
     * JvmReserve
     */
    val jvmReserveTransformer = SuspendTransformConfiguration.jvmAsyncTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$asReserve"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jvmAsyncTransformer.markAnnotation.classInfo,
        transformReturnType = SuspendReserveClassInfo,
        transformReturnTypeGeneric = true,
    )

    /**
     * JsPromise
     */
    val jsPromiseTransformer = SuspendTransformConfiguration.jsPromiseTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$runInPromise"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jsPromiseTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jsPromiseTransformer.markAnnotation.classInfo,
    )

    //region @JvmSuspendTrans
    private val suspendTransMarkAnnotationClassInfo = ClassInfo("love.forte.simbot.suspendrunner", "SuspendTrans")

    private val jvmSuspendTransMarkAnnotationForBlocking = MarkAnnotation(
        suspendTransMarkAnnotationClassInfo,
        baseNameProperty = "blockingBaseName",
        suffixProperty = "blockingSuffix",
        asPropertyProperty = "blockingAsProperty",
        defaultSuffix = SuspendTransformConfiguration.jvmBlockingAnnotationInfo.defaultSuffix,
    )
    private val jvmSuspendTransMarkAnnotationForAsync = MarkAnnotation(
        suspendTransMarkAnnotationClassInfo,
        baseNameProperty = "asyncBaseName",
        suffixProperty = "asyncSuffix",
        asPropertyProperty = "asyncAsProperty",
        defaultSuffix = SuspendTransformConfiguration.jvmAsyncAnnotationInfo.defaultSuffix,
    )
    private val jvmSuspendTransMarkAnnotationForReserve = MarkAnnotation(
        suspendTransMarkAnnotationClassInfo,
        baseNameProperty = "reserveBaseName",
        suffixProperty = "reserveSuffix",
        asPropertyProperty = "reserveAsProperty",
        defaultSuffix = "Reserve",
    )
    private val jsSuspendTransMarkAnnotationForPromise = MarkAnnotation(
        suspendTransMarkAnnotationClassInfo,
        baseNameProperty = "jsPromiseBaseName",
        suffixProperty = "jsPromiseSuffix",
        asPropertyProperty = "jsPromiseAsProperty",
        defaultSuffix = "Async",
    )

    val suspendTransTransformerForJvmBlocking = jvmBlockingTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForBlocking,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes + jvmSuspendTransMarkAnnotationForBlocking.classInfo
    )

    val suspendTransTransformerForJvmAsync = jvmAsyncTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForAsync,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + jvmSuspendTransMarkAnnotationForAsync.classInfo
    )

    val suspendTransTransformerForJvmReserve = jvmReserveTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForReserve,
        copyAnnotationExcludes = jvmReserveTransformer.copyAnnotationExcludes + jvmSuspendTransMarkAnnotationForReserve.classInfo,
    )

    val suspendTransTransformerForJsPromise = jsPromiseTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForReserve,
        copyAnnotationExcludes = jsPromiseTransformer.copyAnnotationExcludes + jsSuspendTransMarkAnnotationForPromise.classInfo,
    )
    //endregion

    //region @JvmSuspendTransProperty
    private val jvmSuspendTransPropMarkAnnotationClassInfo =
        ClassInfo("love.forte.simbot.suspendrunner", "SuspendTransProperty")

    private val jvmSuspendTransPropMarkAnnotationForBlocking = MarkAnnotation(
        jvmSuspendTransPropMarkAnnotationClassInfo,
        baseNameProperty = "blockingBaseName",
        suffixProperty = "blockingSuffix",
        asPropertyProperty = "blockingAsProperty",
        defaultSuffix = "",
        defaultAsProperty = true
    )
    private val jvmSuspendTransPropMarkAnnotationForAsync = MarkAnnotation(
        jvmSuspendTransPropMarkAnnotationClassInfo,
        baseNameProperty = "asyncBaseName",
        suffixProperty = "asyncSuffix",
        asPropertyProperty = "asyncAsProperty",
        defaultSuffix = SuspendTransformConfiguration.jvmAsyncAnnotationInfo.defaultSuffix,
        defaultAsProperty = true
    )
    private val jvmSuspendTransPropMarkAnnotationForReserve = MarkAnnotation(
        jvmSuspendTransPropMarkAnnotationClassInfo,
        baseNameProperty = "reserveBaseName",
        suffixProperty = "reserveSuffix",
        asPropertyProperty = "reserveAsProperty",
        defaultSuffix = "Reserve",
        defaultAsProperty = true
    )

    val jvmSuspendTransPropTransformerForBlocking = jvmBlockingTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForBlocking,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes + jvmSuspendTransPropMarkAnnotationForBlocking.classInfo
    )

    val jvmSuspendTransPropTransformerForAsync = jvmAsyncTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForAsync,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + jvmSuspendTransPropMarkAnnotationForAsync.classInfo
    )

    val jvmSuspendTransPropTransformerForReserve = jvmReserveTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForReserve,
        copyAnnotationExcludes = jvmReserveTransformer.copyAnnotationExcludes + jvmSuspendTransPropMarkAnnotationForReserve.classInfo
    )
    //endregion
}

fun Project.configureSuspendTransform() {
    extensions.configure<SuspendTransformGradleExtension>("suspendTransform") {
        includeRuntime = false
        includeAnnotation = false

        addJvmTransformers(
            // @JvmBlocking
            SuspendTransforms.jvmBlockingTransformer,
            // @JvmAsync
            SuspendTransforms.jvmAsyncTransformer,

            // @JvmSuspendTrans
            SuspendTransforms.suspendTransTransformerForJvmBlocking,
            SuspendTransforms.suspendTransTransformerForJvmAsync,
            SuspendTransforms.suspendTransTransformerForJvmReserve,

            // @JvmSuspendTransProperty
            SuspendTransforms.jvmSuspendTransPropTransformerForBlocking,
            SuspendTransforms.jvmSuspendTransPropTransformerForAsync,
            SuspendTransforms.jvmSuspendTransPropTransformerForReserve,
        )

        // addJsTransformers(
        //     SuspendTransforms.suspendTransTransformerForJsPromise,
        // )
    }
}
// endregion

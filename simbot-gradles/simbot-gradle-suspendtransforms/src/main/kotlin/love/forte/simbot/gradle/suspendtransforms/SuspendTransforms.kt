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

package love.forte.simbot.gradle.suspendtransforms

import love.forte.plugin.suspendtrans.*

/**
 * 添加至 gradle 项目的 `buildSrc` 中，并在需要配置 `suspendTransform` 的地方：
 *
 * ```kotlin
 * suspendTransform {
 *     includeRuntime = false
 *     includeAnnotation = false
 *
 *     addJvmTransformers(
 *         // @JvmBlocking
 *         SuspendTransforms.jvmBlockingTransformer,
 *         // @JvmAsync
 *         SuspendTransforms.jvmAsyncTransformer,
 *
 *         // @JvmSuspendTrans
 *         SuspendTransforms.suspendTransTransformerForJvmBlocking,
 *         SuspendTransforms.suspendTransTransformerForJvmAsync,
 *         SuspendTransforms.suspendTransTransformerForJvmReserve,
 *
 *         // @JvmSuspendTransProperty
 *         SuspendTransforms.jvmSuspendTransPropTransformerForBlocking,
 *         SuspendTransforms.jvmSuspendTransPropTransformerForAsync,
 *         SuspendTransforms.jvmSuspendTransPropTransformerForReserve,
 *     )
 * }
 *
 * ```
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
public object SuspendTransforms {
    private val javaIncludeAnnotationApi4JClassInfo = ClassInfo("love.forte.simbot.annotations", "Api4J")
    private val javaIncludeAnnotationApi4J = IncludeAnnotation(javaIncludeAnnotationApi4JClassInfo)
    private val javaIncludeAnnotations = listOf(javaIncludeAnnotationApi4J)

    private val jsIncludeAnnotationApi4JsClassInfo = ClassInfo("love.forte.simbot.annotations", "Api4Js")
    private val jsIncludeAnnotationApi4Js = IncludeAnnotation(jsIncludeAnnotationApi4JsClassInfo)

    // TODO
    private val jsIncludeAnnotations = listOf(jsIncludeAnnotationApi4Js)


    private val SuspendReserveClassInfo = ClassInfo(
        packageName = "love.forte.simbot.suspendrunner.reserve",
        className = "SuspendReserve",
    )

    /**
     * JvmBlocking
     */
    public val jvmBlockingTransformer: Transformer = SuspendTransformConfiguration.jvmBlockingTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$runInBlocking"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes +
            SuspendTransformConfiguration.jvmBlockingTransformer.markAnnotation.classInfo
    )

    /**
     * JvmAsync
     */
    public val jvmAsyncTransformer: Transformer = SuspendTransformConfiguration.jvmAsyncTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$runInAsyncNullable"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes +
            SuspendTransformConfiguration.jvmAsyncTransformer.markAnnotation.classInfo
    )

    /**
     * JvmReserve
     */
    public val jvmReserveTransformer: Transformer = SuspendTransformConfiguration.jvmAsyncTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$asReserve"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes +
            SuspendTransformConfiguration.jvmAsyncTransformer.markAnnotation.classInfo,
        transformReturnType = SuspendReserveClassInfo,
        transformReturnTypeGeneric = true,
    )

    /**
     * JsPromise
     */
    public val jsPromiseTransformer: Transformer = SuspendTransformConfiguration.jsPromiseTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$runInPromise"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jsPromiseTransformer.copyAnnotationExcludes +
            SuspendTransformConfiguration.jsPromiseTransformer.markAnnotation.classInfo,
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

    public val suspendTransTransformerForJvmBlocking: Transformer = jvmBlockingTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForBlocking,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes +
            jvmSuspendTransMarkAnnotationForBlocking.classInfo
    )

    public val suspendTransTransformerForJvmAsync: Transformer = jvmAsyncTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForAsync,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes +
            jvmSuspendTransMarkAnnotationForAsync.classInfo
    )

    public val suspendTransTransformerForJvmReserve: Transformer = jvmReserveTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForReserve,
        copyAnnotationExcludes = jvmReserveTransformer.copyAnnotationExcludes +
            jvmSuspendTransMarkAnnotationForReserve.classInfo,
    )

    public val suspendTransTransformerForJsPromise: Transformer = jsPromiseTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForReserve,
        copyAnnotationExcludes = jsPromiseTransformer.copyAnnotationExcludes +
            jsSuspendTransMarkAnnotationForPromise.classInfo,
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

    public val jvmSuspendTransPropTransformerForBlocking: Transformer = jvmBlockingTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForBlocking,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes +
            jvmSuspendTransPropMarkAnnotationForBlocking.classInfo
    )

    public val jvmSuspendTransPropTransformerForAsync: Transformer = jvmAsyncTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForAsync,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes +
            jvmSuspendTransPropMarkAnnotationForAsync.classInfo
    )

    public val jvmSuspendTransPropTransformerForReserve: Transformer = jvmReserveTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForReserve,
        copyAnnotationExcludes = jvmReserveTransformer.copyAnnotationExcludes +
            jvmSuspendTransPropMarkAnnotationForReserve.classInfo
    )
    //endregion


}





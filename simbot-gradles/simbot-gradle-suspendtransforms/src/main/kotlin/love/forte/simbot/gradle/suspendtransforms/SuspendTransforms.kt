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

package love.forte.simbot.gradle.suspendtransforms/*
 *     Copyright (c) 2023-2024. ForteScarlet.
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

import love.forte.plugin.suspendtrans.*

/*
 * Copyright (c) 2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

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
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jvmBlockingTransformer.markAnnotation.classInfo
    )

    /**
     * JvmAsync
     */
    public val jvmAsyncTransformer: Transformer = SuspendTransformConfiguration.jvmAsyncTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$runInAsyncNullable"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jvmAsyncTransformer.markAnnotation.classInfo
    )

    /**
     * JvmReserve
     */
    public val jvmReserveTransformer: Transformer = SuspendTransformConfiguration.jvmAsyncTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$asReserve"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jvmAsyncTransformer.markAnnotation.classInfo,
        transformReturnType = SuspendReserveClassInfo,
        transformReturnTypeGeneric = true,
    )

    /**
     * JsPromise
     */
    public val jsPromiseTransformer: Transformer = SuspendTransformConfiguration.jsPromiseTransformer.copy(
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

    public val suspendTransTransformerForJvmBlocking: Transformer = jvmBlockingTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForBlocking,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes + jvmSuspendTransMarkAnnotationForBlocking.classInfo
    )

    public val suspendTransTransformerForJvmAsync: Transformer = jvmAsyncTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForAsync,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + jvmSuspendTransMarkAnnotationForAsync.classInfo
    )

    public val suspendTransTransformerForJvmReserve: Transformer = jvmReserveTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForReserve,
        copyAnnotationExcludes = jvmReserveTransformer.copyAnnotationExcludes + jvmSuspendTransMarkAnnotationForReserve.classInfo,
    )

    public val suspendTransTransformerForJsPromise: Transformer = jsPromiseTransformer.copy(
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

    public val jvmSuspendTransPropTransformerForBlocking: Transformer = jvmBlockingTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForBlocking,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes + jvmSuspendTransPropMarkAnnotationForBlocking.classInfo
    )

    public val jvmSuspendTransPropTransformerForAsync: Transformer = jvmAsyncTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForAsync,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + jvmSuspendTransPropMarkAnnotationForAsync.classInfo
    )

    public val jvmSuspendTransPropTransformerForReserve: Transformer = jvmReserveTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForReserve,
        copyAnnotationExcludes = jvmReserveTransformer.copyAnnotationExcludes + jvmSuspendTransPropMarkAnnotationForReserve.classInfo
    )
    //endregion


}





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


object SuspendTransforms {
    private val includeAnnotationApi4JClassInfo = ClassInfo("love.forte.simbot", "Api4J")
    private val includeAnnotationApi4J = IncludeAnnotation(includeAnnotationApi4JClassInfo)
    private val includeAnnotations = listOf(includeAnnotationApi4J)
    
    /**
     * JvmBlocking
     */
    val jvmBlockingTransformer = SuspendTransformConfiguration.jvmBlockingTransformer.copy(
        syntheticFunctionIncludeAnnotations = includeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.utils", null, "$\$runInBlocking"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jvmBlockingTransformer.markAnnotation.classInfo
    )
    
    /**
     * JvmAsync
     */
    val jvmAsyncTransformer = SuspendTransformConfiguration.jvmAsyncTransformer.copy(
        syntheticFunctionIncludeAnnotations = includeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.utils", null, "$\$runInAsync1"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jvmAsyncTransformer.markAnnotation.classInfo
    )
    
    //region @JvmSuspendTrans
    private val jvmSuspendTransMarkAnnotationClassInfo = ClassInfo("love.forte.simbot", "JvmSuspendTrans")
    
    private val jvmSuspendTransMarkAnnotationForBlocking = MarkAnnotation(
        jvmSuspendTransMarkAnnotationClassInfo,
        baseNameProperty = "blockingBaseName",
        suffixProperty = "blockingSuffix",
        asPropertyProperty = "blockingAsProperty",
        defaultSuffix = SuspendTransformConfiguration.jvmBlockingAnnotationInfo.defaultSuffix,
    )
    private val jvmSuspendTransMarkAnnotationForAsync = MarkAnnotation(
        jvmSuspendTransMarkAnnotationClassInfo,
        baseNameProperty = "asyncBaseName",
        suffixProperty = "asyncSuffix",
        asPropertyProperty = "asyncAsProperty",
        defaultSuffix = SuspendTransformConfiguration.jvmAsyncAnnotationInfo.defaultSuffix,
    )
    
    val jvmSuspendTransTransformerForBlocking = jvmBlockingTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForBlocking,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes + jvmSuspendTransMarkAnnotationForBlocking.classInfo
    )
    
    val jvmSuspendTransTransformerForAsync = jvmAsyncTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForAsync,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + jvmSuspendTransMarkAnnotationForAsync.classInfo
    )
    //endregion
    
    //region @JvmSuspendTransProperty
    private val jvmSuspendTransPropMarkAnnotationClassInfo = ClassInfo("love.forte.simbot", "JvmSuspendTransProperty")
    
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
    
    val jvmSuspendTransPropTransformerForBlocking = jvmBlockingTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForBlocking,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes + jvmSuspendTransPropMarkAnnotationForBlocking.classInfo
    )
    
    val jvmSuspendTransPropTransformerForAsync = jvmAsyncTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForAsync,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + jvmSuspendTransPropMarkAnnotationForAsync.classInfo
    )
    //endregion
    
}





/*
 *     Copyright (c) 2025. ForteScarlet.
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

import love.forte.plugin.suspendtrans.configuration.ClassInfo
import love.forte.plugin.suspendtrans.configuration.SuspendTransformConfigurations.jvmAsyncAnnotationInfo
import love.forte.plugin.suspendtrans.configuration.SuspendTransformConfigurations.jvmAsyncTransformer
import love.forte.plugin.suspendtrans.configuration.SuspendTransformConfigurations.jvmBlockingAnnotationInfo
import love.forte.plugin.suspendtrans.configuration.SuspendTransformConfigurations.jvmBlockingTransformer
import love.forte.plugin.suspendtrans.configuration.SuspendTransformConfigurations.jvmSyntheticClassInfo
import love.forte.plugin.suspendtrans.gradle.ClassInfoSpec
import love.forte.plugin.suspendtrans.gradle.SuspendTransformPluginExtension
import love.forte.plugin.suspendtrans.gradle.TransformerSpec
import love.forte.simbot.gradle.suspendtransforms.SuspendTransformPlugins.SUSPEND_RUNNER_PACKAGE
import love.forte.simbot.gradle.suspendtransforms.SuspendTransformPlugins.SUSPEND_TRANS_ANNO_NAME
import love.forte.simbot.gradle.suspendtransforms.SuspendTransformPlugins.SUSPEND_TRANS_PROPERTY_ANNO_NAME

public object SuspendTransformPlugins {
    public const val OPT_ANNOTATION_PACKAGE: String = "love.forte.simbot.annotations"
    public const val SUSPEND_RUNNER_PACKAGE: String = "love.forte.simbot.suspendrunner"

    public const val API4J_NAME: String = "Api4J"
    public const val API4JS_NAME: String = "Api4Js"

    public const val RUN_IN_BLOCKING_FUN_NAME: String = "$\$runInBlocking"
    public const val RUN_IN_ASYNC_FUN_NAME: String = "$\$runInAsyncNullable"
    public const val AS_RESERVE_FUN_NAME: String = "$\$asReserve"

    public const val SUSPEND_TRANS_ANNO_NAME: String = "SuspendTrans"
    public const val SUSPEND_TRANS_PROPERTY_ANNO_NAME: String = "SuspendTransProperty"

}

public fun SuspendTransformPluginExtension.addSimbotJvmTransforms() {
    transformers { transformersContainer ->
        fun TransformerSpec.includeApi4J(clear: Boolean = true) {
            if (clear) {
                syntheticFunctionIncludeAnnotations.clear()
            }
            addSyntheticFunctionIncludeAnnotation { includeAnnotationSpec ->
                includeAnnotationSpec.classInfo { classInfoSpec ->
                    classInfoSpec.packageName.set(SuspendTransformPlugins.OPT_ANNOTATION_PACKAGE)
                    classInfoSpec.className.set(SuspendTransformPlugins.API4J_NAME)
                }
                includeAnnotationSpec.includeProperty.set(true)
            }
        }

        fun TransformerSpec.blockingFunction() {
            transformFunctionInfo { functionInfoSpec ->
                functionInfoSpec.packageName.set(SUSPEND_RUNNER_PACKAGE)
                functionInfoSpec.functionName.set(SuspendTransformPlugins.RUN_IN_BLOCKING_FUN_NAME)
            }
        }

        fun TransformerSpec.asyncFunction() {
            transformFunctionInfo { functionInfoSpec ->
                functionInfoSpec.packageName.set(SUSPEND_RUNNER_PACKAGE)
                functionInfoSpec.functionName.set(SuspendTransformPlugins.RUN_IN_ASYNC_FUN_NAME)
            }
        }

        fun TransformerSpec.reserveFunction() {
            transformFunctionInfo { functionInfoSpec ->
                functionInfoSpec.packageName.set(SUSPEND_RUNNER_PACKAGE)
                functionInfoSpec.functionName.set(SuspendTransformPlugins.AS_RESERVE_FUN_NAME)
            }
        }

        // @JvmBlocking
        transformersContainer.addJvmBlocking { transformerSpec ->
            transformerSpec.includeApi4J()
            transformerSpec.blockingFunction()
        }
        // @JvmAsync
        transformersContainer.addJvmAsync { transformerSpec ->
            transformerSpec.includeApi4J()
            transformerSpec.asyncFunction()
        }

        fun TransformerSpec.suspendTrans(
            baseNameProperty: String,
            suffixProperty: String,
            asPropertyProperty: String,
            defaultSuffix: String,
        ) {
            markAnnotation { markAnnotationSpec ->
                markAnnotationSpec.classInfo { classInfoSpec ->
                    classInfoSpec.packageName.set(SUSPEND_RUNNER_PACKAGE)
                    classInfoSpec.className.set(SUSPEND_TRANS_ANNO_NAME)
                }

                markAnnotationSpec.baseNameProperty.set(baseNameProperty)
                markAnnotationSpec.suffixProperty.set(suffixProperty)
                markAnnotationSpec.asPropertyProperty.set(asPropertyProperty)
                markAnnotationSpec.defaultSuffix.set(defaultSuffix)
                markAnnotationSpec.defaultAsProperty.set(false)
            }
        }

        fun TransformerSpec.suspendTransProperty(
            baseNameProperty: String,
            suffixProperty: String,
            asPropertyProperty: String,
            defaultSuffix: String,
        ) {
            markAnnotation { markAnnotationSpec ->
                markAnnotationSpec.classInfo { classInfoSpec ->
                    classInfoSpec.packageName.set(SUSPEND_RUNNER_PACKAGE)
                    classInfoSpec.className.set(SUSPEND_TRANS_PROPERTY_ANNO_NAME)
                }

                markAnnotationSpec.baseNameProperty.set(baseNameProperty)
                markAnnotationSpec.suffixProperty.set(suffixProperty)
                markAnnotationSpec.asPropertyProperty.set(asPropertyProperty)
                markAnnotationSpec.defaultSuffix.set(defaultSuffix)
                markAnnotationSpec.defaultAsProperty.set(true)
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
            transformersContainer.addJvm { transformerSpec ->
                transformerSpec.suspendTrans(
                    baseNameProperty,
                    suffixProperty,
                    asPropertyProperty,
                    defaultSuffix,
                )

                transformReturnType?.also { ci ->
                    transformerSpec.transformReturnType { classInfoSpec ->
                        classInfoSpec.ci()
                    }
                }
                transformerSpec.transformReturnTypeGeneric.set(transformReturnTypeGeneric)
                transformerSpec.addOriginFunctionIncludeAnnotation { includeAnnotationSpec ->
                    includeAnnotationSpec.classInfo { classInfoSpec ->
                        classInfoSpec.from(jvmSyntheticClassInfo)
                    }
                }

                transformerSpec.function()
                transformerSpec.includeApi4J()

                transformerSpec.copyAnnotationsToSyntheticFunction.set(true)

                for (exclude in copyExcludes) {
                    transformerSpec.addCopyAnnotationExclude { classInfoSpec ->
                        classInfoSpec.from(exclude)
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
            transformersContainer.addJvm { transformerSpec ->
                transformerSpec.suspendTransProperty(
                    baseNameProperty,
                    suffixProperty,
                    asPropertyProperty,
                    defaultSuffix,
                )

                transformReturnType?.also { ci ->
                    transformerSpec.transformReturnType { classInfoSpec ->
                        classInfoSpec.ci()
                    }
                }
                transformerSpec.transformReturnTypeGeneric.set(transformReturnTypeGeneric)
                transformerSpec.addOriginFunctionIncludeAnnotation { includeAnnotationSpec ->
                    includeAnnotationSpec.classInfo { classInfoSpec ->
                        classInfoSpec.from(jvmSyntheticClassInfo)
                    }
                }

                transformerSpec.function()
                transformerSpec.includeApi4J()

                transformerSpec.copyAnnotationsToSyntheticFunction.set(true)

                for (exclude in copyExcludes) {
                    transformerSpec.addCopyAnnotationExclude { classInfoSpec ->
                        classInfoSpec.from(exclude)
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
            defaultSuffix = jvmBlockingAnnotationInfo.defaultSuffix,
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
    }
}


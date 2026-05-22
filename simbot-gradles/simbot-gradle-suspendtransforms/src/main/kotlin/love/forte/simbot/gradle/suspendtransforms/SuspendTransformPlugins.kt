/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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
import love.forte.simbot.gradle.suspendtransforms.SuspendTransformPlugins.API4J_NAME
import love.forte.simbot.gradle.suspendtransforms.SuspendTransformPlugins.AS_PUBLISHER_FUN_NAME
import love.forte.simbot.gradle.suspendtransforms.SuspendTransformPlugins.AS_RESERVE_FUN_NAME
import love.forte.simbot.gradle.suspendtransforms.SuspendTransformPlugins.OPT_ANNOTATION_PACKAGE
import love.forte.simbot.gradle.suspendtransforms.SuspendTransformPlugins.RUN_IN_ASYNC_FUN_NAME
import love.forte.simbot.gradle.suspendtransforms.SuspendTransformPlugins.RUN_IN_BLOCKING_FUN_NAME
import love.forte.simbot.gradle.suspendtransforms.SuspendTransformPlugins.SUSPEND_RUNNER_PACKAGE
import love.forte.simbot.gradle.suspendtransforms.SuspendTransformPlugins.SUSPEND_TRANS_ANNO_NAME
import love.forte.simbot.gradle.suspendtransforms.SuspendTransformPlugins.SUSPEND_TRANS_PROPERTY_ANNO_NAME

/**
 * @since 4.12.0
 */
public object SuspendTransformPlugins {
    public const val OPT_ANNOTATION_PACKAGE: String = "love.forte.simbot.annotations"
    public const val SUSPEND_RUNNER_PACKAGE: String = "love.forte.simbot.suspendrunner"

    public const val API4J_NAME: String = "Api4J"
    public const val API4JS_NAME: String = "Api4Js"

    public const val JB_BLOCKING_ANNOTATION_PACKAGE: String = "org.jetbrains.annotations"
    public const val JB_BLOCKING_ANNOTATION_NAME: String = "Blocking"

    public const val RUN_IN_BLOCKING_FUN_NAME: String = "$\$runInBlocking"
    public const val RUN_IN_ASYNC_FUN_NAME: String = "$\$runInAsyncNullable"
    public const val AS_RESERVE_FUN_NAME: String = "$\$asReserve"
    public const val AS_PUBLISHER_FUN_NAME: String = "$\$asPublisher"

    public const val SUSPEND_TRANS_ANNO_NAME: String = "SuspendTrans"
    public const val SUSPEND_TRANS_PROPERTY_ANNO_NAME: String = "SuspendTransProperty"

}

/**
 * @since 4.12.0
 */
public fun SuspendTransformPluginExtension.addSimbotJvmTransforms() {
    transformers {
        fun TransformerSpec.excludeTransMarks() {
            addCopyAnnotationExclude {
                packageName.set(SUSPEND_RUNNER_PACKAGE)
                className.set(SUSPEND_TRANS_ANNO_NAME)
            }
            addCopyAnnotationExclude {
                packageName.set(SUSPEND_RUNNER_PACKAGE)
                className.set(SUSPEND_TRANS_PROPERTY_ANNO_NAME)
            }
        }

        fun TransformerSpec.includeApi4J(clear: Boolean = true, blocking: Boolean = false) {
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
            if (blocking) {
                addSyntheticFunctionIncludeAnnotation {
                    classInfo {
                        packageName.set(SuspendTransformPlugins.JB_BLOCKING_ANNOTATION_PACKAGE)
                        className.set(SuspendTransformPlugins.JB_BLOCKING_ANNOTATION_NAME)
                    }
                }
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

        fun TransformerSpec.publisherFunction() {
            transformFunctionInfo {
                packageName.set(SUSPEND_RUNNER_PACKAGE)
                functionName.set(AS_PUBLISHER_FUN_NAME)
            }
        }

        fun TransformerSpec.markSuspendTrans(
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

        fun TransformerSpec.markSuspendTransProperty(
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
            blocking: Boolean = false,
        ) {
            addJvm {
                markSuspendTrans(
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
                includeApi4J(blocking = blocking)

                copyAnnotationsToSyntheticFunction.set(true)

                for (exclude in copyExcludes) {
                    addCopyAnnotationExclude {
                        from(exclude)
                    }
                }
                excludeTransMarks()
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
            blocking: Boolean = false,
        ) {
            addJvm {
                markSuspendTransProperty(
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
                includeApi4J(blocking = blocking)

                copyAnnotationsToSyntheticFunction.set(true)
                copyAnnotationsToSyntheticProperty.set(true)

                for (exclude in copyExcludes) {
                    addCopyAnnotationExclude {
                        from(exclude)
                    }
                }
                excludeTransMarks()
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
            blocking = true,
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

        // @SuspendTrans for Reactive
        addJvmSuspendTrans(
            baseNameProperty = "reactiveBaseName",
            suffixProperty = "reactiveSuffix",
            asPropertyProperty = "reactiveAsProperty",
            defaultSuffix = "Reactive",
            function = { publisherFunction() },
            transformReturnType = {
                packageName.set("org.reactivestreams")
                className.set("Publisher")
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
            blocking = true,
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

        // @SuspendTrans for Reactive
        addJvmSuspendTransProperty(
            baseNameProperty = "reactiveBaseName",
            suffixProperty = "reactiveSuffix",
            asPropertyProperty = "reactiveAsProperty",
            defaultSuffix = "Reactive",
            function = { publisherFunction() },
            transformReturnType = {
                packageName.set("org.reactivestreams")
                className.set("Publisher")
            },
            transformReturnTypeGeneric = true,
            copyExcludes = jvmAsyncTransformer.copyAnnotationExcludes,
        )

        // @JvmAsync
        addJvmAsync {
            includeApi4J()
            excludeTransMarks()
            asyncFunction()
        }

        // @JvmBlocking
        addJvmBlocking {
            includeApi4J(blocking = true)
            excludeTransMarks()
            blockingFunction()
        }

    }
}

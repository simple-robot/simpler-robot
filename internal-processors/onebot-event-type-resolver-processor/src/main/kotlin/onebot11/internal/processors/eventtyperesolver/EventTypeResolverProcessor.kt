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
@file:Suppress("PackageNaming")

package onebot11.internal.processors.eventtyperesolver

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.jvm.jvmMultifileClass
import com.squareup.kotlinpoet.jvm.jvmName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass


/**
 *
 * @author ForteScarlet
 */
class EventTypeResolverProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        EventTypeResolverProcessor(environment)
}

private val KSerializerClassName =
    ClassName("kotlinx.serialization", "KSerializer")

private const val EVENT_BASE_PACKAGE = "love.forte.simbot.component.onebot.v11.event"

private val EventClassName =
    ClassName(EVENT_BASE_PACKAGE, "RawEvent")

private val ExpectEventTypeAnnotationClassName =
    ClassName(EVENT_BASE_PACKAGE, "ExpectEventType")

private val ExpectEventSubTypePropertyAnnotationClassName =
    ClassName(EVENT_BASE_PACKAGE, "ExpectEventSubTypeProperty")

// postType
// subType

private const val FUNCTION_POST_TYPE_PARAM_NAME = "postType"
private const val FUNCTION_SUB_TYPE_PARAM_NAME = "subType"
private const val FUNCTION_EVENT_PARAM_NAME = "event"

private const val ANNOTATION_POST_TYPE_ARG_NAME = "postType"
private const val ANNOTATION_SUB_TYPE_ARG_NAME = "subType"

// ktx 可序列化标记
private const val SERIALIZABLE_ANNOTATION = "kotlinx.serialization.Serializable"

private const val FUNCTION_RESOLVER_SERIALIZER_NAME = "resolveEventSerializer"
private const val FUNCTION_RESOLVER_TYPE_NAME = "resolveEventType"
private const val FUNCTION_RESOLVER_SUB_TYPE_NAME = "resolveEventSubTypeFieldName"

private const val FILE_NAME = "EventResolver.generated"
private const val FILE_JVM_NAME = "EventResolvers"

private val InternalAPIClassName = ClassName(
    "love.forte.simbot.component.onebot.common.annotations",
    "InternalOneBotAPI"
)

private class EventTypeResolverProcessor(val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val generated = AtomicBoolean(false)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (!generated.compareAndSet(false, true)) {
            environment.logger.warn("Processor was used.")
            return emptyList()
        }

        val eventDeclaration = resolver.getClassDeclarationByName(EventClassName.canonicalName)
            ?: throw NoSuchElementException("Class: $EventClassName")

        val expectEventTypeAnnotationDeclaration =
            resolver.getClassDeclarationByName(ExpectEventTypeAnnotationClassName.canonicalName)
                ?: throw NoSuchElementException("Class: $ExpectEventTypeAnnotationClassName")

        // 标记在大分类类型上
        val expectEventSubTypePropertyAnnotationDeclaration =
            resolver.getClassDeclarationByName(ExpectEventSubTypePropertyAnnotationClassName.canonicalName)
                ?: throw NoSuchElementException("Class: $ExpectEventSubTypePropertyAnnotationClassName")

        val eventType = eventDeclaration.asStarProjectedType()
        val expectEventTypeAnnotationType = expectEventTypeAnnotationDeclaration.asStarProjectedType()

        val allEventImplWithAnnotation = resolver.getSymbolsWithAnnotation(SERIALIZABLE_ANNOTATION)
            .filterIsInstance<KSClassDeclaration>()
            // 是一个可序列化的具体的类
            .filter { !it.isAbstract() }
            // 是 event 的子类
            .filter { eventType.isAssignableFrom(it.asStarProjectedType()) }
            // 有注解 ExpectType
            .filter {
                it.annotations.any { a -> expectEventTypeAnnotationType.isAssignableFrom(a.annotationType.resolve()) }
            }
            .onEach {
                environment.logger.info("Resolved message element impl: $it", it)
            }
            .toList()

        val rootSubEventTypes =
            resolver.getSymbolsWithAnnotation(ExpectEventSubTypePropertyAnnotationClassName.canonicalName)
                .filterIsInstance<KSClassDeclaration>()
                // 是 event 的子类
                .filter { eventType.isAssignableFrom(it.asStarProjectedType()) }
                .onEach {
                    environment.logger.info("Resolved root sub event type: $it", it)
                }
                .toList()

        val values = resolveTypes(expectEventTypeAnnotationType, allEventImplWithAnnotation)

        val resolveSerializerFunction = resolveSerializerFunction(values)
        val resolveTypeFunction = resolveTypeFunction(values)

        val resolveRootEventFunction =
            resolveRootEventFunction(expectEventSubTypePropertyAnnotationDeclaration, rootSubEventTypes)

        val resolveSubTypeFieldNameFunction =
            resolveSubTypeFieldNameFunction(expectEventSubTypePropertyAnnotationDeclaration, rootSubEventTypes)

        val generatedFile = FileSpec.builder(EVENT_BASE_PACKAGE, FILE_NAME).apply {
            addFileComment(
                """
                ****************************
                文件内容自动生成自 ${nowString()}
                ****************************
                """.trimIndent()
            )
            addAnnotation(
                AnnotationSpec.builder(Suppress::class)
                    .addMember("%S, %S", "ALL", "unused")
                    .build()
            )

            jvmName(FILE_JVM_NAME)
            jvmMultifileClass()

            addFunction(resolveSerializerFunction)
            addFunction(resolveTypeFunction)
            addFunction(resolveRootEventFunction)
            addFunction(resolveSubTypeFieldNameFunction)

            indent("    ")
        }.build()

        generatedFile.writeTo(
            codeGenerator = environment.codeGenerator,
            aggregating = true,
            originatingKSFiles = buildList {
                eventDeclaration.containingFile?.also(::add)
                for (declaration in allEventImplWithAnnotation) {
                    declaration.containingFile?.also(::add)
                }
                for (rootSubEventType in rootSubEventTypes) {
                    rootSubEventType.containingFile?.also(::add)
                }
            }
        )

        return emptyList()
    }

    /**
     * 生成:
     * ```kotlin
     * fun resolveEventSerializer(event: Event): KSerializer<out Event>? =
     * when (event) {
     *     is MessageEvent -> resolveEventSerializer(event.postType, event.messageType)
     *     is ... -> resolveEventSerializer(event.postType, ...)
     *     else -> null
     * }
     * ```
     *
     */
    private fun resolveRootEventFunction(
        annotationDeclaration: KSClassDeclaration,
        rootSubEventTypes: List<KSClassDeclaration>
    ): FunSpec {
        return FunSpec.builder(FUNCTION_RESOLVER_SERIALIZER_NAME).apply {
            addAnnotation(InternalAPIClassName)
            addParameter(FUNCTION_EVENT_PARAM_NAME, EventClassName)
            returns(
                KSerializerClassName.parameterizedBy(
                    // out Event
                    WildcardTypeName.producerOf(EventClassName)
                ).copy(nullable = true)
            )

            addKdoc(
                "根据事件 [%L] 的类型分析得到对应的序列化器，如果找不到则得到 `null`。\n",
                FUNCTION_EVENT_PARAM_NAME
            )
            addKdoc("目前支持获取如下事件的子类型的序列化器：\n")

            addCode(
                buildCodeBlock {
                    add("return ")
                    beginControlFlow("when(%L)", FUNCTION_EVENT_PARAM_NAME)

                    for (rootSubEventType in rootSubEventTypes) {
                        val annotation = rootSubEventType.annotations
                            .find {
                                annotationDeclaration
                                    .asStarProjectedType()
                                    .isAssignableFrom(it.annotationType.resolve())
                            }
                            ?: continue

                        // The subtype property name
                        val value =
                            annotation.arguments.find { it.name?.asString() == "value" }?.value as? String? ?: continue

                        // %L.%L
                        addStatement(
                            "is %T -> %L(%L.postType, %L.%L)",
                            rootSubEventType.toClassName(),
                            FUNCTION_RESOLVER_SERIALIZER_NAME,
                            FUNCTION_EVENT_PARAM_NAME,
                            FUNCTION_EVENT_PARAM_NAME,
                            value
                        )

                        addKdoc("- [%T] \n", rootSubEventType.toClassName())
                    }

                    addStatement("else -> null")
                    endControlFlow()
                }
            )
        }.build()
    }


    private fun resolveTypes(
        eventType: KSType,
        allEventImplWithAnnotation: List<KSClassDeclaration>
    ): Map<String, Map<String, KSClassDeclaration>> {
        // Map<String, Map<String, ClassDeclaration>>
        val result = mutableMapOf<String, MutableMap<String, KSClassDeclaration>>()

        for (declaration in allEventImplWithAnnotation) {
            val expectTypeAnnotation =
                declaration.annotations.find { eventType.isAssignableFrom(it.annotationType.resolve()) } ?: continue

            val postType =
                expectTypeAnnotation.arguments
                    .find {
                        it.name?.asString() == ANNOTATION_POST_TYPE_ARG_NAME
                    }?.value as String?
            val subType =
                expectTypeAnnotation.arguments
                    .find {
                        it.name?.asString() == ANNOTATION_SUB_TYPE_ARG_NAME
                    }?.value as String?

            if (postType == null) {
                environment.logger.warn(
                    "Cannot get `postType` from the @ExpectEventType annotation on $declaration",
                    declaration
                )
                continue
            }
            if (subType == null) {
                environment.logger.warn(
                    "Cannot get `subType` from the @ExpectEventType annotation on $declaration",
                    declaration
                )
                continue
            }


            result.computeIfAbsent(postType) { mutableMapOf() }[subType] = declaration
        }

        return result
    }


    private inline fun resolveValueStatements(
        values: Map<String, Map<String, KSClassDeclaration>>,
        beginPostType: (postType: String, subMap: Map<String, KSClassDeclaration>) -> Unit,
        onStatement: (postType: String, subType: String, decl: KSClassDeclaration) -> Unit,
        endPostType: (postType: String, subMap: Map<String, KSClassDeclaration>) -> Unit,
    ) {
        for ((postType, subMap) in values) {
            beginPostType(postType, subMap)
            for ((subType, declaration) in subMap) {
                onStatement(postType, subType, declaration)
            }
            endPostType(postType, subMap)
        }
    }

    private inline fun CodeBlock.Builder.resolveCodeBlockWhenStatements(
        values: Map<String, Map<String, KSClassDeclaration>>,
        postTypeValName: String = "postType",
        subTypeValName: String = "subType",
        onStatement: (postType: String, subType: String, decl: KSClassDeclaration) -> Unit,
        onPostTypeElse: () -> Unit,
        onSubTypeElse: () -> Unit,
    ) {
        beginControlFlow("when(%L)", postTypeValName)
        resolveValueStatements(
            values,
            beginPostType = { postType, _ ->
                add("%S -> ", postType)
                beginControlFlow("when(%L)", subTypeValName)
            },
            onStatement = { postType, subType, declaration ->
                add("%S -> ", subType)
                onStatement(postType, subType, declaration)
            },
            endPostType = { _, _ ->
                onSubTypeElse()
                endControlFlow()
            },
        )
        onPostTypeElse()
        endControlFlow()
    }


    private fun FunSpec.Builder.resolveKDoc(
        values: Map<String, Map<String, KSClassDeclaration>>
    ) {
        addKdoc("根据事件的主类型 `postType` 和二级子类型获取到一个唯一的事件序列化器，如果找不到则得到 `null`。\n")
        resolveValueStatements(
            values,
            beginPostType = { postType, subMap ->
                addKdoc("- `%S` (total: %L) \n", postType, subMap.size)
            },
            onStatement = { _, subType, declaration ->
                addKdoc("    - `%S` -> [%T]\n", subType, declaration.toClassName())
            },
            endPostType = { _, _ ->
            }
        )
    }

    private fun resolveSerializerFunction(
        values: Map<String, Map<String, KSClassDeclaration>>
    ): FunSpec {
        return FunSpec.builder(FUNCTION_RESOLVER_SERIALIZER_NAME).apply {
            addAnnotation(InternalAPIClassName)
            addParameter(FUNCTION_POST_TYPE_PARAM_NAME, STRING)
            addParameter(FUNCTION_SUB_TYPE_PARAM_NAME, STRING)
            returns(
                KSerializerClassName.parameterizedBy(
                    // out Event
                    WildcardTypeName.producerOf(EventClassName)
                ).copy(nullable = true)
            )

            resolveKDoc(values)

            addCode(
                buildCodeBlock {
                    add("return ")
                    resolveCodeBlockWhenStatements(
                        values,
                        postTypeValName = FUNCTION_POST_TYPE_PARAM_NAME,
                        subTypeValName = FUNCTION_SUB_TYPE_PARAM_NAME,
                        onStatement = { _, _, declaration ->
                            addStatement("%T.serializer()", declaration.toClassName())
                        },
                        onPostTypeElse = { addStatement("else -> null") },
                        onSubTypeElse = { addStatement("else -> null") },
                    )
                }
            )
        }.build()
    }

    private fun resolveTypeFunction(
        values: Map<String, Map<String, KSClassDeclaration>>
    ): FunSpec {
        return FunSpec.builder(FUNCTION_RESOLVER_TYPE_NAME).apply {
            addAnnotation(InternalAPIClassName)
            addParameter(FUNCTION_POST_TYPE_PARAM_NAME, STRING)
            addParameter(FUNCTION_SUB_TYPE_PARAM_NAME, STRING)
            returns(
                KClass::class.asTypeName().parameterizedBy(
                    // out Event
                    WildcardTypeName.producerOf(EventClassName)
                ).copy(nullable = true)
            )

            resolveKDoc(values)

            addCode(
                buildCodeBlock {
                    add("return ")
                    resolveCodeBlockWhenStatements(
                        values,
                        postTypeValName = FUNCTION_POST_TYPE_PARAM_NAME,
                        subTypeValName = FUNCTION_SUB_TYPE_PARAM_NAME,
                        onStatement = { _, _, declaration ->
                            addStatement("%T::class", declaration.toClassName())
                        },
                        onPostTypeElse = { addStatement("else -> null") },
                        onSubTypeElse = { addStatement("else -> null") },
                    )
                }
            )
        }.build()
    }

    private fun resolveSubTypeFieldNameFunction(
        annotationDeclaration: KSClassDeclaration,
        rootSubEventTypes: List<KSClassDeclaration>
    ): FunSpec {
        return FunSpec.builder(FUNCTION_RESOLVER_SUB_TYPE_NAME).apply {
            addAnnotation(InternalAPIClassName)
            addParameter(FUNCTION_POST_TYPE_PARAM_NAME, STRING)

            returns(STRING.copy(nullable = true))

            addCode(
                buildCodeBlock {
                    addCode("return ")
                    beginControlFlow("when (%L)", FUNCTION_POST_TYPE_PARAM_NAME)

                    for (rootSubEventType in rootSubEventTypes) {
                        val annotation = rootSubEventType.annotations
                            .find {
                                annotationDeclaration
                                    .asStarProjectedType()
                                    .isAssignableFrom(it.annotationType.resolve())
                            }
                            ?: continue

                        val postTypeValue =
                            annotation.arguments.find { it.name?.asString() == "postType" }?.value as? String?
                                ?: continue

                        val propertyName =
                            annotation.arguments.find { it.name?.asString() == "name" }?.value as? String?
                                ?: continue

                        addStatement("%S -> %S", postTypeValue, propertyName)

                    }

                    addStatement("else -> null")

                    endControlFlow()
                }
            )


        }.build()
    }

}

private val formatter = DateTimeFormatter
    .ofLocalizedDateTime(
        FormatStyle.FULL,
        FormatStyle.FULL
    )
    .localizedBy(Locale.CHINA)

private fun nowString(): String {
    val now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"))
    return formatter.format(now)
}

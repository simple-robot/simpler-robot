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

package kook.internal.processors.msgelement

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import java.time.Instant
import java.time.ZoneOffset


private const val BASE_ELEMENT_CLASS_PACKAGE = "love.forte.simbot.component.kook.message"
private const val BASE_ELEMENT_CLASS_SIMPLE_NAME = "KookMessageElement"

private const val BASE_ELEMENT_CLASS_NAME =
    "$BASE_ELEMENT_CLASS_PACKAGE.$BASE_ELEMENT_CLASS_SIMPLE_NAME"

private val BaseElementClassName =
    ClassName(BASE_ELEMENT_CLASS_PACKAGE, BASE_ELEMENT_CLASS_SIMPLE_NAME)

private const val KTX_SERIALIZABLE_ANNOTATION_PACKAGE = "kotlinx.serialization"
private const val KTX_SERIALIZABLE_ANNOTATION_SIMPLE_NAME = "Serializable"
private const val KTX_SERIALIZABLE_ANNOTATION_NAME =
    "$KTX_SERIALIZABLE_ANNOTATION_PACKAGE.$KTX_SERIALIZABLE_ANNOTATION_SIMPLE_NAME"

private val PolymorphicModuleBuilderClassName =
    ClassName("kotlinx.serialization.modules", "PolymorphicModuleBuilder")

private const val OUTPUT_PACKAGE = "love.forte.simbot.component.kook.message"
private const val OUTPUT_FILE = "IncludeKookMessageElements.generated"
private const val OUTPUT_FUN_NAME = "includeKookMessageElements"

/**
 *
 * @author ForteScarlet
 */
class MessageElementProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val codeGenerator = environment.codeGenerator
    private val elementDeclarations = mutableListOf<KSClassDeclaration>()

    override fun finish() {
        val newFun = resolve(elementDeclarations)

        val fileSpec = FileSpec.Companion.builder(
            OUTPUT_PACKAGE, OUTPUT_FILE
        )
            .addFunction(newFun)
            .addFileComment("\nAuto-Generated at ${Instant.now().atOffset(ZoneOffset.ofHours(8))}\n")
            .build()

        fileSpec.writeTo(
            codeGenerator = codeGenerator,
            aggregating = true,
            originatingKSFiles = buildList {
                for (impl in elementDeclarations) {
                    impl.containingFile?.also { add(it) }
                }
            }
        )
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val baseClass = resolver.getClassDeclarationByName(
            BASE_ELEMENT_CLASS_NAME
        ) ?: error("Base class $BASE_ELEMENT_CLASS_NAME not found")

        val baseClassStarType = baseClass.asStarProjectedType()

        resolver
            .getSymbolsWithAnnotation(KTX_SERIALIZABLE_ANNOTATION_NAME)
            .filterIsInstance<KSClassDeclaration>()
            .filter { declaration ->
                // 是 base class 的子类
                baseClassStarType.isAssignableFrom(declaration.asStarProjectedType())
            }
            .filter { declaration ->
                // 不是抽象的、不是sealed的可序列化类
                !declaration.isAbstract() && !declaration.modifiers.contains(Modifier.SEALED)
            }
            .toCollection(elementDeclarations)

        return emptyList()
    }

    /**
     * 生成函数：
     *
     * ```kotlin
     * internal fun PolymorphicModuleBuilder<KookMessageElement>.includeKookMessageElements() {
     *      subclass(KookAsset::class, KookAsset.serializer())
     *      // ...
     * }
     * ```
     */
    private fun resolve(declarations: List<KSClassDeclaration>): FunSpec {
        val receiverType = PolymorphicModuleBuilderClassName.parameterizedBy(BaseElementClassName)
//        val optAnnotation =
        val optAnnotation = AnnotationSpec.builder(ClassName("kotlin", "OptIn"))
            .addMember(
                "%T::class, %T::class",
                ClassName("love.forte.simbot.annotations", "ExperimentalSimbotAPI"),
                ClassName("love.forte.simbot.kook", "ExperimentalKookApi")
            )
            .build()

        val internalAPIAnnotation = ClassName("love.forte.simbot.kook", "InternalKookApi")

        return FunSpec.builder(OUTPUT_FUN_NAME).apply {
            modifiers.add(KModifier.INTERNAL)
            receiver(receiverType)
            addAnnotation(optAnnotation)
            addAnnotation(internalAPIAnnotation)

            for (declaration in declarations) {
                val className = declaration.toClassName()
                addCode("subclass(%T::class, %T.serializer())\n", className, className)
            }
        }.build()
    }
}


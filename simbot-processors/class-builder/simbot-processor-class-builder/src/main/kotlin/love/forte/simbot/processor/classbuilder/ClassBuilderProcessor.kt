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

package love.forte.simbot.processor.classbuilder

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import love.forte.simbot.processor.classbuilder.annotation.BuilderFor
import love.forte.simbot.processor.classbuilder.annotation.ClassBuilder
import kotlin.reflect.KClass


public data class ExistBuilder(
    val annotation: KSAnnotation,
    val target: KClass<*>,
    val declaration: KSClassDeclaration
)

public data class ExpectBuilderDeclaration(
    val annotationData: AnnotationData,
    val type: KSClassDeclaration
) {
    val builderName: String
        get() = annotationData.name.ifBlank { type.simpleName.asString() + "Builder" }

    public data class AnnotationData(
        val source: KSAnnotation,
        val name: String,
        val marks: List<KSType>
    )
}

/**
 *
 * @author ForteScarlet
 */
public class ClassBuilderProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    public companion object {
        public val BuilderForAnnotationName: String = BuilderFor::class.qualifiedName!!
        public val ClassBuilderAnnotationName: String = ClassBuilder::class.qualifiedName!!
    }

    // 没法直接扫描 libs 中的类，只有当遇到类型的时候检测一下注解，然后再猜测一下名称。
    // private val existClassBuilders = mutableMapOf<KClass<*>, ExistBuilder>()
    private val classBuilders = mutableMapOf<KSClassDeclaration, BuilderGenerator>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // find all @ClassBuilder
        resolveAllExpectClassBuilders(resolver)

        return emptyList()
    }

    /**
     * 扫描找到所有的 [ClassBuilder] 注解的类。
     */
    private fun resolveAllExpectClassBuilders(resolver: Resolver) {
        resolver.getSymbolsWithAnnotation(ClassBuilderAnnotationName)
            .filterIsInstance<KSClassDeclaration>()
            .onEach {
                if (!(it.classKind == ClassKind.CLASS && !it.isAbstract())) {
                    environment.reportError(
                        "ClassBuilder annotation can only be used on non-abstract classes",
                        it
                    )
                }
            }
            .mapNotNull {
                val annotation = it.annotations.find {
                    (it.annotationType.resolve().declaration as? KSClassDeclaration)
                        ?.qualifiedName?.asString() == ClassBuilderAnnotationName
                } ?: return@mapNotNull null

                val name = annotation.arguments.find { it.name?.asString() == "name" }?.value as? String

                @Suppress("UNCHECKED_CAST")
                val marks =
                    // KClass<out Annotation>
                    annotation.arguments.find { it.name?.asString() == "marks" }?.value as? List<KSType>

                ExpectBuilderDeclaration(
                    ExpectBuilderDeclaration.AnnotationData(annotation, name ?: "", marks ?: emptyList()),
                    it
                )
            }
            .forEach { expect ->
                classBuilders[expect.type] = BuilderGenerator(
                    resolver,
                    environment,
                    expect
                )
            }
    }

    override fun finish() {
        // 生成 Builders
        data class FileWithSource(val file: FileSpec, val sources: List<KSFile>)

        classBuilders.map { (_, generator) ->
            environment.logger.info("Expect builder: ${generator.declaration.type}")

            val file = generator.generate()

            FileWithSource(file, generator.sources.toList())
        }.forEach { (file, sources) ->

            file.writeTo(
                environment.codeGenerator,
                true,
                sources
            )
        }


    }
}

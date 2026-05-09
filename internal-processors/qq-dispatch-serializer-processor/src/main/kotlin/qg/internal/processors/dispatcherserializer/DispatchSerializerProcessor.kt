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

package qg.internal.processors.dispatcherserializer

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import java.time.Instant
import java.time.ZoneOffset

private const val SIGNAL_DISPATCH_PKG = "love.forte.simbot.qguild.event"
private const val SIGNAL_DISPATCH_SIMPLE_NAME = "Signal.Dispatch"
private const val SIGNAL_DISPATCH_NAME = "$SIGNAL_DISPATCH_PKG.$SIGNAL_DISPATCH_SIMPLE_NAME"

/**
 *
 * @author ForteScarlet
 */
internal class DispatchSerializerProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {
    private data class SerializableDispatch(
        val declaration: KSClassDeclaration,
        val typeName: String
    )

    // private lateinit var signalDispatchDeclaration: KSClassDeclaration
    private lateinit var signalDispatchDeclarationClassName: ClassName
    private var signalDispatchDeclarationOgFile: KSFile? = null

    private val dispatchSerializableDeclarations = sortedSetOf<SerializableDispatch>(
        Comparator.comparing { it.typeName }
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val signalDispatchDeclaration = resolver.getClassDeclarationByName(SIGNAL_DISPATCH_NAME)
            ?: error("Cannot find class declaration $SIGNAL_DISPATCH_NAME")

        signalDispatchDeclarationOgFile = signalDispatchDeclaration.containingFile
        signalDispatchDeclarationClassName = signalDispatchDeclaration.asStarProjectedType().toClassName()

        // find all subtypes,
        resolver.getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                signalDispatchDeclaration.asStarProjectedType().isAssignableFrom(it.asStarProjectedType())
            }
            // not abstract
            .filter {
                !it.isAbstract() && !it.modifiers.contains(Modifier.SEALED)
            }
            // marked @kotlinx.serialization.Serializable
            .filter {
                it.annotations.any { anno ->
                    with(anno.annotationType.resolve().declaration) {
                        packageName.asString() == "kotlinx.serialization" &&
                            simpleName.asString() == "Serializable"
                    }
                }
            }
            .mapNotNull {
                // find @love.forte.simbot.qguild.event.DispatchTypeName
                val typeNameAnnotation = it.annotations.find { anno ->
                    with(anno.annotationType.resolve().declaration) {
                        packageName.asString() == "love.forte.simbot.qguild.event" &&
                            simpleName.asString() == "DispatchTypeName"
                    }
                } ?: return@mapNotNull null

                val argument = typeNameAnnotation.arguments.find { a -> a.name?.asString() == "value" }
                    ?: return@mapNotNull null

                val typeName = argument.value as String

                SerializableDispatch(it, typeName)
            }
            .toCollection(dispatchSerializableDeclarations)

        return emptyList()
    }

    override fun finish() {
        genResolver()
    }

    private fun genResolver() {
        val orgFiles = mutableListOf<KSFile>()
        signalDispatchDeclarationOgFile?.also(orgFiles::add)

        val fileSpecBuilder = FileSpec.builder("love.forte.simbot.qguild.event", "SignalDispatchResolvers.generated")

        val funBuilder = FunSpec.builder("resolveDispatchSerializer").apply {
            addAnnotation(ClassName("love.forte.simbot.qguild", "Generated"))
            addParameter("eventName", String::class)
            returns(
                ClassName("kotlinx.serialization", "KSerializer")
                    .parameterizedBy(
                        WildcardTypeName
                            .producerOf(
                                ClassName(SIGNAL_DISPATCH_PKG, SIGNAL_DISPATCH_SIMPLE_NAME)
                            )
                    )
                    .copy(nullable = true)
            )

            addCode(
                CodeBlock.builder().apply {
                    beginControlFlow("return when(eventName)")
                    // "" -> Type.serializer()
                    for (dispatchSerializableDeclaration in dispatchSerializableDeclarations) {
                        dispatchSerializableDeclaration.declaration.containingFile?.also(orgFiles::add)

                        addStatement(
                            "%S -> %T.serializer()",
                            dispatchSerializableDeclaration.typeName,
                            dispatchSerializableDeclaration.declaration.toClassName()
                        )
                    }
                    addStatement("else -> null")
                    endControlFlow()
                }.build()
            )

            addKdoc(
                CodeBlock.builder().apply {
                    addStatement("| event name | target |")
                    addStatement("| --- | --- |")
                    for (dispatchSerializableDeclaration in dispatchSerializableDeclarations) {
                        addStatement(
                            "| `%S` | [%T] |",
                            dispatchSerializableDeclaration.typeName,
                            dispatchSerializableDeclaration.declaration.toClassName()
                        )
                    }
                    addStatement("")
                    addStatement("@since 4.1.0")
                }.build()
            )
        }

        fileSpecBuilder.addFileComment("Auto generated at ${Instant.now().atOffset(ZoneOffset.ofHours(8))}")
        fileSpecBuilder.addFunction(funBuilder.build())

        fileSpecBuilder.build().writeTo(
            environment.codeGenerator,
            aggregating = true,
            originatingKSFiles = orgFiles
        )
    }
}

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

package love.forte.simbot.common.annodatapropgen

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.findActualType
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.writeTo
import love.forte.simbot.common.annodatapropgen.annotations.GenDataClass
import love.forte.simbot.common.annodatapropgen.annotations.GenDataClassFrom

private sealed class GenData {
    abstract val source: KSClassDeclaration
}

private data class Gen(val anno: GenDataClass, override val source: KSClassDeclaration) : GenData()
private data class GenFrom(val anno: GenDataClassFrom, override val source: KSClassDeclaration) : GenData()
private data class Generated(
    val source: KSClassDeclaration,
    val type: TypeSpec,

    )

private data class FileKey(val packageName: String, val fileName: String)
private data class FileInfo(val builder: FileSpec.Builder, val sources: MutableSet<KSFile> = mutableSetOf())

private val GenDataClass.realTargetName: String?
    get() = targetName.takeIf { it.isNotBlank() }

/**
 *
 * @author ForteScarlet
 */
class AnnotationDataPropertiesGeneratorProcessor(private val environment: SymbolProcessorEnvironment) :
    SymbolProcessor {
    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbolsWithGen = resolver.getSymbolsWithAnnotation(GenDataClass::class.java.name)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.ANNOTATION_CLASS }
            .mapNotNull {
                val anno = it.getAnnotationsByType(GenDataClass::class).firstOrNull() ?: return@mapNotNull null
                Gen(anno, it)
            }

        val symbolsWithGenFrom = resolver.getSymbolsWithAnnotation(GenDataClassFrom::class.java.name)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.ANNOTATION_CLASS }
            .flatMap { c ->
                c.getAnnotationsByType(GenDataClassFrom::class).map {
                    GenFrom(it, c)
                }
            }

        val merge = (symbolsWithGen + symbolsWithGenFrom)
            // Only top level declarations
            .filter { it.source.parentDeclaration == null }

        val gens: List<GenData> = merge.toList()
        val types = mutableListOf<TypeSpec>()
        val files = mutableMapOf<FileKey, FileInfo>()
        val generatedMap = mutableMapOf<KSClassDeclaration, TypeSpec>()

        TypeSpec.classBuilder("").build().toBuilder()

        gens.forEach {
            when (it) {
                is Gen -> it.generateType(null, gens, resolver, generatedMap, files)
                is GenFrom -> it.generateType(resolver, generatedMap)
            }
        }

        files.values.forEach { (fb, sources) ->
            environment.logger.info("WRITE FB: fb=$fb (${fb.name})")
            fb.build().writeTo(
                codeGenerator = environment.codeGenerator,
                dependencies = Dependencies(
                    aggregating = true,
                    sources = sources.toTypedArray(),
                )
            )
        }

        // TODO

        return emptyList()
    }

    @OptIn(KspExperimental::class)
    private fun Gen.generateType(
        parent: TypeSpec.Builder?,
        gens: List<GenData>,
        resolver: Resolver,
        generateMap: MutableMap<KSClassDeclaration, TypeSpec>,
        files: MutableMap<FileKey, FileInfo>
    ) {
        val type = TypeSpec.classBuilder(
            name = anno.realTargetName
                ?: if (parent == null) "${source.simpleName.asString()}Properties" else source.simpleName.asString(),
        ).apply p@{
            addModifiers(KModifier.DATA)
            when {
                Modifier.INTERNAL in source.modifiers -> {
                    addModifiers(KModifier.INTERNAL)
                }

                Modifier.PRIVATE in source.modifiers -> {
                    addModifiers(KModifier.PRIVATE)
                }

                Modifier.PUBLIC in source.modifiers -> {
                    addModifiers(KModifier.PUBLIC)
                }
            }

            // sub annotation types
            source.declarations.filterIsInstance<KSClassDeclaration>()
                .filter { it.classKind == ClassKind.ANNOTATION_CLASS }
                .mapNotNull { sub ->
                    val genDataClassAnno =
                        sub.getAnnotationsByType(GenDataClass::class).firstOrNull() ?: return@mapNotNull null
                    Gen(genDataClassAnno, sub)
                }.forEach {
                    environment.logger.info("SUB: $it")
                    it.generateType(this@p, gens, resolver, generateMap, files)
                }

            primaryConstructor(FunSpec.constructorBuilder().apply {
                this@generateType.source.getDeclaredProperties().forEach { prop ->
                    val name = prop.simpleName.asString()
                    val type = prop.type.resolvePropType(gens, generateMap)
                    val parameter = ParameterSpec.builder(name, type).build()
                    val property = PropertySpec.builder(name, type).apply {
                        mutable(anno.propertiesMutable)
                        initializer(parameter.name)
                    }.build()

                    addProperty(property)
                    addParameter(parameter)
                }


            }.build())

        }.build()

        generateMap[source] = type

        if (parent != null) {
            parent.addType(type)
        } else {
            val (fb, sources) = files.computeIfAbsent(
                FileKey(
                    source.packageName.asString(),
                    "AnnoData.Generated"
                )
            ) { (packageName, fileName) ->
                FileInfo(
                    FileSpec.builder(packageName, fileName).apply {
                        addType(type)
                    }
                )
            }

            environment.logger.info("RESOLVE FILE: gen=$this")
            environment.logger.info("RESOLVE FILE: fb=$fb (${fb.name}), type=$type")
            fb.addType(type)
            source.containingFile?.also { sources.add(it) }
        }

    }

    private fun GenFrom.generateType(resolver: Resolver, generateMap: MutableMap<KSClassDeclaration, TypeSpec>) {

    }

    private fun genFunction() {

    }


    private fun KSTypeReference.resolvePropType(
        gens: List<GenData>,
        generateMap: MutableMap<KSClassDeclaration, TypeSpec>
    ): TypeName {
        val resolved = resolve()
        val resolveClassDeclaration = resolved.resolveClassDeclaration()!!
        for (typeParameter in resolveClassDeclaration.typeParameters) {

        }

        ParameterizedTypeName

        val typeName = resolve().toTypeName()

        if (typeName !is ParameterizedTypeName) return typeName

        typeName.typeArguments.forEach {  }

        return typeName
    }
}

private fun ParameterizedTypeName.resolveType(
    gens: List<GenData>,
    generateMap: MutableMap<KSClassDeclaration, TypeSpec>
): ParameterizedTypeName {
    TODO()
}

internal fun KSType.resolveClassDeclaration(): KSClassDeclaration? {
    return when (val declaration = declaration) {
        is KSClassDeclaration -> declaration
        is KSTypeAlias -> declaration.findActualType()
        else -> return null
    }
}

internal fun KSType.resolveClassDeclarationToClassName(): ClassName? {
    return resolveClassDeclaration()?.toClassName()
}

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

package love.forte.simbot.processor.message.element.polymorphic.include

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.isLocal
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.jvm.jvmMultifileClass
import com.squareup.kotlinpoet.jvm.jvmName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

private val PolymorphicModuleBuilderClassName =
    ClassName("kotlinx.serialization.modules", "PolymorphicModuleBuilder")

/**
 *
 * @author ForteScarlet
 */
public open class MessageElementPolymorphicIncludeProcessor(
    private val environment: SymbolProcessorEnvironment,
    private val configuration: MessageElementPolymorphicIncludeConfiguration
) : SymbolProcessor {

    protected open lateinit var baseKSClassDeclaration: KSClassDeclaration
    protected open val targetClasses: MutableList<KSClassDeclaration> = mutableListOf()
    protected open val isLocalOnly: Boolean = configuration.localOnly
    protected open val generateFunName: String = configuration.generateFunName
    protected open val outputPackage: String? = configuration.outputPackage
    protected open val outputFileName: String = configuration.outputFileName
    protected open val outputFileJvmName: String? = configuration.outputFileJvmName?.takeIf { it.isNotEmpty() }
    protected open val outputFileJvmMultifile: Boolean = configuration.outputFileJvmMultifile
    protected open val visibility: KModifier = configuration.visibilityValue()

    protected open inner class Visitor : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            // check is local and is serializable class
            if (isLocalOnly && !classDeclaration.isLocal()) {
                return super.visitClassDeclaration(classDeclaration, data)
            }

            // not abstract, not sealed
            if (classDeclaration.isAbstract() || classDeclaration.modifiers.contains(Modifier.SEALED)) {
                return super.visitClassDeclaration(classDeclaration, data)
            }

            // is implements base class
            val baseType = baseKSClassDeclaration.asStarProjectedType()
            if (!baseType.isAssignableFrom(classDeclaration.asStarProjectedType())) {
                return super.visitClassDeclaration(classDeclaration, data)
            }

            targetClasses.add(classDeclaration)
        }
    }

    protected open val visitor: KSVisitor<Unit, Unit> = Visitor()

    override fun finish() {
        write(createFile())
    }

    protected open fun createFile(): FileSpec =
        createFileBuilder().apply {
            useInFile()
        }.build()

    protected open fun createFileBuilder(): FileSpec.Builder {
        return FileSpec.builder(
            outputPackage ?: baseKSClassDeclaration.packageName.asString(),
            outputFileName
        ).apply {
            outputFileJvmName?.also { jvmName(it) }
            if (outputFileJvmMultifile) {
                jvmMultifileClass()
            }
        }
    }

    protected open fun FileSpec.Builder.useInFile() {
        val funSpec = FunSpec.builder(generateFunName).apply {
            receiver(PolymorphicModuleBuilderClassName.parameterizedBy(baseKSClassDeclaration.toClassName()))
            modifiers.add(visibility)

            val memberName = MemberName("kotlinx.serialization.modules", "subclass")
            for (targetClass in targetClasses) {
                addCode("%M(%T.serializer())\n", memberName, targetClass.toClassName())
            }
        }.build()

        addFunction(funSpec)
    }

    protected open fun write(fileSpec: FileSpec) {
        fileSpec.writeTo(
            codeGenerator = environment.codeGenerator,
            aggregating = true,
            originatingKSFiles = buildList {
                for (impl in targetClasses) {
                    baseKSClassDeclaration.containingFile?.also { add(it) }
                    impl.containingFile?.also { add(it) }
                }
            }
        )
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // find base class
        val baseClassName = configuration.baseClass
        resolver.getClassDeclarationByName(baseClassName)
            ?.also { baseKSClassDeclaration = it }
            ?: error("Cannot find base class $baseClassName")

        resolver.getSymbolsWithAnnotation("kotlinx.serialization.Serializable")
            .filterIsInstance<KSClassDeclaration>()
            .forEach { visitor.visitClassDeclaration(it, Unit) }

        return emptyList()
    }
}

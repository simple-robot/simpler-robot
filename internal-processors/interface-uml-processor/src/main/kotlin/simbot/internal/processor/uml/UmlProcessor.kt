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

package simbot.internal.processor.uml

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ksp.toClassName
import java.io.Writer
import java.nio.file.StandardOpenOption
import kotlin.io.path.*

private const val TARGET_CLASS_OPTION = "simbot.internal.processor.uml.target"
private const val OUTPUT_OPTION = "simbot.internal.processor.uml.output"

class UmlProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val output = environment.options[OUTPUT_OPTION] ?: throw NullPointerException("Output option is null")

        val targetClassName = environment.options[TARGET_CLASS_OPTION]
            ?: throw NullPointerException("Target class: $TARGET_CLASS_OPTION")
        val targetClass = resolver.getClassDeclarationByName(targetClassName)
            ?: run {
                environment.logger.warn("Target class $targetClassName not found")
                return emptyList()
            }
        environment.logger.info("Target class $targetClass")

        val subtypes = resolver.getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter { targetClass.asStarProjectedType().isAssignableFrom(it.asStarProjectedType()) }
            .filter { it.getVisibility() == Visibility.PUBLIC || it.getVisibility() == Visibility.PROTECTED }
            .toList()

        subtypes.forEach {
            environment.logger.info("Subclass ${it.toClassName()}: $it")
        }

        val outputPath = Path(output)
        if (outputPath.notExists()) {
            outputPath.createParentDirectories()
        } else {
            outputPath.deleteExisting()
        }

        outputPath.bufferedWriter(
            options = arrayOf(
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
            )
        ).use { writer ->
            writer.write(targetClass.asStarProjectedType(), subtypes)
        }

        return emptyList()
    }
}

private const val IMPL_TO = " -[#000082,plain]-^ "

private fun Writer.write(targetType: KSType, types: List<KSClassDeclaration>) {
    write(
        """
        ```plantuml
        @startuml
        
        """.trimIndent()
    )

    write("\n")
    write("\n")

    types.forEach { type ->
        if (type.classKind == ClassKind.INTERFACE) {
            write("interface ")
        } else {
            write("class ")
        }
        write(type.simpleName.asString())
        val superTypes = type.superTypes.filter { targetType.isAssignableFrom(it.resolve()) }
            .toList()

        if (superTypes.isNotEmpty()) {
            write(" extends ")
            superTypes.joinTo(this, ", ") { it.resolve().toClassName().simpleNames.joinToString(".") }
        }

        write("\n")
    }

    write("\n")

    write(
        """
        @enduml
        ```
        """.trimIndent()
    )
}

/*
```plantuml
@startuml

!theme plain
top to bottom direction
skinparam linetype ortho

class A
class B
class SimbotDemo3Application

B -[#000082,plain]-^ A
@enduml
```
 */

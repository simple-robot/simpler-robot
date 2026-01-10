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

package kook.internal.processors.apireader

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.toClassName
import java.io.BufferedWriter
import java.io.File
import java.nio.file.StandardOpenOption
import kotlin.io.path.bufferedWriter

private const val KOOK_API_CLASS_NAME = "love.forte.simbot.kook.api.KookApi"

private const val API_READ_TARGET_FILE_OPTION_KEY = "kook.api.finder.api.output"

abstract class ReaderProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    abstract val optionName: String
    abstract val targetClassName: String

    private var targetFile: File? = null
    private val targetClasses = mutableListOf<KSClassDeclaration>()

    override fun finish() {
        val targetFile = targetFile ?: return
        if (!targetFile.exists()) {
            targetFile.parentFile.mkdirs()
        } else {
            targetFile.delete()
        }

        targetFile.toPath().bufferedWriter(
            options = arrayOf(
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE
            )
        ).use { writer ->
            writer.writeDeflistTo(targetClasses)
        }
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val targetFilePath: String? = environment.options[optionName]

        val targetFile = File(
            targetFilePath ?: run {
                val msg = "target output file option ['$optionName'] is null!"
                environment.logger.warn(msg)
                return emptyList()
            }
        )

        this.targetFile = targetFile

        environment.logger.info("target output file: ${targetFile.absolutePath}")
        val targetClass = resolver.getClassDeclarationByName(targetClassName)
        environment.logger.info("apiClass: $targetClassName found $targetClass", targetClass)
        targetClass ?: return emptyList()

        // find all
        resolver.getAllFiles().flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                targetClass.asStarProjectedType().isAssignableFrom(it.asStarProjectedType())
            }
            .filter { !it.isAbstract() }
            .toCollection(targetClasses)

        return emptyList()
    }

}

/**
 *
 * @author ForteScarlet
 */
class ApiReaderProcessor(environment: SymbolProcessorEnvironment) : ReaderProcessor(environment) {
    override val optionName: String = API_READ_TARGET_FILE_OPTION_KEY
    override val targetClassName: String = KOOK_API_CLASS_NAME
}

private fun BufferedWriter.writeDeflistTo(list: List<KSClassDeclaration>) {
    write("<deflist>\n")
    list.forEach { declaration ->
        val className = declaration.toClassName().canonicalName
        val idName = className.replace('.', '_')
        write("<def title=\"${declaration.simpleName.asString()}\" id=\"$idName\">\n")
        newLine()
        write("`$className`\n")
        newLine()

        val lines = declaration.docString?.trim()
            ?.lines()
            ?.map { it.trim() }
            ?.filter { !it.startsWith('@') }
            ?.map { line ->
                line
                    .replace(linkRegex, "<a ignore-vars=\\\"true\\\" href=\\\"$2\\\">$1</a>")
                    .replace(refRegex, " `$1` ")
                    .replace(titleRegex, "\n**$1**\n")
            }

        if (lines != null) {
            lines.forEach { line ->
                write(line)
                newLine()
            }
            newLine()
        }

        newLine()

        val sealedSub = declaration.getSealedSubclasses().toList()
        if (sealedSub.isNotEmpty()) {
            writeDeflistTo(sealedSub)
        }

        write("</def>\n")
    }
    newLine()
    write("</deflist>\n")

}

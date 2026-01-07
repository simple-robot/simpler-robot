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
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Visibility
import com.squareup.kotlinpoet.ksp.toClassName
import java.io.BufferedWriter
import java.io.File
import java.nio.file.StandardOpenOption
import kotlin.io.path.bufferedWriter

private const val KOOK_EVENT_CLASS_NAME = "love.forte.simbot.kook.event.EventExtra"
private const val EVENT_READ_TARGET_CLASS_OPTION_KEY = "kook.api.finder.event.class"
private const val EVENT_READ_TARGET_FILE_OPTION_KEY = "kook.api.finder.event.output"


/**
 *
 * @author ForteScarlet
 */
class EventReaderProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val EXPECT_VISIBILITY = setOf(Visibility.PUBLIC, Visibility.PROTECTED)

    private val targetClassName = environment.options[EVENT_READ_TARGET_CLASS_OPTION_KEY]
        ?: KOOK_EVENT_CLASS_NAME

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
        val targetFilePath: String? = environment.options[EVENT_READ_TARGET_FILE_OPTION_KEY]

        val targetFile = File(targetFilePath ?: run {
            val msg = "target output file option ['$EVENT_READ_TARGET_FILE_OPTION_KEY'] is null!"
            environment.logger.warn(msg)
            return emptyList()
        })
        this.targetFile = targetFile

        environment.logger.info("Target class name: $targetClassName")
        environment.logger.info("Target output file: ${targetFile.absolutePath}")
        val targetClass = resolver.getClassDeclarationByName(targetClassName)
        environment.logger.info("apiClass: $targetClass found at $targetClass", targetClass)
        targetClass ?: return emptyList()

        resolver.getAllFiles().flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                targetClass.asStarProjectedType().isAssignableFrom(it.asStarProjectedType())
            }
//            .filter { !it.isAbstract() }
            .filter { it.getVisibility() in EXPECT_VISIBILITY }
            .toCollection(targetClasses)

        return emptyList()
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
            val serName = declaration.annotations.find {
                it.shortName.asString() == "SerialName"
            }?.arguments?.find { it.name?.asString() == "value" }?.value?.toString()

            if (serName != null) {
                write("事件类型名: `\"${serName}\"`\n")
                newLine()
            }

            val lines = declaration.docString?.trim()
                ?.lines()
                ?.filter { it.isNotBlank() }
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

            write("</def>\n")
        }
        newLine()
        write("</deflist>\n")

    }
}


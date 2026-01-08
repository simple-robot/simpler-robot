/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package qg.internal.processors.apireader

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getKotlinClassByName
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

private const val QG_API_CLASS_NAME = "love.forte.simbot.qguild.api.QQGuildApi"

private const val API_READ_TARGET_FILE_OPTION_KEY = "qg.api.finder.api.output"

abstract class ReaderProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    abstract val optionName: String
    abstract val targetClassName: String

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val targetFilePath: String? = environment.options[optionName]

        val targetFile = File(targetFilePath ?: run {
            val msg = "target output file option ['$optionName'] is null!"
            environment.logger.error(msg)
            throw NullPointerException(msg)
        })

        environment.logger.info("target output file: ${targetFile.absolutePath}")
        val targetClass = resolver.getKotlinClassByName(targetClassName)
        environment.logger.info("apiClass: $targetClassName")
        targetClass ?: return emptyList()

        val targetClasses = resolver.getAllFiles().flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                targetClass.asStarProjectedType().isAssignableFrom(it.asStarProjectedType())
            }
            .filter { !it.isAbstract() }
            .toList()

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


        return emptyList()
    }

}

/**
 *
 * @author ForteScarlet
 */
class ApiReaderProcessor(private val environment: SymbolProcessorEnvironment) : ReaderProcessor(environment) {
    override val optionName: String = API_READ_TARGET_FILE_OPTION_KEY
    override val targetClassName: String = QG_API_CLASS_NAME
}

//class EventReaderProcessor(private val environment: SymbolProcessorEnvironment) : ReaderProcessor(environment) {
//    override val optionName: String = EVENT_READ_TARGET_FILE_OPTION_KEY
//    override val targetClassName: String = QG_EVENT_CLASS_NAME
//}

private fun BufferedWriter.writeDeflistTo(list: List<KSClassDeclaration>) {
    write("<deflist>\n")
    list.forEach { declaration ->
        val className = declaration.toClassName().canonicalName
        val idName = className.replace('.', '_')
        write("<def title=\"${declaration.simpleName.asString()}\" id=\"$idName\">\n")
        newLine()
        write("`${declaration.packageName.asString()}.${declaration.simpleName.asString()}`\n")
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

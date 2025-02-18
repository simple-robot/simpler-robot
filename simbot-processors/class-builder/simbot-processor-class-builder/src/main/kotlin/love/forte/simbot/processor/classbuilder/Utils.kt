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

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.KSBuiltIns
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.CodeBlock
import love.forte.simbot.processor.classbuilder.annotation.BuilderFor
import love.forte.simbot.processor.classbuilder.annotation.ClassBuilder

internal val BuilderForAnnotationName: String = BuilderFor::class.qualifiedName!!
internal val ClassBuilderAnnotationName: String = ClassBuilder::class.qualifiedName!!

internal fun KSAnnotation.isClassBuilder(): Boolean {
    return (annotationType.resolve().declaration as? KSClassDeclaration)
        ?.qualifiedName?.asString() == ClassBuilderAnnotationName
}

internal fun KSAnnotation.isBuilderFor(): Boolean {
    return (annotationType.resolve().declaration as? KSClassDeclaration)
        ?.qualifiedName?.asString() == BuilderForAnnotationName
}

internal data class ClassBuilderAnnotationInfo(
    val source: KSAnnotation,
    val name: String,
    val marks: List<KSType>,
    val open: Boolean,
    val internal: Boolean,
)

internal fun KSAnnotation.toClassBuilderAnnotationInfo(): ClassBuilderAnnotationInfo? {
    val name = arguments.find { it.name?.asString() == "name" }?.value as? String

    @Suppress("UNCHECKED_CAST")
    val marks =
        // KClass<out Annotation>
        arguments.find { it.name?.asString() == "marks" }?.value as? List<KSType>

    @Suppress("NullableBooleanElvis")
    val open = arguments.find { it.name?.asString() == "open" }?.value as? Boolean ?: false

    @Suppress("NullableBooleanElvis")
    val internal = arguments.find { it.name?.asString() == "internal" }?.value as? Boolean ?: false

    return ClassBuilderAnnotationInfo(
        source = this,
        name = name ?: "",
        marks = marks ?: emptyList(),
        open = open,
        internal = internal
    )
}


internal inline fun SymbolProcessorEnvironment.reportError(
    msg: String,
    symbol: KSNode? = null,
    reporter: (String) -> Nothing = { error(it) }
): Nothing {
    logger.error(msg, symbol)
    reporter(msg)
}

internal fun KSDeclaration.isPrimitive(builtIns: KSBuiltIns): Boolean {
    return this == builtIns.intType.declaration ||
        this == builtIns.longType.declaration ||
        this == builtIns.shortType.declaration ||
        this == builtIns.byteType.declaration ||
        this == builtIns.charType.declaration ||
        this == builtIns.floatType.declaration ||
        this == builtIns.doubleType.declaration ||
        this == builtIns.booleanType.declaration
}

internal fun KSType.isIterable(resolver: Resolver): Boolean {
    return resolver.builtIns.iterableType.isAssignableFrom(this.makeNotNullable())
}

internal fun KSType.isList(resolver: Resolver, mutable: Boolean = false): Boolean {
    val notNullThis = makeNotNullable()

    val listType = if (mutable) {
        resolver.getClassDeclarationByName<MutableList<*>>()
    } else {
        resolver.getClassDeclarationByName<List<*>>()
    }?.asStarProjectedType() ?: return false

    return listType.isAssignableFrom(notNullThis)
}

internal fun KSType.isSet(resolver: Resolver, mutable: Boolean = false): Boolean {
    val notNullThis = makeNotNullable()

    val setType = if (mutable) {
        resolver.getClassDeclarationByName<MutableSet<*>>()
    } else {
        resolver.getClassDeclarationByName<Set<*>>()
    }?.asStarProjectedType() ?: return false

    return setType.isAssignableFrom(notNullThis)
}

internal fun KSType.isCollection(resolver: Resolver, mutable: Boolean = false): Boolean {
    val notNullThis = makeNotNullable()

    val collectionType = if (mutable) {
        resolver.getClassDeclarationByName<MutableCollection<*>>()
    } else {
        resolver.getClassDeclarationByName<Collection<*>>()
    }?.asStarProjectedType() ?: return false

    return collectionType.isAssignableFrom(notNullThis)
}

internal fun KSType.isMap(resolver: Resolver, mutable: Boolean = false): Boolean {
    val notNullThis = makeNotNullable()

    val collectionType = if (mutable) {
        resolver.getClassDeclarationByName<MutableMap<*, *>>()
    } else {
        resolver.getClassDeclarationByName<Map<*, *>>()
    }?.asStarProjectedType() ?: return false

    return collectionType.isAssignableFrom(notNullThis)
}

internal fun KSType.isArray(resolver: Resolver): Boolean {
    val notNullThis = makeNotNullable()
    return resolver.builtIns.arrayType.isAssignableFrom(notNullThis)
}

internal inline fun CodeBlock.Builder.inReturnApplyBlock(block: CodeBlock.Builder.() -> Unit = {}): CodeBlock.Builder {
    beginControlFlow("return apply")
    block()
    endControlFlow()
    return this
}

internal inline fun CodeBlock.Builder.inStatement(block: CodeBlock.Builder.() -> Unit = {}): CodeBlock.Builder {
    add("«")
    block()
    add("\n»")
    return this
}

internal inline fun CodeBlock.Builder.inReturnApplyStatement(
    block: CodeBlock.Builder.() -> Unit = {}
): CodeBlock.Builder {
    return inReturnApplyBlock {
        inStatement {
            block()
        }
    }
}

internal inline fun CodeBlock.Builder.inControlFlow(
    controlFlow: String,
    vararg args: kotlin.Any?,
    block: CodeBlock.Builder.() -> Unit = {}
): CodeBlock.Builder {
    beginControlFlow(controlFlow, *args)
    block()
    endControlFlow()
    return this
}

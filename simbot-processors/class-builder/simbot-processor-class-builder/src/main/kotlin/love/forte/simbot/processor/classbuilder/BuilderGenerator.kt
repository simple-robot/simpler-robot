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
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*
import java.util.Locale
import kotlin.collections.ArrayDeque
import kotlin.properties.Delegates

internal class BuilderGenerator(
    val resolver: Resolver,
    val environment: SymbolProcessorEnvironment,
    val declaration: ExpectBuilderDeclaration,
    /**
     * 原类型对应生成的Builder的类型的映射。
     */
    val builders: MutableMap<ClassName, TypeName>
) {

    val sources = mutableSetOf<KSFile>()

    lateinit var typeVariables: List<TypeVariableName>
    lateinit var parameters: List<BuilderProperty>
    lateinit var properties: List<BuilderProperty>
    lateinit var typeSpecBuilder: TypeSpec.Builder
    lateinit var fileSpecBuilder: FileSpec.Builder

    fun prepare() {
        val type = declaration.type
        type.containingFile?.also { sources.add(it) }

        val typeParameterResolver = type.typeParameters.toTypeParameterResolver()

        typeVariables = type.typeParameters.map { it.toTypeVariableName(typeParameterResolver) }

        val builderClassName = ClassName(type.packageName.asString(), declaration.builderName)

        var builderTypeName: TypeName = builderClassName

        if (typeVariables.isNotEmpty()) {
            builderTypeName = builderClassName.parameterizedBy(typeVariables)
        }

        builders[type.toClassName()] = builderTypeName

        typeSpecBuilder = TypeSpec.classBuilder(builderClassName)

        if (declaration.annotationData.open) {
            typeSpecBuilder.addModifiers(OPEN)
        }

        if (declaration.annotationData.internal) {
            typeSpecBuilder.addModifiers(INTERNAL)
        }

        // types
        typeSpecBuilder.addTypeVariables(typeVariables)

        // 添加注解
        typeSpecBuilder.addAnnotation(
            AnnotationSpec.builder(BuilderForClassName)
                .addMember("%T::class", type.toClassName())
                .build()
        )

        // marks
        declaration.annotationData.marks.forEach {
            typeSpecBuilder.addAnnotation(it.toClassName())
        }

        fileSpecBuilder = FileSpec.builder(type.packageName.asString(), declaration.builderName)
        // 添加 Suppress("ALL", "unused", "RedundantVisibilityModifier")
        fileSpecBuilder.addAnnotation(
            AnnotationSpec.builder(Suppress::class)
                .addMember("%S, %S, %S, %S", "ALL", "unused", "UNCHECKED_CAST", "RedundantVisibilityModifier")
                .build()
        )

        val parameterPropertyNames = mutableSetOf<String>()

        // 找到用于构建的构建函数，转化它的参数
        parameters =
            (type.primaryConstructor ?: type.getConstructors().firstOrNull())
                ?.parameters
                ?.map { parameter ->

                    val name = parameter.name?.asString() ?: environment.reportError(
                        "Parameter name is null: $parameter",
                        parameter
                    )

                    // collect parameterPropertyNames
                    if (parameter.isVal || parameter.isVar) {
                        parameterPropertyNames.add(name)
                    }

                    BuilderProperty(
                        resolver = resolver,
                        environment = environment,
                        declaration = declaration,
                        fileBuilder = fileSpecBuilder,
                        typeBuilder = typeSpecBuilder,
                        typeParameterResolver = typeParameterResolver,
                        generator = this,
                        builderTypeName = builderTypeName,
                        typeParameters = typeVariables,
                        builderNames = builders,
                        isVararg = parameter.isVararg,
                        isRequired = true,
                        name = name,
                        type = parameter.type.resolve(),
                        parameter
                    )
                }
                ?: emptyList()

        properties = type.getAllProperties()
            .filter { property ->
                // 如果在 properties 里，此处跳过
                // 普通属性则必须为可变属性
                property.simpleName.asString() !in parameterPropertyNames && property.isMutable
            }
            .filter { property ->
                // 属性对于builder来讲必须可见
                // property.isVisibleFrom(type)
                // TODO isVisibleFrom?
                property.getVisibility().let {
                    it == Visibility.PUBLIC || it == Visibility.INTERNAL
                }
            }
            .map { property ->

                val name = property.simpleName.asString()

                BuilderProperty(
                    resolver = resolver,
                    environment = environment,
                    declaration = declaration,
                    fileBuilder = fileSpecBuilder,
                    typeBuilder = typeSpecBuilder,
                    typeParameterResolver = typeParameterResolver,
                    generator = this,
                    builderTypeName = builderTypeName,
                    typeParameters = typeVariables,
                    builderNames = builders,
                    isVararg = false,
                    isRequired = false,
                    name = name,
                    type = property.type.resolve(),
                    property
                )
            }
            .toList()
    }

    fun generate(): FileSpec {
        for (parameterProperty in parameters) {
            parameterProperty.emit()
        }

        for (memberProperty in properties) {
            memberProperty.emit()
        }

        typeSpecBuilder.emitBuild(typeVariables)

        fileSpecBuilder.addType(typeSpecBuilder.build())
        return fileSpecBuilder.build()
    }

    private fun TypeSpec.Builder.emitBuild(typeVariables: List<TypeVariableName>) {
        addFunction(
            FunSpec.builder("build").apply {
                if (declaration.annotationData.open) {
                    addModifiers(OPEN)
                }
                if (typeVariables.isEmpty()) {
                    returns(declaration.type.toClassName())
                } else {
                    returns(declaration.type.toClassName().parameterizedBy(typeVariables))
                }
                addCode(
                    buildCodeBlock {
                        // return T(a = a, b = b, ...).also { it.c = c; it.d = d; }
                        add("return %T(", declaration.type.toClassName())
                        for ((index, parameter) in this@BuilderGenerator.parameters.withIndex()) {
                            val accessName = if (parameter.isNullableOrIsOptional) {
                                "${parameter.name}?"
                            } else {
                                parameter.name
                            }

                            val passing = parameter.passing?.let { p ->
                                buildCodeBlock {
                                    add(accessName)
                                    add(p)
                                }
                            } ?: CodeBlock.of(parameter.name)

                            if (index == 0) {
                                add("${parameter.name} = ")
                                add(passing)
                            } else {
                                add(", ${parameter.name} = ")
                                add(passing)
                            }
                        }
                        add(")")
                        inControlFlow(".also") {
                            for ((index, property) in this@BuilderGenerator.properties.withIndex()) {
                                // this.xxx?.also { value -> it.xxx = value }
                                val name = property.name
                                val localName = "v$index"
                                val accessName = if (property.isNullableOrIsOptional) "$name?" else name
                                val passing: CodeBlock = property.passing?.let { p ->
                                    buildCodeBlock {
                                        add("(")
                                        add(accessName)
                                        add(p)
                                        add(")")
                                    }
                                } ?: CodeBlock.of(name)

                                inStatement {
                                    add(passing)
                                    add("?.also { $localName -> it.$name = $localName")
                                    add(" }")
                                }
                            }
                        }
                    }
                )
            }.build()
        )
    }
}

/**
 * 一个在原类型中的构造函数中的参数，或者一个可变属性。
 *
 * - 如果参数nullable -> 属性可null，默认为 `null`
 * - 如果参数notnull -> 属性lateinit or delegate
 *
 * 具名参数的是否默认不能被动态使用。
 * 参考 [KT-18695](https://youtrack.jetbrains.com/issue/KT-18695)
 *
 * 这是一个可变类，不能重复使用。
 */
internal class BuilderProperty(
    val resolver: Resolver,
    val environment: SymbolProcessorEnvironment,
    val declaration: ExpectBuilderDeclaration,
    val fileBuilder: FileSpec.Builder,
    val typeBuilder: TypeSpec.Builder,
    val typeParameterResolver: TypeParameterResolver,
    val generator: BuilderGenerator,
    val builderTypeName: TypeName,
    val typeParameters: List<TypeVariableName>,
    val builderNames: Map<ClassName, TypeName>,
    val isVararg: Boolean,
    val isRequired: Boolean,
    val name: String,
    val type: KSType,
    val node: KSNode?
) {
    companion object {
        val DelegatesClassName = Delegates::class.asClassName()
    }

    val firstUpperName: String = name.replaceFirstChar { it.uppercase(Locale.US) }

    val isNullable = type.isMarkedNullable
    val isOptional get() = !isRequired
    val isNullableOrIsOptional get() = isNullable || isOptional

    val typeOrNullable: KSType
        get() = if (isNullableOrIsOptional) {
            type.makeNullable()
        } else {
            type
        }

    // 只要 name.xxx 后面的内容，从 . 开始，例如 `.toList`。
    var passing: CodeBlock? = null

    @Suppress("ReturnCount")
    fun findBuilder(type: KSType): TypeName? {
        val classDeclaration = type.declaration as? KSClassDeclaration ?: return null
        val exists = builderNames[classDeclaration.toClassName()]
        if (exists != null) {
            return exists
        }

        // check annotation
        val classBuilderAnnotation = type.declaration.annotations
            .filter { it.isClassBuilder() }
            .mapNotNull {
                it.toClassBuilderAnnotationInfo()
            }
            .firstOrNull()

        // guess builder name
        val classType = type.declaration as? KSClassDeclaration
        if (classBuilderAnnotation == null || classType == null) {
            return null
        }

        val guessedBuilderName = ExpectBuilderDeclaration(classBuilderAnnotation, classType)
            .builderName

        val builderClassDeclaration = resolver.getClassDeclarationByName(
            buildString {
                val pn = classType.packageName.asString()
                if (pn.isNotEmpty()) {
                    append(pn)
                    append('.')
                }
                append(guessedBuilderName)
            }
        ) ?: return null

        // check has BuilderFor

        val validated = builderClassDeclaration.annotations.any {
            it.isBuilderFor() &&
                // BuilderFor的目标也同样是原本的 type
                when (val firstArg = it.arguments.firstOrNull()) {
                    is KSType -> firstArg.makeNotNullable().isAssignableFrom(type.makeNotNullable())
                    is KSValueArgument ->
                        (firstArg.value as? KSType)
                            ?.makeNotNullable()
                            ?.isAssignableFrom(type.makeNotNullable()) == true

                    else -> false
                }
        }

        if (!validated) return null

        val className = builderClassDeclaration.toClassName()

        val typeParameters = builderClassDeclaration.typeParameters.map {
            it.toTypeVariableName(typeParameterResolver)
        }

        var returnTypeName: TypeName = className
        if (typeParameters.isNotEmpty()) {
            returnTypeName = className.parameterizedBy(typeParameters)
        }

        return returnTypeName
    }

    fun emit() {
        val queue = ArrayDeque<Emitter>()
        queue.add(BuildPropertyEmitter())

        do {
            queue.removeFirst().emit(queue)
        } while (queue.isNotEmpty())
    }

    private abstract inner class Emitter {
        abstract fun emit(queue: ArrayDeque<Emitter>)

        fun addProperty(property: PropertySpec.Builder) {
            if (isRequired) {
                property.addKdoc("Required property for [%T.$name]\n", declaration.type.toClassName())
            } else if (isNullable) {
                property.addKdoc("Nullable property for [%T.$name]\n", declaration.type.toClassName())
            } else {
                property.addKdoc("Optional property for [%T.$name]\n", declaration.type.toClassName())
            }
            property.addKdoc("@see %T.$name\n", declaration.type.toClassName())

            typeBuilder.addProperty(property.build())
        }

        fun addFunction(function: FunSpec.Builder) {
            function.addKdoc("@see ${declaration.builderName}.$name\n")
            function.addKdoc("@see %T.$name\n", declaration.type.toClassName())
            typeBuilder.addFunction(function.build())
        }

        fun addExtension(function: FunSpec.Builder) {
            fileBuilder.addFunction(function.build())
        }
    }

    private abstract inner class PropertyEmitter : Emitter()
    private abstract inner class FunctionEmitter : Emitter()
    private abstract inner class ExtensionEmitter : Emitter()

    private inner class BuildPropertyEmitter : Emitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            when {
                // 基础类型
                type.declaration.isPrimitive(resolver.builtIns) -> queue.add(PrimitivePropertyEmitter())
                // list, set, collection, array, vararg
                isVararg ||
                    type.isCollection(resolver) ||
                    type.isArray(resolver) -> queue.add(CollectionPropertyEmitter())

                // TODO IntArray, LongArray, CharArray, ShortArray, ByteArray, BooleanArray, DoubleArray, FloatArray

                // Map
                type.isMap(resolver) -> queue.add(MapPropertyEmitter())
                // TODO 枚举要不要也处理一下？

                // 普通类型
                else -> queue.add(SimplePropertyEmitter())
            }
        }
    }

    private inner class PrimitivePropertyEmitter : PropertyEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            // 原本的类型如果不可为null，使用notNull代理，否则初始为null
            // 添加属性。
            // 原始类型如果不是nullable，那么这里就是 delegate
            val propertySpec = PropertySpec.builder(
                name,
                typeOrNullable.toTypeName(typeParameterResolver)
            ).apply {
                if (declaration.annotationData.open) {
                    addModifiers(PROTECTED, OPEN)
                } else {
                    addModifiers(PRIVATE)
                }
                mutable(true)
                if (isNullableOrIsOptional) {
                    initializer("null")
                } else {
                    // by kotlin.properties.Delegates.notNull()
                    delegate("%T.notNull()", DelegatesClassName)
                }
            }

            addProperty(propertySpec)

            queue.add(SelfFunctionEmitter(type))

            findBuilder(type)?.also {
                queue.add(CollectionBuilderExtensionEmitter(it))
            }
        }
    }

    /**
     * 一个普通的但是不是privitive类型的 emitter，比如String
     */
    private inner class SimplePropertyEmitter : PropertyEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            val propertySpec = PropertySpec.builder(
                name,
                typeOrNullable.toTypeName(typeParameterResolver)
            ).apply {
                if (declaration.annotationData.open) {
                    addModifiers(PROTECTED, OPEN)
                } else {
                    addModifiers(PRIVATE)
                }
                mutable(true)
                if (isNullableOrIsOptional) {
                    initializer("null")
                } else {
                    addModifiers(LATEINIT)
                }
            }

            addProperty(propertySpec)

            queue.add(SelfFunctionEmitter(type))

            findBuilder(type)?.also {
                queue.add(SimpleBuilderExtensionEmitter(it))
            }
        }
    }

    private inner class CollectionPropertyEmitter : PropertyEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            // list or set or array or vararg
            val typeParameter: KSTypeArgument? = if (isVararg) {
                resolver.getTypeArgument(
                    resolver.createKSTypeReferenceFromKSType(type),
                    Variance.INVARIANT
                )
            } else {
                type.arguments.firstOrNull()
            }

            var propertyType: KSType
            var initializer: CodeBlock

            var isSet = false

            when {
                type.isSet(resolver) -> {
                    isSet = true
                    // Create MutableSet<T>
                    propertyType = resolver.getClassDeclarationByName("kotlin.collections.MutableSet")
                        ?.asType(typeParameter?.let(::listOf) ?: emptyList())
                        ?: environment.reportError("Cannot find MutableSet class KSType", node)

                    initializer = CodeBlock.of(
                        "%M<%T>()",
                        MemberName("kotlin.collections", "mutableSetOf"),
                        typeParameter?.toTypeName(typeParameterResolver) ?: ANY
                    )
                }

                // list, collection, 都用list
                else -> {
                    // Create MutableList<T>
                    propertyType = resolver.getClassDeclarationByName("kotlin.collections.MutableList")
                        ?.asType(typeParameter?.let(::listOf) ?: emptyList())
                        ?: environment.reportError("Cannot find MutableList class KSType", node)

                    initializer = CodeBlock.of(
                        "%M<%T>()",
                        MemberName("kotlin.collections", "mutableListOf"),
                        typeParameter?.toTypeName(typeParameterResolver) ?: ANY
                    )
                }
            }

            if (isNullableOrIsOptional) {
                propertyType = propertyType.makeNullable()
            }

            val propertySpec = PropertySpec.builder(
                name,
                propertyType.toTypeName(typeParameterResolver)
            ).apply {
                if (declaration.annotationData.open) {
                    addModifiers(PROTECTED, OPEN)
                } else {
                    addModifiers(PRIVATE)
                }
                mutable(true)
                initializer(if (isNullableOrIsOptional) CodeBlock.of("null") else initializer)
            }

            addProperty(propertySpec)

            val isMutable = type.isCollection(resolver, mutable = true)

            passing = if (isMutable) {
                // toMutableSet/List
                if (isSet) {
                    CodeBlock.of(".%M()", MemberName("kotlin.collections", "toMutableSet", true))
                } else {
                    CodeBlock.of(".%M()", MemberName("kotlin.collections", "toMutableList", true))
                }
            } else if (isVararg || type.isArray(resolver)) {
                if (typeParameter?.type?.resolve()?.declaration is KSClassDeclaration) {
                    // reified type
                    CodeBlock.of(".%M()", MemberName("kotlin.collections", "toTypedArray", true))

                } else {
                    // typedArray.toTypedArray<Any?>() as Array<T>
                    val asType = if (isNullableOrIsOptional) "Array<%T>?" else "Array<%T>"
                    CodeBlock.of(
                        ".%M<Any?>() as $asType",
                        MemberName("kotlin.collections", "toTypedArray", true),
                        typeParameter?.type?.toTypeName(typeParameterResolver) ?: ANY
                    )
                }
            } else {
                // toList/Set
                if (isSet) {
                    CodeBlock.of(".%M()", MemberName("kotlin.collections", "toSet", true))
                } else {
                    CodeBlock.of(".%M()", MemberName("kotlin.collections", "toList", true))
                }
            }

            queue.add(SelfFunctionEmitter(propertyType))
            queue.add(
                CollectionFunctionEmitter(
                    propertyType,
                    initializer,
                    typeParameter?.type?.resolve() ?: resolver.builtIns.anyType,
                )
            )

        }
    }

    private inner class MapPropertyEmitter : PropertyEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            val keyTypeArgument = type.arguments.firstOrNull()
            val valueTypeArgument = type.arguments.getOrNull(1)

            fun anyTypeArgument() = resolver.getTypeArgument(
                resolver.createKSTypeReferenceFromKSType(resolver.builtIns.anyType),
                Variance.INVARIANT
            )

            // MutableMap<K, V>
            var propertyType: KSType = resolver.getClassDeclarationByName("kotlin.collections.MutableMap")
                ?.asType(
                    listOf(
                        keyTypeArgument ?: anyTypeArgument(),
                        valueTypeArgument ?: anyTypeArgument()
                    )
                )
                ?: environment.reportError(
                    "Cannot find MutableMap class KSType",
                    node
                )

            val initializer: CodeBlock = CodeBlock.of(
                "%M<%T, %T>()",
                MemberName("kotlin.collections", "mutableMapOf"),
                keyTypeArgument?.toTypeName(typeParameterResolver) ?: ANY,
                valueTypeArgument?.toTypeName(typeParameterResolver) ?: ANY
            )

            if (isNullableOrIsOptional) {
                propertyType = propertyType.makeNullable()
            }

            val propertySpec = PropertySpec.builder(
                name,
                propertyType.toTypeName(typeParameterResolver)
            ).apply {
                if (declaration.annotationData.open) {
                    addModifiers(PROTECTED, OPEN)
                } else {
                    addModifiers(PRIVATE)
                }
                mutable(true)
                initializer(if (isNullableOrIsOptional) CodeBlock.of("null") else initializer)
            }

            addProperty(propertySpec)

            val isMutable = type.isMap(resolver, mutable = true)

            passing = if (isMutable) {
                CodeBlock.of(".%M()", MemberName("kotlin.collections", "toMutableMap", true))
            } else {
                CodeBlock.of(".%M()", MemberName("kotlin.collections", "toMap", true))
            }

            queue.add(SelfFunctionEmitter(propertyType))
            queue.add(
                MapFunctionEmitter(
                    propertyType,
                    initializer,
                    keyTypeArgument?.type?.resolve() ?: resolver.builtIns.anyType,
                    valueTypeArgument?.type?.resolve() ?: resolver.builtIns.anyType,
                )
            )
        }
    }

    /**
     * 为属性生成一个参数是自己的同名函数
     * ```kotlin
     * var name: String? = null
     *
     * fun name(name: String?): Builder = apply {
     *     this.name = name
     * }
     * ```
     */
    private inner class SelfFunctionEmitter(val propertyType: KSType) : FunctionEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            addFunction(
                FunSpec.builder(name).apply {
                    addModifiers(PUBLIC)
                    if (declaration.annotationData.open) {
                        addModifiers(OPEN)
                    }
                    returns(this@BuilderProperty.builderTypeName)
                    addParameter(name, propertyType.toTypeName(typeParameterResolver))
                    addCode(
                        buildCodeBlock {
                            inReturnApplyBlock {
                                addStatement("this.$name = $name")
                            }
                        }
                    )
                }
            )
        }
    }

    private abstract inner class CollectionOrMapFunctionEmitter(
        val propertyType: KSType,
        val propertyInitializer: CodeBlock,
        val elementType: KSType,
    ) : FunctionEmitter() {
        val accessFun: FunSpec? = if (isNullableOrIsOptional) {
            FunSpec.builder("_access$firstUpperName").apply {
                // Supress FunctionName
                addAnnotation(
                    AnnotationSpec.builder(Suppress::class)
                        .addMember("%S", "FunctionName")
                        .build()
                )
                addModifiers(PRIVATE)
                returns(propertyType.makeNotNullable().toTypeName(typeParameterResolver))
                addCode(
                    buildCodeBlock {
                        inStatement {
                            add("return $name ?: ")
                            add(propertyInitializer)
                            add(".also { this.$name = it }")
                        }
                    }
                )
            }.build().also {
                typeBuilder.addFunction(it)
            }
        } else {
            null
        }

        val accessor = accessFun?.let { funSpec ->
            CodeBlock.of("%N()", funSpec)
        } ?: if (isNullableOrIsOptional) {
            CodeBlock.of("$name?")
        } else {
            CodeBlock.of(name)
        }

        /**
         * 函数已经包含 name，returns
         */
        inline fun addAddFunction(block: FunSpec.Builder.() -> Unit) {
            addFunction(
                FunSpec.builder("add$firstUpperName").apply {
                    addKdoc("Add an element to [$name]\n")
                    addModifiers(PUBLIC)
                    if (declaration.annotationData.open) {
                        addModifiers(OPEN)
                    }
                    returns(builderTypeName)
                    block()
                }
            )
        }

        /**
         * 函数已经包含 name，returns
         */
        inline fun addAddAllFunction(block: FunSpec.Builder.() -> Unit) {
            addFunction(
                FunSpec.builder("addAll$firstUpperName").apply {
                    addKdoc("Add all elements to [$name].\n")
                    addModifiers(PUBLIC)
                    if (declaration.annotationData.open) {
                        addModifiers(OPEN)
                    }
                    returns(builderTypeName)
                    block()
                }
            )
        }

        /**
         * 函数已经包含 name，returns
         */
        inline fun addClearFunction(block: FunSpec.Builder.() -> Unit) {
            addFunction(
                FunSpec.builder("clear$firstUpperName").apply {
                    addKdoc("Clear elements of [$name].\n")
                    if (isNullableOrIsOptional) {
                        addKdoc("_[$name] Will be set to null internally._\n")
                    }
                    addModifiers(PUBLIC)
                    if (declaration.annotationData.open) {
                        addModifiers(OPEN)
                    }
                    returns(builderTypeName)
                    block()
                }
            )
        }
    }

    /**
     * 生成 `add`, `addAll`, `clear`,
     * 如果 `T` 也拥有 Builder，则再添加 extension function
     *
     * ```kotlin
     * var list = mutableListOf<T>()
     *
     * private fun initList(): ML<T> {
     *     return list ?: <init>.also { this.list = it }
     * }
     *
     * fun add(element: T): Builder = apply {
     *     initList().add(element)
     * }
     * ```
     *
     */
    private inner class CollectionFunctionEmitter(
        propertyType: KSType,
        propertyInitializer: CodeBlock,
        elementType: KSType,
    ) : CollectionOrMapFunctionEmitter(
        propertyType,
        propertyInitializer,
        elementType,
    ) {
        override fun emit(queue: ArrayDeque<Emitter>) {
            // add(T)
            val elementTypeName = elementType.toTypeName(typeParameterResolver)

            addAddFunction {
                addParameter("element", elementTypeName)
                addCode(
                    buildCodeBlock {
                        inReturnApplyStatement {
                            add(accessor)
                            add(".add(element)")
                        }
                    }
                )
            }

            // addAll(Collection<T>)
            // addAll(vararg T)

            addAddAllFunction {
                addParameter("elements", COLLECTION.parameterizedBy(elementTypeName))
                addCode(
                    buildCodeBlock {
                        inReturnApplyStatement {
                            add(accessor)
                            add(".addAll(elements)")
                        }
                    }
                )
            }

            addAddAllFunction {
                addParameter("elements", elementTypeName, VARARG)
                addCode(
                    buildCodeBlock {
                        inReturnApplyStatement {
                            add(accessor)
                            add(".addAll(elements.%M())", MemberName("kotlin.collections", "asList", true))
                        }
                    }
                )
            }

            // clear()
            addClearFunction {
                addCode(
                    buildCodeBlock {
                        inReturnApplyBlock {
                            if (isNullableOrIsOptional) {
                                addStatement("this.$name = null")
                            } else {
                                addStatement("this.$name.clear()")
                            }
                        }
                    }
                )
            }

            findBuilder(elementType)?.also {
                queue.add(CollectionBuilderExtensionEmitter(it))
            }
        }
    }

    private inner class MapFunctionEmitter(
        propertyType: KSType,
        propertyInitializer: CodeBlock,
        val keyType: KSType,
        elementType: KSType,
    ) : CollectionOrMapFunctionEmitter(
        propertyType,
        propertyInitializer,
        elementType,
    ) {
        override fun emit(queue: ArrayDeque<Emitter>) {
            val keyTypeName = keyType.toTypeName(typeParameterResolver)
            val valueTypeName = elementType.toTypeName(typeParameterResolver)

            // add(K, V)
            addAddFunction {
                // key
                // value
                addParameter("key", keyTypeName)
                addParameter("value", valueTypeName)
                addCode(
                    buildCodeBlock {
                        inReturnApplyStatement {
                            add(accessor)
                            add(".put(key, value)")
                        }
                    }
                )
            }

            // addAll(Map<K, V>)
            addAddAllFunction {
                addParameter("elements", MAP.parameterizedBy(keyTypeName, valueTypeName))
                addCode(
                    buildCodeBlock {
                        inReturnApplyStatement {
                            add(accessor)
                            add(".putAll(elements)")
                        }
                    }
                )
            }

            // clear()
            addClearFunction {
                addCode(
                    buildCodeBlock {
                        inReturnApplyBlock {
                            if (isNullableOrIsOptional) {
                                addStatement("this.$name = null")
                            } else {
                                addStatement("this.$name.clear()")
                            }
                        }
                    }
                )
            }

            findBuilder(keyType)?.also { keyBuilder ->
                queue.add(MapBuilderExtensionEmitter(keyTypeName, keyBuilder))
            }
        }
    }

    /**
     * 为 element 类型也存在 Builder 的属性添加扩展函数。
     *
     * ```kotlin
     *
     * inline fun Builder.addXxx(block: ThatBuilder.() -> Unit): Builder {
     *     return addXxx(ThatBuilder().also(block).build())
     * }
     *
     * ```
     */
    private inner class CollectionBuilderExtensionEmitter(
        val otherBuilderName: TypeName,
    ) : ExtensionEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            val fn = "add$firstUpperName"
            addExtension(
                FunSpec.builder(fn).apply {
                    addKdoc("@see %T", otherBuilderName)
                    addModifiers(INLINE)
                    if (declaration.annotationData.internal) {
                        addModifiers(INTERNAL)
                    } else {
                        addModifiers(PUBLIC)
                    }
                    returns(builderTypeName)
                    receiver(builderTypeName)
                    addTypeVariables(typeParameters)
                    addParameter(
                        "block",
                        LambdaTypeName.get(
                            receiver = otherBuilderName,
                            returnType = UNIT
                        )
                    )
                    addCode("return $fn(%T().also(block).build())", otherBuilderName)
                }
            )
        }
    }

    /**
     * 为 element 类型也存在 Builder 的属性添加扩展函数。
     *
     * ```kotlin
     *
     * inline fun Builder.addXxx(K, block: ThatBuilder.() -> Unit): Builder {
     *     return addXxx(key, ThatBuilder().also(block).build())
     * }
     *
     * ```
     */
    private inner class MapBuilderExtensionEmitter(
        val keyType: TypeName,
        val otherBuilderName: TypeName,
    ) : ExtensionEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            val fn = "add$firstUpperName"
            addExtension(
                FunSpec.builder(fn).apply {
                    addKdoc("@see %T", otherBuilderName)
                    addModifiers(INLINE)
                    if (declaration.annotationData.internal) {
                        addModifiers(INTERNAL)
                    } else {
                        addModifiers(PUBLIC)
                    }
                    returns(builderTypeName)
                    receiver(builderTypeName)
                    addTypeVariables(typeParameters)
                    addParameter("key", keyType)
                    addParameter(
                        "block",
                        LambdaTypeName.get(
                            receiver = otherBuilderName,
                            returnType = UNIT
                        )
                    )
                    addCode("return $fn(key, %T().also(block).build())", otherBuilderName)
                }
            )
        }
    }

    private inner class SimpleBuilderExtensionEmitter(
        val otherBuilderName: TypeName
    ) : ExtensionEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            val fn = name
            addExtension(
                FunSpec.builder(fn).apply {
                    addKdoc("@see ${declaration.builderName}.$fn\n")
                    addKdoc("@see %T.$name\n", declaration.type.toClassName())
                    addKdoc("@see %T", otherBuilderName)
                    addModifiers(INLINE)
                    if (declaration.annotationData.internal) {
                        addModifiers(INTERNAL)
                    } else {
                        addModifiers(PUBLIC)
                    }
                    returns(builderTypeName)
                    receiver(builderTypeName)
                    addTypeVariables(typeParameters)
                    addParameter(
                        "block",
                        LambdaTypeName.get(
                            receiver = otherBuilderName,
                            returnType = UNIT
                        )
                    )
                    addCode("return $fn(%T().also(block).build())", otherBuilderName)
                }
            )
        }
    }
}


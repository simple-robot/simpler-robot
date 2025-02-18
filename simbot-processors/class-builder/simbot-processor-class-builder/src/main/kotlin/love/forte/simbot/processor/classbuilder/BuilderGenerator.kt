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
import love.forte.simbot.processor.classbuilder.annotation.BuilderFor
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
    lateinit var parameters: List<BuilderValueParameterProperty>
    lateinit var properties: List<BuilderMemberProperty>
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

        // types
        typeSpecBuilder.addTypeVariables(typeVariables)

        // 添加注解
        typeSpecBuilder.addAnnotation(
            AnnotationSpec.builder(BuilderFor::class)
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
                .addMember("%S, %S, %S", "ALL", "unused", "RedundantVisibilityModifier")
                .build()
        )

        // 找到用于构建的构建函数，转化它的参数
        parameters =
            (type.primaryConstructor ?: type.getConstructors().firstOrNull())
                ?.parameters
                ?.map { parameter ->
                    BuilderValueParameterProperty(
                        resolver,
                        environment,
                        declaration,
                        fileSpecBuilder,
                        typeSpecBuilder,
                        typeParameterResolver,
                        this,
                        builderTypeName,
                        typeVariables,
                        builders,
                        parameter
                    )
                }
                ?: emptyList()

        val parameterPropertyNames = parameters.mapNotNullTo(mutableSetOf()) { parameter ->
            if (parameter.parameter.isVal || parameter.parameter.isVar) {
                parameter.name
            } else {
                null
            }
        }

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
                BuilderMemberProperty(
                    resolver,
                    environment,
                    declaration,
                    fileSpecBuilder,
                    typeSpecBuilder,
                    typeParameterResolver,
                    this,
                    builderTypeName,
                    typeVariables,
                    builders,
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
                addModifiers(PUBLIC)
                if (typeVariables.isEmpty()) {
                    returns(declaration.type.toClassName())
                } else {
                    returns(declaration.type.toClassName().parameterizedBy(typeVariables))
                }
                addCode(
                    buildCodeBlock {
                        // return T(a = a, b = b, ...).also { it.c = c; it.d = d; }

                        addStatement("return TODO()")

                        // TODO
                        buildCodeBlock {
                            inStatement {
                                add("return %T(", declaration.type.toClassName())
                                for ((index, parameter) in this@BuilderGenerator.parameters.withIndex()) {
                                    val passing = parameter.passing ?: CodeBlock.of(parameter.name)
                                    if (index == 0) {
                                        add("${parameter.name} = ")
                                        add(passing)
                                    } else {
                                        add(", ${parameter.name} = ")
                                        add(passing)
                                    }
                                }
                                add(")")
                            }
                            inControlFlow(".apply") {
                                addStatement("TODO()")
                            }
                        }.toString().lines().forEach { line ->
                            addComment(
                                "%L",
                                line
                            )
                        }


                    }
                )
            }.build()
        )
    }
}

/**
 * 一个需要通过 Builder 进行配置的属性。
 */
internal abstract class BuilderProperty(
    val resolver: Resolver,
    val environment: SymbolProcessorEnvironment,
    val declaration: ExpectBuilderDeclaration,
    val fileBuilder: FileSpec.Builder,
    val typeBuilder: TypeSpec.Builder,
    val typeParameterResolver: TypeParameterResolver,
    val generator: BuilderGenerator,
    val builderTypeName: TypeName,
    val typeParameters: List<TypeVariableName>,
    val builderNames: Map<ClassName, TypeName>
) {
    abstract val name: String
    abstract val type: KSType

    abstract fun emit()
}

/**
 * 一个在原类型中的构造函数中的参数。
 * 不一定是个属性，但是作为构造参数它是必须的，尽管有默认值。
 *
 * - 如果参数nullable -> 属性可null，默认为 `null`
 * - 如果参数notnull -> 属性lateinit or delegate
 *
 * 因为具名参数的是否默认不能被动态使用。
 * 参考 [KT-18695](https://youtrack.jetbrains.com/issue/KT-18695)
 *
 * 这是一个可变类，不能重复使用。
 */
internal class BuilderValueParameterProperty(
    resolver: Resolver,
    environment: SymbolProcessorEnvironment,
    declaration: ExpectBuilderDeclaration,
    fileBuilder: FileSpec.Builder,
    typeBuilder: TypeSpec.Builder,
    typeParameterResolver: TypeParameterResolver,
    generator: BuilderGenerator,
    builderTypeName: TypeName,
    typeParameters: List<TypeVariableName>,
    builderNames: Map<ClassName, TypeName>,
    val parameter: KSValueParameter,
) : BuilderProperty(
    resolver,
    environment,
    declaration,
    fileBuilder,
    typeBuilder,
    typeParameterResolver,
    generator,
    builderTypeName,
    typeParameters,
    builderNames
) {
    companion object {
        val DelegatesClassName = Delegates::class.asClassName()
    }

    override val name: String = parameter.name?.asString() ?: environment.reportError(
        "Parameter name is null: $parameter",
        parameter
    )

    val firstUpperName: String = name.replaceFirstChar { it.uppercase(Locale.US) }

    override val type: KSType = parameter.type.resolve()

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

    override fun emit() {
        val queue = ArrayDeque<Emitter>()
        queue.add(BuildPropertyEmitter())

        do {
            queue.removeFirst().emit(queue)
        } while (queue.isNotEmpty())
    }

    private abstract class Emitter {
        abstract fun emit(queue: ArrayDeque<Emitter>)
    }

    private abstract class PropertyEmitter : Emitter()
    private abstract class FunctionEmitter : Emitter()
    private abstract class ExtensionEmitter : Emitter()

    private inner class BuildPropertyEmitter : PropertyEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            when {
                // 基础类型
                type.declaration.isPrimitive(resolver.builtIns) -> queue.add(PrimitivePropertyEmitter())
                // list, set, collection, array, vararg
                type.isCollection(resolver) ||
                    type.isArray(resolver) ||
                    parameter.isVararg -> queue.add(CollectionPropertyEmitter())

                // Map
                type.isMap(resolver) -> queue.add(MapPropertyEmitter())

                // 普通类型
                else -> queue.add(SimplePropertyEmitter())

                // TODO 枚举要不要也处理一下？
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
                type.toTypeName(typeParameterResolver)
            ).apply {
                addKdoc("@see %T.$name", declaration.type.toClassName())
                addModifiers(PUBLIC)
                mutable(true)
                if (type.isMarkedNullable) {
                    initializer("null")
                } else {
                    // by kotlin.properties.Delegates.notNull()
                    delegate("%T.notNull()", DelegatesClassName)
                }

            }.build()

            typeBuilder.addProperty(propertySpec)

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
                type.toTypeName(typeParameterResolver)
            ).apply {
                addKdoc("@see %T.$name", declaration.type.toClassName())
                addModifiers(PUBLIC)
                mutable(true)
                if (type.isMarkedNullable) {
                    initializer("null")
                } else {
                    addModifiers(LATEINIT)
                }
            }.build()

            typeBuilder.addProperty(propertySpec)

            queue.add(SelfFunctionEmitter(type))

            findBuilder(type)?.also {
                queue.add(SimpleBuilderExtensionEmitter(it))
            }
        }
    }

    private inner class CollectionPropertyEmitter : PropertyEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            val isNullable = type.isMarkedNullable
            val isVararg = parameter.isVararg


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

            val isMutable = type.isCollection(resolver, mutable = true)
            var isSet = false

            when {
                type.isSet(resolver) -> {
                    isSet = true
                    // Create MutableSet<T>
                    propertyType = resolver.getClassDeclarationByName("kotlin.collections.MutableSet")
                        ?.asType(typeParameter?.let(::listOf) ?: emptyList())
                        ?: environment.reportError("Cannot find MutableSet class KSType", parameter)

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
                        ?: environment.reportError("Cannot find MutableList class KSType", parameter)

                    initializer = CodeBlock.of(
                        "%M<%T>()",
                        MemberName("kotlin.collections", "mutableListOf"),
                        typeParameter?.toTypeName(typeParameterResolver) ?: ANY
                    )
                }
            }

            if (isNullable) {
                propertyType = propertyType.makeNullable()
            }

            val propertySpec = PropertySpec.builder(
                name,
                propertyType.toTypeName(typeParameterResolver)
            ).apply {
                addKdoc("@see %T.$name", declaration.type.toClassName())
                addModifiers(PUBLIC)
                mutable(true)
                initializer(if (isNullable) CodeBlock.of("null") else initializer)
            }.build()

            typeBuilder.addProperty(propertySpec)

            if (isMutable) {
                // toMutableSet/List
                passing = if (isSet) {
                    CodeBlock.of("$name.%M()", MemberName("kotlin.collections", "toMutableSet", true))
                } else {
                    CodeBlock.of("$name.%M()", MemberName("kotlin.collections", "toMutableList", true))
                }
            } else {
                // toList/Set
                passing = if (isSet) {
                    CodeBlock.of("$name.%M()", MemberName("kotlin.collections", "toSet", true))
                } else {
                    CodeBlock.of("$name.%M()", MemberName("kotlin.collections", "toList", true))
                }
            }

            queue.add(SelfFunctionEmitter(propertyType))
            queue.add(
                CollectionFunctionEmitter(
                    propertyType,
                    initializer,
                    typeParameter?.type?.resolve() ?: resolver.builtIns.anyType,
                    isNullable
                )
            )

        }
    }

    private inner class MapPropertyEmitter : PropertyEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            val isNullable = type.isMarkedNullable
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
                    parameter
                )

            val initializer: CodeBlock = CodeBlock.of(
                "%M<%T, %T>()",
                MemberName("kotlin.collections", "mutableMapOf"),
                keyTypeArgument?.toTypeName(typeParameterResolver) ?: ANY,
                valueTypeArgument?.toTypeName(typeParameterResolver) ?: ANY
            )

            if (isNullable) {
                propertyType = propertyType.makeNullable()
            }

            val propertySpec = PropertySpec.builder(
                name,
                propertyType.toTypeName(typeParameterResolver)
            ).apply {
                addKdoc("@see %T.$name", declaration.type.toClassName())
                addModifiers(PUBLIC)
                mutable(true)
                initializer(if (isNullable) CodeBlock.of("null") else initializer)
            }.build()

            typeBuilder.addProperty(propertySpec)

            queue.add(SelfFunctionEmitter(propertyType))
            queue.add(
                MapFunctionEmitter(
                    propertyType,
                    initializer,
                    keyTypeArgument?.type?.resolve() ?: resolver.builtIns.anyType,
                    valueTypeArgument?.type?.resolve() ?: resolver.builtIns.anyType,
                    isNullable
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
            typeBuilder.addFunction(
                FunSpec.builder(name).apply {
                    addModifiers(PUBLIC)
                    returns(this@BuilderValueParameterProperty.builderTypeName)
                    addParameter(name, propertyType.toTypeName(typeParameterResolver))
                    addCode(
                        buildCodeBlock {
                            inReturnApplyBlock {
                                addStatement("this.$name = $name")
                            }
                        }
                    )
                }.build()
            )
        }
    }

    private abstract inner class CollectionOrMapFunctionEmitter(
        val propertyType: KSType,
        val propertyInitializer: CodeBlock,
        val elementType: KSType,
        val isNullable: Boolean,
    ) : FunctionEmitter() {
        val accessFun: FunSpec? = if (isNullable) {
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
        } ?: CodeBlock.of(name)

        /**
         * 函数已经包含 name，returns
         */
        inline fun addAddFunction(block: FunSpec.Builder.() -> Unit) {
            typeBuilder.addFunction(
                FunSpec.builder("add$firstUpperName").apply {
                    addModifiers(PUBLIC)
                    returns(builderTypeName)
                    block()
                }.build()
            )
        }

        /**
         * 函数已经包含 name，returns
         */
        inline fun addAddAllFunction(block: FunSpec.Builder.() -> Unit) {
            typeBuilder.addFunction(
                FunSpec.builder("addAll$firstUpperName").apply {
                    addModifiers(PUBLIC)
                    returns(builderTypeName)
                    block()
                }.build()
            )
        }

        /**
         * 函数已经包含 name，returns
         */
        inline fun addClearFunction(block: FunSpec.Builder.() -> Unit) {
            typeBuilder.addFunction(
                FunSpec.builder("clear$firstUpperName").apply {
                    addModifiers(PUBLIC)
                    returns(builderTypeName)
                    block()
                }.build()
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
        isNullable: Boolean,
    ) : CollectionOrMapFunctionEmitter(
        propertyType,
        propertyInitializer,
        elementType,
        isNullable
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
                addParameter("elements", elementTypeName, KModifier.VARARG)
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
                            if (isNullable) {
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
        isNullable: Boolean,
    ) : CollectionOrMapFunctionEmitter(
        propertyType,
        propertyInitializer,
        elementType,
        isNullable
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
                            if (isNullable) {
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
            fileBuilder.addFunction(
                FunSpec.builder(fn).apply {
                    addKdoc("@see %T.$fn\n", declaration.type.toClassName())
                    addKdoc("@see %T", otherBuilderName)
                    addModifiers(PUBLIC, INLINE)
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
                }.build()
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
            fileBuilder.addFunction(
                FunSpec.builder(fn).apply {
                    addKdoc("@see %T.$fn\n", declaration.type.toClassName())
                    addKdoc("@see %T", otherBuilderName)
                    addModifiers(PUBLIC, INLINE)
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
                }.build()
            )
        }
    }

    private inner class SimpleBuilderExtensionEmitter(
        val otherBuilderName: TypeName
    ) : ExtensionEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            val fn = name
            fileBuilder.addFunction(
                FunSpec.builder(fn).apply {
                    addKdoc("@see %T.$fn\n", declaration.type.toClassName())
                    addKdoc("@see %T", otherBuilderName)

                    addModifiers(PUBLIC, INLINE)
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
                }.build()
            )
        }
    }
}

internal class BuilderMemberProperty(
    resolver: Resolver,
    environment: SymbolProcessorEnvironment,
    declaration: ExpectBuilderDeclaration,
    fileBuilder: FileSpec.Builder,
    typeBuilder: TypeSpec.Builder,
    typeParameterResolver: TypeParameterResolver,
    generator: BuilderGenerator,
    builderClassName: TypeName,
    typeParameters: List<TypeVariableName>,
    builderNames: Map<ClassName, TypeName>,
    val property: KSPropertyDeclaration
) : BuilderProperty(
    resolver,
    environment,
    declaration,
    fileBuilder,
    typeBuilder,
    typeParameterResolver,
    generator,
    builderClassName,
    typeParameters,
    builderNames
) {
    override val name: String = property.simpleName.asString()
    override val type: KSType = property.type.resolve()

    override fun emit() {
        // TODO("Not yet implemented")
    }
}

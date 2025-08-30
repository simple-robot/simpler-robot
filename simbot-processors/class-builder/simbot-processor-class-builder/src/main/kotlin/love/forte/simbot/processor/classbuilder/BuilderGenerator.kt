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
import love.forte.codegentle.common.code.*
import love.forte.codegentle.common.ksp.toClassName
import love.forte.codegentle.common.ksp.toClassNameOrNull
import love.forte.codegentle.common.naming.*
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.addMember
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinFile
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinSimpleFileBuilder
import love.forte.codegentle.kotlin.ksp.toTypeRef
import love.forte.codegentle.kotlin.ksp.toTypeVariableRef
import love.forte.codegentle.kotlin.modifiers
import love.forte.codegentle.kotlin.naming.KotlinAnnotationNames
import love.forte.codegentle.kotlin.naming.KotlinClassNames
import love.forte.codegentle.kotlin.naming.KotlinClassNames.COLLECTION
import love.forte.codegentle.kotlin.naming.KotlinLambdaTypeName
import love.forte.codegentle.kotlin.ref.addKotlinAnnotation
import love.forte.codegentle.kotlin.spec.*
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

    lateinit var typeVariables: List<TypeRef<TypeVariableName>>
    lateinit var parameters: List<BuilderProperty>
    lateinit var properties: List<BuilderProperty>
    lateinit var typeSpecBuilder: KotlinSimpleTypeSpec.Builder
    lateinit var fileSpecBuilder: KotlinSimpleFileBuilder

    fun prepare() {
        val type = declaration.type
        type.containingFile?.also { sources.add(it) }


        // val typeParameterResolver = type.typeParameters.toTypeParameterResolver()

        typeVariables = type.typeParameters.map { it.toTypeVariableRef() }

        val builderClassName = ClassName(
            type.packageName.asString().parseToPackageName(),
            declaration.builderName
        )

        var builderTypeName: TypeName = builderClassName

        if (typeVariables.isNotEmpty()) {
            builderTypeName = builderClassName.parameterized(typeVariables)
        }

        builders[type.toClassName()] = builderTypeName

        typeSpecBuilder = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, builderClassName.name)

        if (declaration.annotationData.open) {
            typeSpecBuilder.addModifier(KotlinModifier.OPEN)
        }

        if (declaration.annotationData.internal) {
            typeSpecBuilder.addModifiers(KotlinModifier.INTERNAL)
        }

        // types
        typeSpecBuilder.addTypeVariables(typeVariables)

        // 添加注解
        typeSpecBuilder.addKotlinAnnotation(BuilderForClassName) {
            addMember(format = "%V::class") {
                emitType(type.toClassName())
            }
        }

        // marks
        declaration.annotationData.marks.forEach {
            it.toClassNameOrNull()?.also(typeSpecBuilder::addKotlinAnnotation)
        }

        fileSpecBuilder = KotlinFile.builder(type.packageName.asString().parseToPackageName())
            .name(declaration.builderName)

        // 添加 Suppress("ALL", "unused", "RedundantVisibilityModifier")
        fileSpecBuilder.addKotlinAnnotation(KotlinAnnotationNames.SUPPRESS) {
            addMember(format = "%V, %V, %V, %V") {
                emitString("ALL")
                emitString("unused")
                emitString("UNCHECKED_CAST")
                emitString("RedundantVisibilityModifier")
            }
            // addMultipleMembers(
            //     codeValues = listOf(
            //         CodeValue("%V", CodePart.string("ALL")),
            //         CodeValue("%V", CodePart.string("unused")),
            //         CodeValue("%V", CodePart.string("UNCHECKED_CAST")),
            //         CodeValue("%V", CodePart.string("RedundantVisibilityModifier")),
            //     )
            // )
        }

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
                        // typeParameterResolver = typeParameterResolver,
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
                    // typeParameterResolver = typeParameterResolver,
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

    fun generate(): KotlinFile {
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

    private fun KotlinSimpleTypeSpec.Builder.emitBuild(typeVariables: List<TypeRef<TypeVariableName>>) {
        addFunction("build") {
            if (declaration.annotationData.open) {
                modifiers.open()
            }

            if (typeVariables.isEmpty()) {
                returns(declaration.type.toClassName().ref())
            } else {
                returns(declaration.type.toClassName().parameterized(typeVariables).ref())
            }

            addCode {
                // return T(a = a, b = b, ...).also { it.c = c; it.d = d; }
                addCode("return %V(", CodePart.type(declaration.type.toClassName()))
                for ((index, parameter) in this@BuilderGenerator.parameters.withIndex()) {
                    val accessName = if (parameter.isNullableOrIsOptional) {
                        "${parameter.name}?"
                    } else {
                        parameter.name
                    }

                    val passing = parameter.passing?.let { p ->
                        CodeValue {
                            addCode(accessName)
                            addCode(p)
                        }
                    } ?: CodeValue(parameter.name)

                    if (index == 0) {
                        addCode("${parameter.name} = ")
                        addCode(passing)
                    } else {
                        addCode(", ${parameter.name} = ")
                        addCode(passing)
                    }
                }
                addCode(")")
                // addCode(".also {\n")
                inControlFlow(".also") {
                    for ((index, property) in this@BuilderGenerator.properties.withIndex()) {
                        // this.xxx?.also { value -> it.xxx = value }
                        val name = property.name
                        val localName = "v$index"
                        val accessName = if (property.isNullableOrIsOptional) "$name?" else name
                        val passing: CodeValue = property.passing?.let { p ->
                            CodeValue {
                                addCode("(")
                                addCode(accessName)
                                addCode(p)
                                addCode(")")
                            }
                        } ?: CodeValue(name)

                        inStatement {
                            addCode(passing)
                            addCode("?.also { $localName -> it.$name = $localName")
                            addCode(" }")
                        }
                    }
                }
                // addCode("\n}")
            }
        }
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
    val fileBuilder: KotlinSimpleFileBuilder,
    val typeBuilder: KotlinSimpleTypeSpec.Builder,
    // val typeParameterResolver: TypeParameterResolver,
    val generator: BuilderGenerator,
    val builderTypeName: TypeName,
    val typeParameters: List<TypeRef<TypeVariableName>>,
    val builderNames: Map<ClassName, TypeName>,
    val isVararg: Boolean,
    val isRequired: Boolean,
    val name: String,
    val type: KSType,
    val node: KSNode?
) {
    companion object {
        val DelegatesClassName = Delegates::class.toClassName()
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
    var passing: CodeValue? = null

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
            it.toTypeVariableRef() // typeParameterResolver
        }

        var returnTypeName: TypeName = className
        if (typeParameters.isNotEmpty()) {
            returnTypeName = className.parameterized(typeParameters)
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

        fun addProperty(property: KotlinPropertySpec.Builder) {
            if (isRequired) {
                property.addDoc("Required property for [%V.$name]\n", CodePart.type(declaration.type.toClassName()))
            } else if (isNullable) {
                property.addDoc("Nullable property for [%V.$name]\n", CodePart.type(declaration.type.toClassName()))
            } else {
                property.addDoc("Optional property for [%V.$name]\n", CodePart.type(declaration.type.toClassName()))
            }
            property.addDoc("@see %V.$name\n", CodePart.type(declaration.type.toClassName()))

            typeBuilder.addProperty(property.build())
        }

        fun addFunction(function: KotlinFunctionSpec.Builder) {
            function.addDoc("@see ${declaration.builderName}.$name\n")
            function.addDoc("@see %V.$name\n", CodePart.type(declaration.type.toClassName()))
            typeBuilder.addFunction(function.build())
        }

        fun addExtension(function: KotlinFunctionSpec.Builder) {
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
            val propertySpec = KotlinPropertySpec.builder(
                name,
                // typeOrNullable.toTypeName(typeParameterResolver)
                typeOrNullable.toTypeRef()
            ).apply {
                if (declaration.annotationData.open) {
                    modifiers {
                        protected()
                        open()
                    }
                } else {
                    modifiers.private()
                }
                mutable(true)
                if (isNullableOrIsOptional) {
                    initializer("null")
                } else {
                    // by kotlin.properties.Delegates.notNull()
                    delegate("%V.notNull()", CodePart.type(DelegatesClassName))
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
            val propertySpec = KotlinPropertySpec.builder(
                name,
                // typeOrNullable.toTypeName(typeParameterResolver)
                typeOrNullable.toTypeRef()
            ).apply {
                if (declaration.annotationData.open) {
                    modifiers {
                        protected()
                        open()
                    }
                } else {
                    modifiers.private()
                }
                mutable(true)
                if (isNullableOrIsOptional) {
                    initializer("null")
                } else {
                    modifiers.lateinit()
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
            var initializer: CodeValue

            var isSet = false

            when {
                type.isSet(resolver) -> {
                    isSet = true
                    // Create MutableSet<T>
                    propertyType = resolver.getClassDeclarationByName("kotlin.collections.MutableSet")
                        ?.asType(typeParameter?.let(::listOf) ?: emptyList())
                        ?: environment.reportError("Cannot find MutableSet class KSType", node)

                    // initializer = CodeBlock.of(
                    //     "%M<%T>()",
                    //     MemberName("kotlin.collections", "mutableSetOf"),
                    //     typeParameter?.toTypeName(typeParameterResolver) ?: ANY
                    // )

                    initializer = CodeValue(
                        "%V<%V>()",
                    ) {
                        emitName(MemberName("kotlin.collections".parseToPackageName(), "mutableSetOf"))
                        emitType(typeParameter?.toTypeRef() ?: KotlinClassNames.ANY.ref())
                    }
                }

                // list, collection, 都用list
                else -> {
                    // Create MutableList<T>
                    propertyType = resolver.getClassDeclarationByName("kotlin.collections.MutableList")
                        ?.asType(typeParameter?.let(::listOf) ?: emptyList())
                        ?: environment.reportError("Cannot find MutableList class KSType", node)

                    // initializer = CodeBlock.of(
                    //     "%M<%T>()",
                    //     MemberName("kotlin.collections", "mutableListOf"),
                    //     typeParameter?.toTypeName(typeParameterResolver) ?: ANY
                    // )

                    initializer = CodeValue("%V<%V>()") {
                        emitName(MemberName("kotlin.collections", "mutableListOf"))
                        emitType(typeParameter?.toTypeRef() ?: KotlinClassNames.ANY.ref())
                    }
                }
            }

            if (isNullableOrIsOptional) {
                propertyType = propertyType.makeNullable()
            }

            val propertySpec = KotlinPropertySpec.builder(
                name,
                // propertyType.toTypeName(typeParameterResolver)
                propertyType.toTypeRef()
            ).apply {
                if (declaration.annotationData.open) {
                    modifiers {
                        protected()
                        open()
                    }
                } else {
                    modifiers.private()
                }
                mutable(true)
                initializer(if (isNullableOrIsOptional) CodeValue("null") else initializer)
            }

            addProperty(propertySpec)

            val isMutable = type.isCollection(resolver, mutable = true)

            passing = if (isMutable) {
                // toMutableSet/List
                if (isSet) {
                    CodeValue(".%V()", CodePart.name(MemberName("kotlin.collections", "toMutableSet")))
                } else {
                    CodeValue(".%V()", CodePart.name(MemberName("kotlin.collections", "toMutableList")))
                }
            } else if (isVararg || type.isArray(resolver)) {
                if (typeParameter?.type?.resolve()?.declaration is KSClassDeclaration) {
                    // reified type
                    CodeValue(".%V()", CodePart.name(MemberName("kotlin.collections", "toTypedArray")))

                } else {
                    // typedArray.toTypedArray<Any?>() as Array<T>
                    val asType = if (isNullableOrIsOptional) "Array<%V>?" else "Array<%V>"
                    CodeValue(".%V<Any?>() as $asType") {
                        emitName(MemberName("kotlin.collections", "toTypedArray"))
                        // typeParameter?.type?.toTypeName(typeParameterResolver) ?: ANY
                        emitType(typeParameter?.type?.toTypeRef() ?: KotlinClassNames.ANY.ref())
                    }
                }
            } else {
                // toList/Set
                if (isSet) {
                    CodeValue(".%V()", CodePart.name(MemberName("kotlin.collections", "toSet")))
                } else {
                    CodeValue(".%V()", CodePart.name(MemberName("kotlin.collections", "toList")))
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

            val initializer = CodeValue("%V<%V, %V>()") {
                emitName(MemberName("kotlin.collections", "mutableMapOf"))
                // keyTypeArgument?.toTypeRef(typeParameterResolver) ?: ANY
                // valueTypeArgument?.toTypeRef(typeParameterResolver) ?: ANY
                emitType(keyTypeArgument?.toTypeRef() ?: KotlinClassNames.ANY.ref())
                emitType(valueTypeArgument?.toTypeRef() ?: KotlinClassNames.ANY.ref())
            }

            if (isNullableOrIsOptional) {
                propertyType = propertyType.makeNullable()
            }

            val propertySpec = KotlinPropertySpec.builder(
                name,
                // propertyType.toTypeName(typeParameterResolver)
                propertyType.toTypeRef()
            ).apply {
                if (declaration.annotationData.open) {
                    modifiers {
                        protected()
                        open()
                    }
                } else {
                    modifiers.private()
                }
                mutable(true)
                initializer(if (isNullableOrIsOptional) CodeValue("null") else initializer)
            }

            addProperty(propertySpec)

            val isMutable = type.isMap(resolver, mutable = true)

            passing = if (isMutable) {
                CodeValue(".%V()", CodePart.name(MemberName("kotlin.collections", "toMutableMap")))
            } else {
                CodeValue(".%V()", CodePart.name(MemberName("kotlin.collections", "toMap")))
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
                KotlinFunctionSpec.builder(name).apply {
                    modifiers.public()
                    if (declaration.annotationData.open) {
                        modifiers.open()
                    }
                    returns(this@BuilderProperty.builderTypeName.ref())
                    // addParameter(name, propertyType.toTypeName(typeParameterResolver))
                    val name = this@BuilderProperty.name
                    addParameter(name, propertyType.toTypeRef())
                    addCode {
                        // addCode("return apply { this.$name = $name }")
                        inReturnApplyBlock {
                            addStatement("this.$name = $name")
                        }
                    }
                }
            )
        }
    }

    private abstract inner class CollectionOrMapFunctionEmitter(
        val propertyType: KSType,
        val propertyInitializer: CodeValue,
        val elementType: KSType,
    ) : FunctionEmitter() {
        val accessFun: KotlinFunctionSpec? = if (isNullableOrIsOptional) {
            KotlinFunctionSpec.builder("_access$firstUpperName").apply {
                // Suppress FunctionName
                addKotlinAnnotation(KotlinAnnotationNames.SUPPRESS) {
                    addMember(format = "%V", argumentParts = arrayOf(CodePart.string("FunctionName")))
                }
                modifiers.private()
                // returns(propertyType.makeNotNullable().toTypeName(typeParameterResolver))
                returns(propertyType.makeNotNullable().toTypeRef())
                val name = this@BuilderProperty.name
                addCode {
                    inStatement {
                        addCode("return $name ?: ")
                        addCode(propertyInitializer)
                        addCode(".also { this.$name = it }")
                    }
                }
            }.build().also {
                typeBuilder.addFunction(it)
            }
        } else {
            null
        }

        val accessor = accessFun?.let { funSpec ->
            CodeValue("%V()", CodePart.name(funSpec.name))
        } ?: if (isNullableOrIsOptional) {
            CodeValue("$name?")
        } else {
            CodeValue(name)
        }

        /**
         * 函数已经包含 name，returns
         */
        inline fun addAddFunction(block: KotlinFunctionSpec.Builder.() -> Unit) {
            addFunction(
                KotlinFunctionSpec.builder("add$firstUpperName").apply {
                    addDoc("Add an element to [$name]\n")
                    modifiers.public()
                    if (declaration.annotationData.open) {
                        modifiers.open()
                    }
                    returns(builderTypeName.ref())
                    block()
                }
            )
        }

        /**
         * 函数已经包含 name，returns
         */
        inline fun addAddAllFunction(block: KotlinFunctionSpec.Builder.() -> Unit) {
            addFunction(
                KotlinFunctionSpec.builder("addAll$firstUpperName").apply {
                    addDoc("Add all elements to [$name].\n")
                    modifiers.public()
                    if (declaration.annotationData.open) {
                        modifiers.open()
                    }
                    returns(builderTypeName.ref())
                    block()
                }
            )
        }

        /**
         * 函数已经包含 name，returns
         */
        inline fun addClearFunction(block: KotlinFunctionSpec.Builder.() -> Unit) {
            addFunction(
                KotlinFunctionSpec.builder("clear$firstUpperName").apply {
                    addDoc("Clear elements of [$name].\n")
                    if (isNullableOrIsOptional) {
                        addDoc("_[$name] Will be set to null internally._\n")
                    }
                    modifiers.public()
                    if (declaration.annotationData.open) {
                        modifiers.open()
                    }
                    returns(builderTypeName.ref())
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
        propertyInitializer: CodeValue,
        elementType: KSType,
    ) : CollectionOrMapFunctionEmitter(
        propertyType,
        propertyInitializer,
        elementType,
    ) {
        override fun emit(queue: ArrayDeque<Emitter>) {
            // add(T)
            // val elementTypeName = elementType.toTypeName(typeParameterResolver)
            val elementTypeName = elementType.toTypeRef()

            addAddFunction {
                addParameter("element", elementTypeName)
                addCode {
                    // addCode("return apply {")
                    // inStatement {
                    //     addCode(accessor)
                    //     addCode(".add(element)")
                    // }
                    // addCode("}")
                    // TODO?
                    inReturnApplyStatement {
                        addCode(accessor)
                        addCode(".add(element)")
                    }
                }
            }

            // addAll(Collection<T>)
            // addAll(vararg T)

            addAddAllFunction {
                // addParameter("elements", COLLECTION.parameterizedBy(elementTypeName))
                addParameter("elements", COLLECTION.parameterized(elementTypeName))
                addCode {
                    // addCode("return apply {")
                    // inStatement {
                    //     addCode(accessor)
                    //     addCode(".addAll(elements)")
                    // }
                    // addCode("}")
                    // TODO
                    inReturnApplyStatement {
                        addCode(accessor)
                        addCode(".addAll(elements)")
                    }
                }
            }

            addAddAllFunction {
                addParameter("elements", elementTypeName) {
                    modifiers.vararg()
                }
                addCode {
                    // addCode("return apply {")
                    // inStatement {
                    //     addCode(accessor)
                    //     addCode(".addAll(elements.%V())") {
                    //         emitName(MemberName(PackageNames.KOTLIN_COLLECTIONS, "asList"))
                    //     }
                    // }
                    // addCode("}")
                    // TODO
                    inReturnApplyStatement {
                        addCode(accessor)
                        addCode(".addAll(elements.%V())") {
                            emitName(MemberName(PackageNames.KOTLIN_COLLECTIONS, "asList"))
                        }
                    }
                }
            }

            // clear()
            addClearFunction {
                val name = this@BuilderProperty.name
                addCode {
                    // addCode("return apply {")
                    // if (isNullableOrIsOptional) {
                    //     addStatement("this.$name = null")
                    // } else {
                    //     addStatement("this.$name.clear()")
                    // }
                    // addCode("}")
                    inReturnApplyBlock {
                        if (isNullableOrIsOptional) {
                            addStatement("this.$name = null")
                        } else {
                            addStatement("this.$name.clear()")
                        }
                    }
                }
            }

            findBuilder(elementType)?.also {
                queue.add(CollectionBuilderExtensionEmitter(it))
            }
        }
    }

    private inner class MapFunctionEmitter(
        propertyType: KSType,
        propertyInitializer: CodeValue,
        val keyType: KSType,
        elementType: KSType,
    ) : CollectionOrMapFunctionEmitter(
        propertyType,
        propertyInitializer,
        elementType,
    ) {
        override fun emit(queue: ArrayDeque<Emitter>) {
            // val keyTypeName = keyType.toTypeName(typeParameterResolver)
            // val valueTypeName = elementType.toTypeName(typeParameterResolver)
            val keyTypeName = keyType.toTypeRef()
            val valueTypeName = elementType.toTypeRef()

            // add(K, V)
            addAddFunction {
                // key
                // value
                addParameter("key", keyTypeName)
                addParameter("value", valueTypeName)
                addCode {
                    // addCode("return apply {")
                    // inStatement {
                    //     addCode(accessor)
                    //     addCode(".put(key, value)")
                    // }
                    // addCode("}")
                    // TODO
                    inReturnApplyStatement {
                        addCode(accessor)
                        addCode(".put(key, value)")
                    }
                }
            }

            // addAll(Map<K, V>)
            addAddAllFunction {
                addParameter("elements", KotlinClassNames.MAP.parameterized(keyTypeName, valueTypeName))
                addCode {
                    // addCode("return apply {")
                    // inStatement {
                    //     addCode(accessor)
                    //     addCode(".putAll(elements)")
                    // }
                    // addCode("}")
                    // TODO
                    inReturnApplyStatement {
                        addCode(accessor)
                        addCode(".putAll(elements)")
                    }
                }
                // addCode(
                //     buildCodeBlock {
                //         inReturnApplyStatement {
                //             add(accessor)
                //             add(".putAll(elements)")
                //         }
                //     }
                // )
            }

            // clear()
            val name = this@BuilderProperty.name
            addClearFunction {
                addCode {
                    // addCode("return apply { ")
                    // if (isNullableOrIsOptional) {
                    //     addStatement("this.$name = null")
                    // } else {
                    //     addStatement("this.$name.clear()")
                    // }
                    // addCode(" }")
                    // TODO
                    inReturnApplyBlock {
                        if (isNullableOrIsOptional) {
                            addStatement("this.$name = null")
                        } else {
                            addStatement("this.$name.clear()")
                        }
                    }
                }

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
                KotlinFunctionSpec.builder(fn).apply {
                    addDoc("@see %V", CodePart.type(otherBuilderName))
                    modifiers.inline()
                    if (declaration.annotationData.internal) {
                        modifiers.internal()
                    } else {
                        modifiers.public()
                    }
                    returns(builderTypeName.ref())
                    receiver(builderTypeName.ref())
                    addTypeVariables(typeParameters)
                    addParameter(
                        "block",
                        KotlinLambdaTypeName {
                            receiver(otherBuilderName.ref())
                        }
                        // KotlinLambdaTypeName.get(
                        //     receiver = otherBuilderName,
                        //     returnType = UNIT
                        // )
                    )
                    addCode("return $fn(%V().also(block).build())", CodePart.type(otherBuilderName))
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
        val keyType: TypeRef<*>,
        val otherBuilderName: TypeName,
    ) : ExtensionEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            val fn = "add$firstUpperName"
            addExtension(
                KotlinFunctionSpec.builder(fn).apply {
                    addDoc("@see %V", CodePart.type(otherBuilderName))
                    modifiers.inline()
                    if (declaration.annotationData.internal) {
                        modifiers.internal()
                    } else {
                        modifiers.public()
                    }
                    returns(builderTypeName.ref())
                    receiver(builderTypeName.ref())
                    addTypeVariables(typeParameters)
                    addParameter("key", keyType)
                    addParameter(
                        "block",
                        KotlinLambdaTypeName {
                            receiver(otherBuilderName.ref())
                        }
                    )
                    addCode("return $fn(key, %V().also(block).build())", CodePart.type(otherBuilderName))
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
                KotlinFunctionSpec.builder(fn).apply {
                    addDoc("@see ${declaration.builderName}.$fn\n")
                    addDoc("@see %V.$name\n", CodePart.type(declaration.type.toClassName()))
                    addDoc("@see %V", CodePart.type(otherBuilderName))
                    modifiers.inline()
                    if (declaration.annotationData.internal) {
                        modifiers.internal()
                    } else {
                        modifiers.public()
                    }
                    returns(builderTypeName.ref())
                    receiver(builderTypeName.ref())
                    addTypeVariables(typeParameters)
                    addParameter(
                        "block",
                        KotlinLambdaTypeName {
                            receiver(otherBuilderName.ref())
                        }
                    )
                    addCode("return $fn(%V().also(block).build())", CodePart.type(otherBuilderName))
                }
            )
        }
    }
}


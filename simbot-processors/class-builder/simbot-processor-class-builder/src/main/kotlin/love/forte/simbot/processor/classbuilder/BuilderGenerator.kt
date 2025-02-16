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

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import love.forte.simbot.processor.classbuilder.annotation.BuilderFor
import kotlin.properties.Delegates


internal class BuilderGenerator(
    val resolver: Resolver,
    val environment: SymbolProcessorEnvironment,
    val declaration: ExpectBuilderDeclaration
) {
    /**
     * 原类型对应生成的Builder的类型的映射。
     */
    private val properties: MutableMap<KSClassDeclaration, ClassName> = mutableMapOf()

    val sources = mutableSetOf<KSFile>()

    fun generate(): FileSpec {
        val type = declaration.type

        type.containingFile?.also { sources.add(it) }

        val typeParameterResolver = type.typeParameters.toTypeParameterResolver()

        val typeSpecBuilder = TypeSpec.classBuilder(declaration.builderName)

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

        val fileSpecBuilder = FileSpec.builder(type.packageName.asString(), declaration.builderName)

        // 找到用于构建的构建函数，转化它的参数
        val parameters =
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

        val properties = type.getAllProperties()
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
                    property
                )
            }


        for (parameterProperty in parameters) {
            parameterProperty.emit()
        }

        for (memberProperty in properties) {
            memberProperty.emit()
        }

        fileSpecBuilder.addType(typeSpecBuilder.build())
        return fileSpecBuilder.build()
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
    val parameter: KSValueParameter,
) : BuilderProperty(resolver, environment, declaration, fileBuilder, typeBuilder, typeParameterResolver, generator) {
    companion object {
        val DelegatesClassName = Delegates::class.asClassName()
    }

    override val name: String = parameter.name?.asString() ?: environment.reportError(
        "Parameter name is null: $parameter",
        parameter
    )

    override val type: KSType = parameter.type.resolve()

    override fun emit() {
        val queue = ArrayDeque<Emitter>()
        queue.add(BuildPropertyEmitter())

        do {
            queue.removeFirst().emit(queue)
        } while (queue.isNotEmpty())
    }

    private sealed class Emitter {
        abstract fun emit(queue: ArrayDeque<Emitter>)
    }

    private sealed class PropertyEmitter : Emitter()
    private sealed class FunctionEmitter : Emitter()
    private sealed class ExtensionEmitter : Emitter()

    private inner class BuildPropertyEmitter : PropertyEmitter() {
        override fun emit(queue: ArrayDeque<Emitter>) {
            // 添加属性。
            // 原始类型如果不是nullable，那么这里就是lateinit
            // TODO 如果是 Array 或原本类型是 vararg，改成List

            when {
                // 基础类型
                type.declaration.isPrimitive(resolver.builtIns) -> queue.add(PrimitivePropertyEmitter())
                // TODO Array, vararg, list, set, collection
                //  需要检测是否也是有 Builder 的

                // TODO 普通类型
                //  需要检测是否也是有 Builder 的

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
                addModifiers(KModifier.PUBLIC)
                mutable(true)
                if (type.isMarkedNullable) {
                    initializer("null")
                } else {
                    // by kotlin.properties.Delegates.notNull()
                    delegate("%T.notNull()", DelegatesClassName)
                }
            }.build()

            typeBuilder.addProperty(propertySpec)

            // TODO add Function emit to queue
        }
    }

    /**
     * 属性类型是普通的obj类型
     */
    private fun emitSimpleTypeProperty(fileBuilder: FileSpec.Builder) {
        TODO()
    }

    /**
     * 属性类型是集合类型，可以是List，Set，Collection或对应的可变类型。
     * 原类型也可以是 Array 或 vararg 类型。
     *
     * 如果原类型不为 null，初始化不可为 null 的对应可变类型，vararg 也是不可null
     * 如果原类型为 null，初始化为 null，但是 add method 里增加初始化函数。
     */
    private fun emitCollectionProperty(fileBuilder: FileSpec.Builder) {
        TODO()
    }

    /**
     * 属性类型是Map类型
     */
    private fun emitMapProperty(fileBuilder: FileSpec.Builder) {
        TODO()
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
    val property: KSPropertyDeclaration
) : BuilderProperty(resolver, environment, declaration, fileBuilder, typeBuilder, typeParameterResolver, generator) {
    override val name: String = property.simpleName.asString()
    override val type: KSType = property.type.resolve()

    override fun emit() {
        // TODO("Not yet implemented")
    }
}

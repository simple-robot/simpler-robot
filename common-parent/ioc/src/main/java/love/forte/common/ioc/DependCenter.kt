/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     DependCenter.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.ioc

import love.forte.common.annotation.Ignore
import love.forte.common.configuration.Configuration
import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.Constr
import love.forte.common.ioc.annotation.Depend
import love.forte.common.ioc.exception.DuplicateDependNameException
import love.forte.common.ioc.exception.IllegalConstrException
import love.forte.common.ioc.exception.IllegalTypeException
import love.forte.common.ioc.exception.NotBeansException
import love.forte.common.utils.FieldUtil
import love.forte.common.utils.annotation.AnnotationUtil
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.javaSetter
import kotlin.reflect.jvm.kotlinProperty

/**
 *
 * 依赖管理中心。
 *
 * ```
 * before inject ---> bean -- [inject intercept] --> inject
 * ```
 *
 *
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 *
 * @param singletonMap 保存单例的map。在put的时候会有同步锁，所以应该不需要线程安全的Map.
 * @param nameResourceWarehouse 保存依赖的map。以name为key, 对应着唯一的值。
 * @param typeResourceWarehouse 保存依赖的map。以type为key, 可以存在多个相同的类型，但是最终只会留下一个。
 * @param parent 依赖中心的父类依赖。
 */
public class DependCenter
@JvmOverloads
constructor(
    private val singletonMap: MutableMap<String, Any> = ConcurrentHashMap(),
    private val nameResourceWarehouse: MutableMap<String, BeanDepend<*>> = ConcurrentHashMap<String, BeanDepend<*>>(),
    private val typeResourceWarehouse: MutableMap<Class<*>, Deque<BeanDepend<*>>> = ConcurrentHashMap<Class<*>, Deque<BeanDepend<*>>>(),
    private var parent: DependBeanFactory? = null,
    private val configs: Configuration? = null // auto config able.
) : BeanDependRegistry {

    private val needInitialized: MutableList<BeanDepend<*>> = mutableListOf()

    /**
     * 根据旧parent得到一个新的parent
     */
    public fun mergeParent(merge: (DependBeanFactory?) -> DependBeanFactory?) {
        parent = merge(parent)
    }

    /**
     * 解析注解并注入依赖。 优先使用类上注解, 如果没有则使用提供的额外注解。 如果最终都没有, 抛出异常。
     *
     * @param defaultAnnotation 如果class有的class不存在bean, 可以提供一个默认的注解实例来代替为这个class的注解。
     * @param type 要注入的类型。
     *
     * @throws NotBeansException 如果类上、方法上都找不到[Beans]相关注解，抛出此异常。
     */
    @JvmOverloads
    public fun <T> inject(defaultAnnotation: Beans? = defaultBeansAnnotation, type: Class<out T>) {
        // 自动解析的情况下, target type 不可以是抽象类型或者接口理类型
        if (type.isInterface || Modifier.isAbstract(type.modifiers)) {
            throw IllegalTypeException("$type cannot be interface or abstract.")
        }

        // 得到beans注解
        val beansAnnotation: Beans =
            AnnotationUtil.getAnnotation(type, Beans::class.java) ?: defaultAnnotation ?: throw NotBeansException(
                type.toGenericString()
            )

        // builder
        val builder = BeanDependBuilder<T>()

        // bean name
        val beanDependName: String = beansAnnotation.value.let { if (it.isBlank()) null else it }
            ?: type.dependName

        builder.name(beanDependName)
        builder.type(type)
        builder.single(beansAnnotation.single)
        builder.priority(beansAnnotation.priority)

        // 实例构建函数
        val emptyInstanceFunc: () -> T = classToEmptyInstanceSupplier(type)

        // 值注入函数
        val instanceInject: (T, DependBeanFactory) -> T = instanceInjectFunc(beansAnnotation, type)

        // 完整实例构建函数
        val instanceSupplier: InstanceSupplier<T> = InstanceSupplier { fac ->
            instanceInject(emptyInstanceFunc(), fac)
        }
        builder.instanceSupplier(instanceSupplier)

        // Register a beanDepend.
        register(builder.build())


        // children
        // 只允许非static的public方法
        type.methods.filter { AnnotationUtil.containsAnnotation(it, Beans::class.java) }

    }

    /**
     * 根据一个Class，解析并得到这个class的实例化函数。
     */
    private fun <T> classToEmptyInstanceSupplier(type: Class<out T>): () -> T {
        val constrFuncs = type.declaredMethods.filter {
            val modifiers = it.modifiers
            // static, and no params. with @Constr
            Modifier.isStatic(modifiers) && it.parameterCount == 0 && (AnnotationUtil.getAnnotation(
                it,
                Constr::class.java
            ) != null)
        }

        when {
            // more @Constr
            constrFuncs.size > 1 -> {
                throw IllegalConstrException("More than 1 static method annotated by @Constr, but only need 1.")
            }
            // no @Constr
            constrFuncs.isEmpty() -> {
                val constructors = type.constructors
                if (constructors.size == 1) {
                    val firstConstr = constructors.first()
                    if (firstConstr.parameterCount > 0) {
                        throw IllegalConstrException("constructor's parameterCount > 0")
                    } else {
                        return { firstConstr.newInstance() as T }
                    }
                } else {
                    // more than 1 constructors. find @Constr.
                    val constrListWithAnnotation = constructors.filter {
                        AnnotationUtil.getAnnotation(it, Constr::class.java) != null
                    }
                    when {
                        // more than 1 with @Constr.
                        constrListWithAnnotation.size > 1 -> throw IllegalConstrException("More than 1 constructor method annotated by @Constr, but only need 1.")
                        // nothing.
                        constrListWithAnnotation.isEmpty() -> {
                            val constr = type.getConstructor()
                            return { constr.newInstance() }
                        }
                        // only one.
                        else -> {
                            val firstConstr = constructors.first()
                            return { firstConstr.newInstance() as T }
                        }
                    }
                }
            }
            // one @Constr
            else -> {
                val constrFunc = constrFuncs.first().apply { isAccessible = true }
                return { constrFunc.invoke(null) as T }
            }
        }
    }

    /**
     * 类中标注了@Beans的方法.
     */
    private fun <T> childMethodToEmptyInstanceSupplier(parentType: Class<*>, childMethod: Method): () -> T {
        val beans: Beans = AnnotationUtil.getAnnotation(childMethod, Beans::class.java)


        TODO()
    }


    /**
     * 根据一个实例注入参数的函数.
     * 寻找其中全部标注了[Depend]的字段
     */
    private fun <T> instanceInjectFunc(beans: Beans, type: Class<out T>): (T, DependBeanFactory) -> T {
        val depends: MutableList<Field> = FieldUtil.getDeclaredFields(type, {
            // 字段不可以是静态的、不可以是final的
            val modifiers: Int = it.modifiers
            if (Modifier.isFinal(modifiers)) {
                throw IllegalTypeException("@Depend cannot be annotated in final field. but annotated in $it from $type")
            }
            if (Modifier.isStatic(modifiers)) {
                throw IllegalTypeException("@Depend cannot be annotated in static field. but annotated in $it from $type")
            }
            if (beans.allDepend) {
                // 全部都注入, 则忽略部分需要忽略的
                !AnnotationUtil.containsAnnotation(it, Ignore::class.java)
            } else {
                // 否则, 注入需要注入的
                AnnotationUtil.containsAnnotation(it, Depend::class.java)
            }
        }, true)

        return if (depends.isEmpty()) {
            // no depends.
            { b, _ -> b }
        } else {
            // contain depends.
            val funcs: List<(T, DependBeanFactory) -> Unit> = depends.map {
                it.isAccessible = true

                // do inject if can
                val dependAnnotation: Depend = AnnotationUtil.getAnnotation(it, Depend::class.java) ?: beans.depend

                val orNull: Boolean = dependAnnotation.orIgnore

                val dependName: String = dependAnnotation.value

                // 获取字段实例的方法
                val instance: (DependBeanFactory) -> Any? = if (dependName.isBlank()) {
                    // by type
                    val dependType: Class<*> = dependAnnotation.type.java
                    val needType: Class<*> = if (dependType != Void::class.java) {
                        dependType
                    } else {
                        it.type
                    }
                    if (orNull) { d ->
                        d.get(needType)
                    } else { d ->
                        d.getOrNull(needType)
                    }
                } else {
                    if (orNull) { d ->
                        d.get(dependName)
                    } else { d ->
                        d.getOrNull(dependName)
                    }
                }

                // 通过field进行赋值
                fun byField(): (T, Any?) -> Unit = { b, v -> v?.apply { it.set(b, this) } }

                // 尝试通过setter赋值
                fun bySetter(): ((T, Any?) -> Unit)? {
                    val setterName: String = dependAnnotation.setterName
                    val paramsType: Class<*> =
                        dependAnnotation.setterParams.java.let { sp -> if (sp == Void::class.java) null else sp }
                            ?: it.type
                    if (setterName.isNotBlank()) {
                        val setter: Method = type.getMethod(setterName, paramsType)
                        return { b, v -> v?.apply { setter(b, this) } }
                    } else {
                        val ktProp: KMutableProperty<*> = it.kotlinProperty?.let { kp ->
                            if (kp is KMutableProperty) kp else null
                        } ?: return null

                        return ktProp.javaSetter?.let { setter ->
                            { b, v -> v?.apply { setter(b, this) } }
                        }
                    }

                }

                // 赋值函数, 提供一个载体与值，进行赋值
                val injector: (T, Any?) -> Unit = if (dependAnnotation.bySetter) {
                    // 优先通过setter获取, 得不到才用字段
                    bySetter() ?: byField()
                } else {
                    // 通过字段
                    byField()
                }

                { ins, fac ->
                    // 获取实例
                    val fieldValue: Any? = instance(fac)
                    // 注入
                    injector(ins, fieldValue)
                }
            }
            // return func
            ({ b, fac ->
                funcs.forEach { it(b, fac) }
                b
            })
        }
    }


    /**
     * 注册一个beanDepend. 直接注册
     *
     * @throws IllegalArgumentException 如果name出现重复, 则可能抛出此异常
     */
    override fun register(beanDepend: BeanDepend<*>) {
        val name: String = beanDepend.name
        nameResourceWarehouse.merge(name, beanDepend, mergeDuplicate(name))
        val type: Class<*> = beanDepend.type
        val deque: Deque<BeanDepend<*>> = typeResourceWarehouse.computeIfAbsent(type) { LinkedList() }
        deque.addLast(beanDepend)
    }

    /**
     * 注册一个type. 解析注解后注册
     */
    override fun register(type: Class<*>) {
        inject(null, type)
    }


    companion object {
        private val defaultBeansAnnotation: Beans = AnnotationUtil.getDefaultAnnotationProxy(Beans::class.java)
    }
}

/**
 * 如果出现重复的name，抛出异常。
 */
internal fun <V> mergeDuplicate(name: String): BiFunction<in V, in V, out V> {
    return BiFunction { _, _ -> throw DuplicateDependNameException(name) }
}


/**
 * 根据类型得到依赖名称。
 * canonicalName ?: typeName
 */
internal val Class<*>.dependName: String
    get() {
        return this.canonicalName ?: this.typeName
    }

/**
 * 根据Method得到依赖名称。
 * declaringClass.propertyName
 */
internal val Method.dependName: String
    get() {
        val name: String = this.name
        return if (name.length > 3 && name.startsWith("get") && name[3].isUpperCase()) {
            val propertyName: String = name[3].toLowerCase() + name.substring(4)
            // class name + . + name
            "$declaringClass.$propertyName"
        } else {
            "$declaringClass.$name"
        }

    }



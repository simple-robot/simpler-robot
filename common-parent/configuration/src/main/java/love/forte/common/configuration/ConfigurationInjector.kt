/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConfigurationInjectorImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.configuration.annotation.ConfigIgnore
import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.configuration.exception.ConfigurationInjectException
import love.forte.common.utils.annotation.getAnnotation
import love.forte.common.utils.convert.ConverterManager
import love.forte.common.utils.convert.HutoolConverterManagerBuilderImpl
import java.lang.reflect.Type
import kotlin.reflect.*
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.*

/**
 * inline class for config target, maybe Field or Method
 */
private inline class ConfigTarget<T>(val member: KCallable<T>) {
    fun setValue(target: Any?, arg: Any?) {
        member.let {
            if (member is KMutableProperty) {
                member.isAccessible = true
                member.setter.call(target, arg)
            } else {
                if (arg != null) member.call(target, arg) else member.call(target)
            }
        }
    }

    /** 得到要注入的类型 */
    val valueType: Type
        get() {
            return if (member is KFunction) {
                member.parameters[1].type.javaType
            } else {
                member.returnType.javaType
            }
        }

}

/**
 *
 * 配置信息注入实现, 单例工具类
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
object ConfigurationInjector {
    /**
     * 向一个配置类实例中注入配置信息。
     *
     * @see AsConfig
     * @see ConfigInject
     * @see ConfigIgnore
     *
     * @param configInstance 配置类实例
     * @param configuration 配置信息
     *
     * @throws love.forte.common.configuration.exception.ConfigurationInjectException 如果注入的时候出现意外，则可能抛出此异常。
     */
    @JvmOverloads
    public fun <T> inject(configInstance: T, configuration: Configuration, converterManager: ConverterManager? = null): T {
        val manager: ConverterManager = converterManager ?: HutoolConverterManagerBuilderImpl().build()

        val configClass: KClass<*> = configInstance!!::class
        // get data
        val asConfigData: AsConfigData = getAnnotation(configClass, AsConfig::class).toData()

        // 同样寻找父类
        val seq: Sequence<KCallable<*>> = if (asConfigData.deep) {
            configClass.allDeclaredMembers
        } else configClass.declaredMembers.asSequence()

        // all members can inject.
        val members: List<InjectCallable<*>> = seq.run {
            filter {
                when (it) {
                    is KFunction -> {
                        // 如果是一个函数，则必须有且仅有一个参数
                        it.visibility.isPublic() && it.parameters.size == 1
                    }
                    // 如果是属性，不能有ignore注解
                    is KProperty -> getAnnotation(it, ConfigIgnore::class) == null
                    // 其他的不管
                    else -> false
                }
            }.run {
                // 如果要读取所有的属性，则不做过滤，否则只过滤有注解的值。
                if (!asConfigData.allField) filter {
                    getAnnotation(it, ConfigInject::class) != null
                } else filter {
                    // 非注解下只会扫描property
                    it is KMutableProperty || getAnnotation(it, ConfigInject::class) != null
                }
            }
        }.map { InjectCallable(ConfigTarget(it), asConfigData, manager) }.toList()

        members.forEach {
            it.setValue(configInstance, configuration)
        }

        return configInstance
    }
}


private data class InjectCallable<out T>(
    private val configTarget: ConfigTarget<T>,
    private val configData: AsConfigData,
    private val manager: ConverterManager
) : KCallable<T> by configTarget.member {
    /**
     * config inject data
     */
    private val configInjectData: ConfigInjectData = getAnnotation(configTarget.member, ConfigInject::class).toData()

    val prefix: String? get() = configData.prefix
    val suffix: String? get() = configData.suffix

    /**
     * 此配置项的name
     */
    val configName: String = configInjectData.value ?: configTarget.member.propName

    /**
     * 得到 对应的config key
     */
    val configKey: String = run {
        val sb = StringBuilder()
        if (prefix != null && !configInjectData.ignorePrefix) {
            sb.append(prefix).append('.')
        }
        sb.append(configName)
        if (suffix != null && !configInjectData.ignoreSuffix) {
            sb.append('.').append(suffix)
        }
        sb.toString()
    }

    /**
     * 设置值
     */
    fun setValue(target: Any?, configuration: Configuration) {
        val config: ConfigurationProperty? = configuration.getConfig(configKey)
        val value: Any? = when {
            config != null -> {
                config.getObject<T>(configTarget.valueType)
            }
            configInjectData.orNull -> {
                null
            }
            else -> {
                val javaTypeShow: String = when (configTarget.member) {
                    is KFunction -> configTarget.member.javaMethod?.toString() ?: configTarget.member.toString()
                    is KProperty -> configTarget.member.javaField?.toString()
                        ?: configTarget.member.javaGetter?.toString() ?: configTarget.member.toString()
                    else -> configTarget.member.toString()
                }
                throw ConfigurationInjectException("cannot found config '$configKey' for $javaTypeShow")
            }
        }
        configTarget.setValue(target, value)
    }

    override fun toString(): String {
        return "$configKey@$configTarget"
    }

}


private val KCallable<*>.propName: String
    get() {
        val n = this.name
        return if (this is KFunction && n.startsWith("set")) {
            n[3].toLowerCase() + n.substring(4)
        } else n
    }


/**
 * 得到包括父类在内的所有member
 */
private val KClass<*>.allDeclaredMembers: Sequence<KCallable<*>>
    get() = (this.declaredMembers.asSequence() + this.superclasses.asSequence()
        .flatMap { it.declaredMembers }).distinct()

/**
 * 判断是否为public
 */
private fun KVisibility?.isPublic(): Boolean = this?.let { it == KVisibility.PUBLIC } ?: false


private fun AsConfig?.toData(): AsConfigData = AsConfigData(this)
private fun ConfigInject?.toData(): ConfigInjectData = ConfigInjectData(this)


/**
 * [ConfigInject] 数据封装实例
 */
private inline class ConfigInjectData(val configInject: ConfigInject?) {

    /**
     * 当前配置名称的键。例如：'user.name'。如果不填则默认通过字段名或者方法名判断名称。
     */
    val value: String? get() = configInject?.value?.let { if (it.isNotBlank()) it else null }

    /**
     * 如果有，忽略类上的前缀。
     */
    val ignorePrefix: Boolean get() = configInject?.ignorePrefix ?: false

    /**
     * 如果有，忽略类上的后缀。
     */
    val ignoreSuffix: Boolean get() = configInject?.ignoreSuffix ?: false

    /**
     * 优先通过 setter 注入配置值。
     *
     * 只有当标注在字段上时才会生效。
     *
     * 如果为true，则会尝试获取类中 `set + fieldName大写(field类型)` 的方法。
     * 如果找不到则会继续使用字段注入。
     *
     */
    val bySetter: Boolean get() = configInject?.bySetter ?: true


    /**
     * 如果找不到对应的配置，则注入null。默认会抛出异常。
     */
    val orNull: Boolean get() = configInject?.orNull ?: false
}


/**
 * [AsConfig] 数据封装实例
 */
private inline class AsConfigData(val asConfig: AsConfig?) {
    /** 前缀，默认为空  */
    val prefix: String? get() = asConfig?.prefix?.let { if (it.isNotBlank()) it else null }

    /** 后缀，默认为空  */
    val suffix: String? get() = asConfig?.suffix?.let { if (it.isNotBlank()) it else null }

    /**
     * 如果在配置类上标注此注解且此参数为 `true`,
     * 则没有标记 [ConfigInject] 的字段也会被默认作为配置字段而添加。
     */
    val allField: Boolean get() = asConfig?.allField ?: false

    /**
     * 进行深层注入，即会扫描父类的字段。 默认false
     */
    val deep: Boolean get() = asConfig?.deep ?: false
}

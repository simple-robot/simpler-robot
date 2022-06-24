package love.forte.simboot.core.utils

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.instanceParameter


/**
 * 构建一个此监听函数的签名。一般类似于全限定名。
 */
public fun KFunction<*>.sign(): String {
    return buildString {
        instanceParameter?.type?.also {
            append(it.toString())
            append('.')
        }
        append(name)
        val pms = parameters.filter { it.kind != KParameter.Kind.INSTANCE }
        if (pms.isNotEmpty()) {
            append('(')
            pms.joinTo(this, separator = ",") {
                it.type.toString()
            }
            append(')')
        }
    }
}
/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

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

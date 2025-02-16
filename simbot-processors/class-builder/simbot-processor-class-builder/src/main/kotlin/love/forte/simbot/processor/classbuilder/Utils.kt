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

import com.google.devtools.ksp.processing.KSBuiltIns
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSNode

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

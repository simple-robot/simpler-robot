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

import java.lang.reflect.Executable
import java.lang.reflect.Modifier


internal inline val Class<*>.isPublic get() = Modifier.isPublic(modifiers)
internal inline val Class<*>.isProtected get() = Modifier.isProtected(modifiers)
internal inline val Class<*>.isPrivate get() = Modifier.isPrivate(modifiers)
internal inline val Class<*>.isFinal get() = Modifier.isFinal(modifiers)
internal inline val Class<*>.isAbstract get() = Modifier.isAbstract(modifiers)
internal inline val Class<*>.isStatic get() = Modifier.isStatic(modifiers)


internal inline val Executable.isPublic get() = Modifier.isPublic(modifiers)
internal inline val Executable.isProtected get() = Modifier.isProtected(modifiers)
internal inline val Executable.isPrivate get() = Modifier.isPrivate(modifiers)
internal inline val Executable.isFinal get() = Modifier.isFinal(modifiers)
internal inline val Executable.isAbstract get() = Modifier.isAbstract(modifiers)
internal inline val Executable.isStatic get() = Modifier.isStatic(modifiers)
internal inline val Executable.isSynchronized get() = Modifier.isSynchronized(modifiers)

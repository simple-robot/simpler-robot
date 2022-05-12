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

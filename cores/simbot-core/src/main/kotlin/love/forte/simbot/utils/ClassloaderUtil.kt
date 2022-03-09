package love.forte.simbot.utils


internal inline val Any.currentClassLoader: ClassLoader
    get() =
        javaClass.classLoader
            ?: Thread.currentThread().contextClassLoader
            ?: ClassLoader.getSystemClassLoader()
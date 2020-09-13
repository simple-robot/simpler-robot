package org.slf4j.impl

import org.slf4j.ILoggerFactory

/**
 *
 * common simple logger.
 *
 * neko logger~
 *
 */
object StaticLoggerBinder {
    val loggerFactory: ILoggerFactory by lazy(LazyThreadSafetyMode.NONE) {


        try {
            // contains Language.
            Class.forName("love.forte.common.language.Language")
        }catch (ignore: Exception){
            // no Language.
        }

        TODO()
    }
    @JvmStatic
    fun getSingleton() = this
}
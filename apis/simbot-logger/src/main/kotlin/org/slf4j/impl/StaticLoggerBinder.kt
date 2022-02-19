package org.slf4j.impl

import love.forte.simbot.logger.SimbotLoggerFactory
import org.slf4j.ILoggerFactory
import org.slf4j.helpers.Util
import org.slf4j.spi.LoggerFactoryBinder

/**
 * slf4j-api 的logger绑定器。
 *
 */
public object StaticLoggerBinder : LoggerFactoryBinder {
    private lateinit var theBinder: LoggerFactoryBinder

    /**
     * 获取当前实例.
     */
    @JvmStatic
    public fun getSingleton(): StaticLoggerBinder = this


    private fun getTheBinder(): LoggerFactoryBinder {
        if (::theBinder.isInitialized) {
            return theBinder
        }

        synchronized(this) {
            if (::theBinder.isInitialized) {
                return theBinder
            }


            TODO()
        }
    }

    /**
     * Return the instance of [ILoggerFactory] that [org.slf4j.LoggerFactory] class should bind to.
     *
     * @return the instance of [ILoggerFactory] that [org.slf4j.LoggerFactory] class should bind to.
     */
    override fun getLoggerFactory(): ILoggerFactory = getTheBinder().loggerFactory

    /**
     * The String form of the [ILoggerFactory] object that this
     * `LoggerFactoryBinder` instance is *intended* to return.
     *
     *
     * This method allows the developer to interrogate this binder's intention
     * which may be different from the [ILoggerFactory] instance it is able to
     * yield in practice. The discrepancy should only occur in case of errors.
     *
     * @return the class name of the intended [ILoggerFactory] instance
     */
    override fun getLoggerFactoryClassStr(): String = getTheBinder().loggerFactoryClassStr


}


private object SimbotLoggerStaticLoggerBinder : LoggerFactoryBinder {
    private const val LOGGER_FACTORY_CLASS_STR = "love.forte.simbot.logger.SimbotLoggerFactory"
    override fun getLoggerFactory(): ILoggerFactory = SimbotLoggerFactory
    override fun getLoggerFactoryClassStr(): String = LOGGER_FACTORY_CLASS_STR
}

/**
 * 类似于 [Util.report].
 */
@Suppress("SameParameterValue")
private fun report(msg: String) {
    System.err.println("SIMBOT-LOGGER: $msg")
}

/**
 * 类似于 [Util.report].
 */
@Suppress("SameParameterValue")
private fun report(msg: String, t: Throwable) {
    System.err.println("SIMBOT-LOGGER: $msg")
    System.err.println("SIMBOT-LOGGER: Reported exception: ")
    t.printStackTrace(System.err)
}


/*
    尝试寻找所有已知的LoggerFactory实例。
 */

private fun findAnyILogger() {
    // LoggerFactoryBinder
    // Log4jLoggerFactory() // log4j
    // LoggerFactory
}

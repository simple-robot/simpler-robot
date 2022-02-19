package love.forte.simbot.logger

import org.slf4j.ILoggerFactory
import org.slf4j.Logger

public object SimbotLoggerFactory : ILoggerFactory {

    /**
     * Return an appropriate [Logger] instance as specified by the
     * `name` parameter.
     *
     *
     * If the name parameter is equal to [Logger.ROOT_LOGGER_NAME], that is
     * the string value "ROOT" (case insensitive), then the root logger of the
     * underlying logging system is returned.
     *
     *
     * Null-valued name arguments are considered invalid.
     *
     *
     * Certain extremely simple logging systems, e.g. NOP, may always
     * return the same logger instance regardless of the requested name.
     *
     * @param name the name of the Logger to return
     * @return a Logger instance
     */
    override fun getLogger(name: String?): Logger {
        // TODO("Not yet implemented")
        return SimbotLogger()
    }
}
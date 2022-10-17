package love.forte.simbot.logger


/**
 *
 * @author ForteScarlet
 */
public class ConsoleLogger(
    private val name: String,
    private val displayName: String,
    private val level: LogLevel
) : Logger {
    override fun getName(): String {
        TODO("Not yet implemented")
    }
    
    override fun isTraceEnabled(): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun trace(log: String) {
        TODO("Not yet implemented")
    }
    
    override fun trace(log: String, vararg arg: Any?) {
        TODO("Not yet implemented")
    }
    
    override fun isDebugEnabled(): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun debug(log: String) {
        TODO("Not yet implemented")
    }
    
    override fun debug(log: String, vararg arg: Any?) {
        TODO("Not yet implemented")
    }
    
    override fun isInfoEnabled(): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun info(log: String) {
        TODO("Not yet implemented")
    }
    
    override fun info(log: String, vararg arg: Any?) {
        TODO("Not yet implemented")
    }
    
    override fun isWarnEnabled(): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun warn(log: String) {
        TODO("Not yet implemented")
    }
    
    override fun warn(log: String, vararg arg: Any?) {
        TODO("Not yet implemented")
    }
    
    override fun isErrorEnabled(): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun error(log: String) {
        TODO("Not yet implemented")
    }
    
    override fun error(log: String, vararg arg: Any?) {
        TODO("Not yet implemented")
    }
}
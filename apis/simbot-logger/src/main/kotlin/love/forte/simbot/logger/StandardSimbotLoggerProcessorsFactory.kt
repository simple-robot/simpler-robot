package love.forte.simbot.logger

import org.slf4j.event.Level

public object StandardSimbotLoggerProcessorsFactory : SimbotLoggerProcessorsFactory {
    private val processors = listOf(
        ConsoleSimbotLoggerProcessor(Level.INFO)
    )

    override fun getProcessors(): List<SimbotLoggerProcessor> {
        return processors
    }
}
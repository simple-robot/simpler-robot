package love.forte.simbot.core.event

import love.forte.simbot.*

@DslMarker
internal annotation class EventListenersGeneratorDSL

/**
 *
 * @author ForteScarlet
 */
public class EventListenersGenerator @InternalSimbotApi constructor(private val configuration: CoreListenerManagerConfiguration) {



    /**
     * 回到配置主类.
     */
    public fun end(): CoreListenerManagerConfiguration = configuration

}
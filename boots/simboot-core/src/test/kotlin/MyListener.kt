import love.forte.di.annotation.Beans
import love.forte.simboot.annotation.Listener
import love.forte.simbot.event.MessageEvent

/**
 *
 * @author ForteScarlet
 */
@Beans
class MyListener {

    @Listener
    fun MessageEvent.listener() {

    }

}
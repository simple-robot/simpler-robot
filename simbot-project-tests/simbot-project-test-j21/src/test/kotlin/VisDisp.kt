import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import love.forte.simbot.utils.CustomBlockingDispatcherProvider
import java.util.concurrent.Executors

/**
 *
 * @author ForteScarlet
 */
class VisDisp : CustomBlockingDispatcherProvider() {
    override fun blockingDispatcher(): CoroutineDispatcher {
        return Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
    }

}

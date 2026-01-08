import love.forte.simbot.common.id.ID
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import java.util.concurrent.CompletableFuture
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class QGBotSuspendTransFunctionTests {

    @Test
    fun checkQGBotSTFunctions() {
        /*
            @ST
            public suspend fun sendTo(channelId: ID, text: String): QGMessageReceipt
         */
        with(QGBot::class.java.getMethod("sendToBlocking", ID::class.java, String::class.java)) {
            assertEquals(QGMessageReceipt::class.java, returnType)
        }
        with(QGBot::class.java.getMethod("sendToAsync", ID::class.java, String::class.java)) {
            assertEquals(CompletableFuture::class.java, returnType)
        }
        with(QGBot::class.java.getMethod("sendToReserve", ID::class.java, String::class.java)) {
            assertEquals(SuspendReserve::class.java, returnType)
        }
    }

}

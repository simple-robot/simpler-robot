import io.ktor.client.engine.mock.*
import love.forte.simbot.qguild.event.GuildCreate
import love.forte.simbot.qguild.stdlib.Bot
import love.forte.simbot.qguild.stdlib.ConfigurableBotConfiguration
import love.forte.simbot.qguild.stdlib.internal.BotImpl
import love.forte.simbot.qguild.stdlib.subscribe
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class BotSubscribeRegisterTest {

    /**
     * 确保普通的 subscribe 和同名扩展函数
     * 不会产生冲突。
     */
    @Test
    fun subscribeTest() {
        val bot = BotImpl(
            Bot.Ticket("", "", ""),
            ConfigurableBotConfiguration().apply {
                apiClientEngine = MockEngine { respondOk() }
                wsClientEngine = MockEngine { respondOk() }
            }
        )

        bot.subscribe {
        }

        bot.subscribe<GuildCreate> {
        }
    }

}

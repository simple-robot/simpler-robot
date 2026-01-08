import love.forte.simbot.component.qguild.event.QGAtMessageCreateEvent
import love.forte.simbot.component.qguild.qqGuildBots
import love.forte.simbot.component.qguild.useQQGuild
import love.forte.simbot.core.application.createSimpleApplication
import love.forte.simbot.core.event.listeners
import love.forte.simbot.message.Image.Key.toImage
import love.forte.simbot.message.Text
import love.forte.simbot.message.plus
import love.forte.simbot.resources.Resource.Companion.toResource
import kotlin.io.path.Path

suspend fun main() {
    val app = createSimpleApplication {
        useQQGuild()
    }

    app.eventListenerManager.listeners {
        QGAtMessageCreateEvent { event ->
            event.reply(Text { "1:hi\nhello!" })
            event.reply(Text { "2:hi\nhello!" } +
                    Path("/Users/forte/Desktop/DT/6C0E80A3247FF69F8F99D55C122A1181.jpg").toResource().toImage())
            event.reply(Path("/Users/forte/Desktop/DT/6C0E80A3247FF69F8F99D55C122A1181.jpg").toResource().toImage())

        }
    }

    app.qqGuildBots {
        register("101986850", "972f64f7c426096f9344b74ba85102fb", "g57N4WsHHRIx1udptqy7GBAEVsfLgynq") {
            botConfig {
                useSandboxServerUrl()
            }
        }.start()
    }


    app.join()
}

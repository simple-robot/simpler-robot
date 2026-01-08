import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import love.forte.simbot.application.listeners
import love.forte.simbot.component.qguild.event.QGGroupAtMessageCreateEvent
import love.forte.simbot.component.qguild.qqGuildBots
import love.forte.simbot.component.qguild.useQQGuild
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.event.process
import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.Intents

fun main() = runBlocking(Dispatchers.Default) {
    val app = launchSimpleApplication {
        eventDispatcher {
            coroutineContext += Dispatchers.Default
        }

        useQQGuild()
    }
    // 订阅事件
    app.listeners {
        // 群AT事件
        process<QGGroupAtMessageCreateEvent> { event ->
            // this: EventListenerContext
            // event: QGGroupAtMessageCreateEvent
            TODO()
        }
        // ...
    }

    // 注册bot
    app.qqGuildBots {
        val bot = register("appId", "secret", "token") {
            // bot 的配置
            botConfig {
                // 比如追加订阅跟群相关的事件
                intents += EventIntents.GroupAndC2CEvent.intents
                // 测试阶段记得使用沙箱URL
                useSandboxServerUrl()
//                wsClientEngineFactory = TODO() // 比如 Curl
//                apiClientEngineFactory = TODO() // 比如 Curl
                // 其他的配置...
            }
            // 组件额外的提供的其他配置, 可选

        }

        // 启动 bot
        bot.start()
        // 也可以 join bot
    }


    app.join()
}

//import io.ktor.client.engine.darwin.*
//import kotlinx.coroutines.test.runTest
//import love.forte.simbot.qguild.BotFactory
//import love.forte.simbot.qguild.api.user.GetBotGuildListApi
//import love.forte.simbot.qguild.api.user.createFlow
//import love.forte.simbot.qguild.requestBy
//
//class Sample {
//    
//    fun a() = runTest() {
//        println("start.")
//
//        val bot =
//            BotFactory.create("101986850", "972f64f7c426096f9344b74ba85102fb", "g57N4WsHHRIx1udptqy7GBAEVsfLgynq") {
//                useSandboxServerUrl()
//
//                apiClientEngine = Darwin.create {
//                }
//
//                wsClientEngine = Darwin.create {
//                }
//            }
//
//        println("Bot: $bot")
//
//        GetBotGuildListApi.createFlow { requestBy(bot) }.collect {
//            println(it)
//        }
//
//        println("Request down")
//
////        bot.registerProcessor<Signal.Dispatch> {
////            println("event: $this")
////            println("raw  : $it")
////        }
////
////        bot.registerProcessor<AtMessageCreate> {
////            println(data.content)
////            if (data.content.trim().startsWith("喵")) {
////                MessageSendApi.create(data.channelId) {
////                    this.msgId = data.id
////                    content = "你刚刚是不是说了... " + data.content + " ?"
////                }.requestBy(bot)
////            }
////        }
////
////        bot.start()
////
////        println("started")
////
////        bot.join()
//    }
//}

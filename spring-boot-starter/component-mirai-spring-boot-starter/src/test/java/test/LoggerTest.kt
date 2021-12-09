package test

import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.utils.runBIO
import org.slf4j.LoggerFactory


// fun main() {
//     val logger = LoggerFactory.getLogger("1")
//
//
//
//
//
//     runCatching {
//         runBlocking {
//             runInterruptible {
//                 runBlocking {
//                     BotFactory.newBot(1, "1").login()
//                 }
//             }
//         }
//     }.getOrElse {
//         logger.error("err", it)
//     }
// }
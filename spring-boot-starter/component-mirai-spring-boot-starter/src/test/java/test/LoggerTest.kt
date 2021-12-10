// package test
//
// import kotlinx.coroutines.*
// import kotlinx.coroutines.future.asCompletableFuture
// import net.mamoe.mirai.BotFactory
// import org.slf4j.LoggerFactory
//
//
// fun main() {
//     val logger = LoggerFactory.getLogger("1")
//
//     val scope = CoroutineScope(Dispatchers.Default)
//
//
//
//
//     runCatching {
//         val future = scope.async {
//             BotFactory.newBot(1, "1").login()
//         }.asCompletableFuture()
//
//         future.join()
//         future.get()
//
//     }.getOrElse {
//         logger.error("err", it)
//     }
// }
import cn.hutool.core.lang.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

val scope1 = CoroutineScope(Executors.newFixedThreadPool(4).asCoroutineDispatcher())
val scope2 = CoroutineScope(Executors.newFixedThreadPool(4).asCoroutineDispatcher())

suspend fun main() {

    val c = Channel<String>(500) {
        println("Undelivered element: $it")
    }
    val sender: SendChannel<String> = c
    val receiver: ReceiveChannel<String> = c

    scope1.launch {
        while(true) {
            sender.send(UUID.fastUUID().toString())
            delay(1000)
        }
    }

    receiver.receiveAsFlow().filter {
        println("filter1")
        true
    }.onEach {
        println("R1: $it")
    }.launchIn(scope2)


    receiver.receiveAsFlow().filter {
        println("filter2")
        true
    }.onEach {
        println("R2: $it")
    }.launchIn(scope2)


}

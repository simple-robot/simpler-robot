package test

import io.netty.util.internal.ThrowableUtil.addSuppressed
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import love.forte.simbot.bot.BotVerifyException
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.utils.withExceptionCollector
import org.slf4j.LoggerFactory

class MyEx(message: String?, cause: Throwable?) : RuntimeException(message, cause)

fun main() {
    val logger = LoggerFactory.getLogger("1")


    runCatching {
        runBlocking {
            BotFactory.newBot(1, "1").login()
        }
    }.getOrElse {
        println(it)
        it.initCause(null)
        logger.error("err", BotVerifyException().apply { addSuppressed(it) })
    }
}


/*
Returning type parameter has been inferred to Nothing implicitly because Nothing is more specific than specified expected type.
Please specify type arguments explicitly in accordance with expected type to hide this warning.
Nothing can produce an exception at runtime.
See KT-36776 for more details.
 */

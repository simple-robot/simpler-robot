package test

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.utils.withExceptionCollector
import org.slf4j.LoggerFactory

class MyEx(message: String?, cause: Throwable?) : RuntimeException(message, cause)

fun main() {
    val logger = LoggerFactory.getLogger("1")
    val me = MyEx("NO", null)
    val ce = CancellationException("bot closed.", me)
    me.addSuppressed(ce)


    runCatching {
        runBlocking {
            throw CancellationException("bot closed.", me)
        }
    }.getOrElse {
        logger.error("err", it)
    }
}


suspend fun run(): Int = withExceptionCollector {
    delay(1)
    withExceptionCollector {
        if (System.getProperty("abc") == null) {
            collectThrow(MyEx("NO", null))
        } else 1
    }
}

/*
Returning type parameter has been inferred to Nothing implicitly because Nothing is more specific than specified expected type.
Please specify type arguments explicitly in accordance with expected type to hide this warning.
Nothing can produce an exception at runtime.
See KT-36776 for more details.
 */


fun Throwable.showAllSuppressed(index: Int) {
    println("> $index: $this")
    this.cause?.let {
        println("> $index CAUSE: $it")
        it.showAllSuppressed(index)
    }

    for (throwable in suppressed) {
        println("> SUPPRESSED $index -> $throwable")
        throwable.showAllSuppressed(index + 1)
    }
}
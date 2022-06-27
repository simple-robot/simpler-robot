package jmh

import love.forte.simbot.randomID
import org.openjdk.jmh.annotations.*
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit


@BenchmarkMode(Mode.Throughput)
@Measurement(timeUnit = TimeUnit.MILLISECONDS)
@Threads(8)
open class IdTest {
    @Benchmark
    fun randomIdByThreadLocalRandomGenerate() {
        randomID(ThreadLocalRandom.current())
    }
    
    @Benchmark
    fun randomIdByDefaultGenerate() {
        randomID()
    }
    
    @Benchmark
    fun randomUUIDGenerate() {
        UUID.randomUUID().toString()
    }
}

// fun main() {
//     val options = OptionsBuilder()
//         .include(IdTest::class.java.simpleName)
//         .output("benchmark_sequence.log")
//
//         .build()
//     Runner(options).run()
// }
import love.forte.simbot.JavaDuration
import love.forte.simbot.java
import love.forte.simbot.javaOrNull
import love.forte.simbot.kotlin
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

/**
 *
 * @author ForteScarlet
 */
class DurationConvertTest {
    
    @Test
    fun javaToKt() {
        assert(JavaDuration.ofNanos(1145141919810L).kotlin == 1145141919810L.nanoseconds)
        assert(JavaDuration.ofMillis(114514).kotlin == 114514.milliseconds)
        assert(JavaDuration.ofSeconds(114).kotlin == 114.seconds)
        assert(JavaDuration.ofMinutes(114).kotlin == 114.minutes)
        assert(JavaDuration.ofHours(114).kotlin == 114.hours)
        assert(JavaDuration.ofDays(114).kotlin == 114.days)
    }
    
    @Test
    fun ktToJava() {
        assert(1145141919810L.nanoseconds.java == JavaDuration.ofNanos(1145141919810L))
        assert(114514.milliseconds.java == JavaDuration.ofMillis(114514))
        assert(114.seconds.java == JavaDuration.ofSeconds(114))
        assert(114.minutes.java == JavaDuration.ofMinutes(114))
        assert(114.hours.java == JavaDuration.ofHours(114))
        assert(114.days.java == JavaDuration.ofDays(114))
        assert(Duration.ZERO.java == JavaDuration.ZERO)
        assert(Duration.INFINITE.javaOrNull == null)
        assert(Duration.INFINITE.unaryMinus().javaOrNull == null)
        assert(Duration.INFINITE.java { JavaDuration.ofSeconds(1) } == JavaDuration.ofSeconds(1))
        assert(Duration.INFINITE.unaryMinus().java { JavaDuration.ofSeconds(1) } == JavaDuration.ofSeconds(1))
    }
    
}
import love.forte.simbot.JavaDuration
import love.forte.simbot.toJavaDuration
import love.forte.simbot.toJavaDurationOrNull
import love.forte.simbot.toKotlinDuration
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
        assert(JavaDuration.ofNanos(1145141919810L).toKotlinDuration() == 1145141919810L.nanoseconds)
        assert(JavaDuration.ofMillis(114514).toKotlinDuration() == 114514.milliseconds)
        assert(JavaDuration.ofSeconds(114).toKotlinDuration() == 114.seconds)
        assert(JavaDuration.ofMinutes(114).toKotlinDuration() == 114.minutes)
        assert(JavaDuration.ofHours(114).toKotlinDuration() == 114.hours)
        assert(JavaDuration.ofDays(114).toKotlinDuration() == 114.days)
    }
    
    @Test
    fun ktToJava() {
        assert(1145141919810L.nanoseconds.toJavaDuration() == JavaDuration.ofNanos(1145141919810L))
        assert(114514.milliseconds.toJavaDuration() == JavaDuration.ofMillis(114514))
        assert(114.seconds.toJavaDuration() == JavaDuration.ofSeconds(114))
        assert(114.minutes.toJavaDuration() == JavaDuration.ofMinutes(114))
        assert(114.hours.toJavaDuration() == JavaDuration.ofHours(114))
        assert(114.days.toJavaDuration() == JavaDuration.ofDays(114))
        assert(Duration.ZERO.toJavaDuration() == JavaDuration.ZERO)
        assert(Duration.INFINITE.toJavaDurationOrNull() == null)
        assert(Duration.INFINITE.unaryMinus().toJavaDurationOrNull() == null)
        assert(Duration.INFINITE.toJavaDuration { JavaDuration.ofSeconds(1) } == JavaDuration.ofSeconds(1))
        assert(Duration.INFINITE.unaryMinus().toJavaDuration { JavaDuration.ofSeconds(1) } == JavaDuration.ofSeconds(1))
    }
    
}
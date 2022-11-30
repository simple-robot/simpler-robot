package love.forte.simbot.common.stagelooptest

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import love.forte.simbot.common.stageloop.Stage
import love.forte.simbot.common.stageloop.StageLoop
import love.forte.simbot.common.stageloop.loop
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class StageLoopTest {
    
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loopTest() = runTest {
        
        var times = 0
        val loop = StageLoop<TestStage>()
        loop.appendStage(TestStage.Start(10) {
            times++
        })
        
        loop.loop()
        
        assertEquals(10, times)
    }
    
    
    private sealed class TestStage : Stage<TestStage>() {
        class Start(val maxTime: Int, val runner: (time: Int) -> Unit) : TestStage() {
            override suspend fun invoke(loop: StageLoop<TestStage>) {
                if (maxTime > 0) {
                    loop.appendStage(Run(0, maxTime, runner))
                }
            }
        }
        
        class Run(val time: Int, val maxTime: Int, val runner: (time: Int) -> Unit) : TestStage() {
            override suspend fun invoke(loop: StageLoop<TestStage>) {
                runner(time)
                if (time + 1 < maxTime) {
                    loop.appendStage(Run(time + 1, maxTime, runner))
                }
            }
        }
        
    }
    
}

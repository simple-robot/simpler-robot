import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.event.ContinuousSessionContext
import love.forte.simbot.event.FriendMessageEvent
import love.forte.simbot.event.invoke

@OptIn(ExperimentalSimbotApi::class)
suspend fun FriendMessageEvent.testSession(session: ContinuousSessionContext) {
    
    session {
        val e = waitingForNext()
    }
    
}
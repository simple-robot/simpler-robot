import love.forte.simbot.event.ContinuousSessionContext
import love.forte.simbot.event.FriendMessageEvent
import love.forte.simbot.event.invoke

suspend fun FriendMessageEvent.testSession(session: ContinuousSessionContext) {
    
    session {
        val e = waitForNextBlocking()
    }
    
}
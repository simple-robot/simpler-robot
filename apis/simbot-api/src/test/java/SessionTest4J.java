import love.forte.simbot.event.ContinuousSessionContext;
import love.forte.simbot.event.FriendMessageEvent;

/**
 * @author ForteScarlet
 */
public class SessionTest4J {

    public void run(FriendMessageEvent event, ContinuousSessionContext session) {
        session.nextMessage(event);
    }
}

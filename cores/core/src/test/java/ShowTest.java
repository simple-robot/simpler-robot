import love.forte.simbot.BotManager;
import love.forte.simbot.event.EventProcessor;

/**
 * @author ForteScarlet
 */
public class ShowTest {
    public static void main(String[] args) {
        // final CoreListenerManagerConfiguration listenerManagerConfiguration = new CoreListenerManagerConfiguration();
        // final CoreListenerManager listenerManager = CoreListenerManager.newInstance(listenerManagerConfiguration);
        //
        // final BotManager<?> botManager = xxxBotManager(listenerManager, (config) -> { /* ... */ });
        //
        // final Bot register = botManager.register(id, key, token, orAnyOther, (config) -> { /* ... */ });
        //
        //
        // final EventListener listener = CoreListenerUtil.newCoreListener(
        //         ChannelMessageEvent.Key,
        //         (context, event) -> {
        //             System.out.println(context);
        //
        //             if (event instanceof MessageReplySupport) {
        //                 final Messages messages = Messages.getMessages(
        //                         Text.getText("Hello World"),
        //                         new At(event.getAuthor().getId())
        //                 );
        //
        //                 ((MessageReplySupport) event).replyBlocking(messages);
        //             }
        //
        //             final Guild guild = event.getChannel().getGuild();
        //
        //             guild.getChildren(null, Limiter.ZERO)
        //                     .forEach(ch -> System.out.println("Channel " + ch.getName() + "in guild " + guild.getName()));
        //
        //
        //             return null;
        //         }
        // );
        //
        // // register listener
        // listenerManager.register(listener);
        //
        //
        // register.startBlocking();
        // register.joinBlocking();
    }

    private static BotManager<?> xxxBotManager(EventProcessor processor) {
        return null;
    }

}

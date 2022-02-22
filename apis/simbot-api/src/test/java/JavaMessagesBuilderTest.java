import love.forte.simbot.Identifies;
import love.forte.simbot.message.Messages;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.message.Text;

/**
 * @author ForteScarlet
 */
public class JavaMessagesBuilderTest {

    public void builder() {
        final MessagesBuilder builder = new MessagesBuilder();
        final Messages messages = builder.at(Identifies.ID(123))
                .face(Identifies.ID("hi"))
                .atAll()
                .text("Hello ")
                .append(Text.of("World"))
                .build();

    }

}

import love.forte.simbot.Identifies;
import love.forte.simbot.message.At;
import love.forte.simbot.message.Messages;
import love.forte.simbot.message.Text;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author ForteScarlet
 */
@DisplayName("Java消息序列化")
public class JavaMessageSerializerTest {

    @Test
    public void serializer() {
        final Messages messages = Messages.getMessages(
                new At(Identifies.ID(123)),
                Text.of("Hello "),
                Text.of("World")
        );

        final String jsonString = Messages.toJsonString(messages);

        final Messages messages2 = Messages.fromJsonString(jsonString);

        assert messages.equals(messages2);
        assert messages2.equals(messages);


    }

}

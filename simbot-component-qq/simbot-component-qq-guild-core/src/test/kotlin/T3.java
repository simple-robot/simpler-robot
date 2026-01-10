import love.forte.simbot.event.ChannelMessageEvent;
import love.forte.simbot.message.Image;
import love.forte.simbot.message.ResourceImage;
import love.forte.simbot.resources.PathResource;
import love.forte.simbot.resources.Resource;
import love.forte.simbot.resources.Resources;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author ForteScarlet
 */
public class T3 {
    public void a(ChannelMessageEvent event) {
        Path path = Paths.get("xxx/你的图片.jpg");
        final PathResource resource = Resource.of(path);
        final ResourceImage image = Image.of(resource);

        event.replyBlocking(image);
        // or
        event.getChannel().sendBlocking(image);

    }
}

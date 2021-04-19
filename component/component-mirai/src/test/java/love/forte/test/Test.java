package love.forte.test;

import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.api.message.results.FileInfo;
import love.forte.simbot.api.message.results.FileResult;
import love.forte.simbot.api.message.results.FileResults;
import love.forte.simbot.api.sender.AdditionalApi;
import love.forte.simbot.api.sender.Getter;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.component.mirai.additional.MiraiAdditionalApis;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.SimbotProcess;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@SimbotApplication
public class Test implements SimbotProcess {
    public static void main(String[] args) {
        SimbotContext context = SimbotApp.run(Test.class, args);

        context.close();

        System.exit(1);
    }

    @Override
    public void pre(@NotNull Configuration config) {
    }

    @Override
    public void post(@NotNull SimbotContext context) {
        Bot bot = context.getBotManager().getDefaultBot();

        Getter getter = bot.getSender().GETTER;

        AdditionalApi<FileResults> groupFiles = MiraiAdditionalApis.groupFiles(1043409458);

        FileResults results = getter.additionalExecute(groupFiles);

        System.out.println(results);
        for (FileResult result : results.getResults()) {
            System.out.println(result);
            FileInfo info = result.getValue();
            System.out.println(info);
            System.out.println(info.getName());
            System.out.println(info.isFile());
            System.out.println(info.isDirectory());
            System.out.println(info.getUrl());
            System.out.println();
        }

    }
}

package love.forte.test;

import love.forte.common.configuration.ConfigurationProperty;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.annotation.SimbotResource;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.configuration.ExecutorServiceProperties;
import love.forte.simbot.task.TaskRunner;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@SimbotApplication
public class Test {
    public static void main(String[] args) {
        SimbotContext context = SimbotApp.run(Test.class, args);
        // TaskRunner runner = context.get(TaskRunner.class);

        ExecutorServiceProperties prop = context.get(ExecutorServiceProperties.class);

        System.out.println(prop);

        ConfigurationProperty config1 = context.getConfiguration().getConfig("simbot.core.task.pool.corePoolSize");
        System.out.println(config1);
        ConfigurationProperty config2 = context.getConfiguration().getConfig("simbot.core.corePoolSize");
        System.out.println(config2);

        // runner.run(() -> {
        //     try {
        //         Thread.sleep(2000);
        //         System.err.println("hi2000");
        //     } catch (InterruptedException ignored) { }
        // });
        // runner.run(() -> {
        //     try {
        //         Thread.sleep(4000);
        //         System.err.println("hi4000");
        //     } catch (InterruptedException ignored) { }
        // });

    }
}

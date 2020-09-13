package love.forte.test;

import cn.hutool.core.io.resource.ResourceUtil;
import love.forte.common.configuration.Configuration;
import love.forte.common.configuration.ConfigurationManagerRegistry;
import love.forte.common.configuration.ConfigurationParserManager;
import love.forte.common.configuration.impl.ConfigurationInjectorImpl;

import java.io.BufferedReader;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class Test1 {


    public static void main(String[] args) throws Exception {
        final BufferedReader test1Reader = ResourceUtil.getUtf8Reader("test.yml");
        final ConfigurationParserManager manager = ConfigurationManagerRegistry.defaultManager();
        final Configuration configuration = manager.parse("yml", test1Reader);

        final ConfigurationInjectorImpl injector = ConfigurationInjectorImpl.INSTANCE;

        final TestConfig testConfig = new TestConfig();

        injector.inject(testConfig, configuration);

        System.err.println(testConfig);
        System.err.println(testConfig.getAge());

    }



}

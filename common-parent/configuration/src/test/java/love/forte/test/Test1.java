package love.forte.test;

import cn.hutool.core.io.resource.ResourceUtil;
import love.forte.common.configuration.Configuration;
import love.forte.common.configuration.ConfigurationInjector;
import love.forte.common.configuration.ConfigurationManagerRegistry;
import love.forte.common.configuration.ConfigurationParserManager;
import love.forte.common.configuration.impl.ConfigurationInjectorImpl;

import java.io.BufferedReader;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class Test1 {


    public static void main(String[] args) throws Exception {
        // final BufferedReader test1Reader = ResourceUtil.getUtf8Reader("test.yml");
        // final ConfigurationParserManager manager = ConfigurationManagerRegistry.defaultManager();
        // final Configuration configuration = manager.parse("yml", test1Reader);
        //
        // final ConfigurationInjector injector = ConfigurationInjectorImpl.INSTANCE;
        //
        // final TestKTConf2 tc = new TestKTConf2();
        //
        // injector.inject(tc, configuration);
        //
        // System.err.println(tc);
        // System.err.println(tc.getAge());

    }



}

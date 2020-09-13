/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     PropertiesParser.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration.impl;

import love.forte.common.configuration.ReaderConfigurationParser;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 针对 properties 格式的配置文件的解析器。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class PropertiesParser extends ReaderConfigurationParser {

    private static final String PROP_PARSER_TYPE = "properties";
    private static final PropertiesParser INSTANCE = new PropertiesParser();
    public static PropertiesParser getInstance() {
        return INSTANCE;
    }
    private PropertiesParser(){}

    /**
     * 解析器类型。
     */
    @Override
    public String getType() {
        return PROP_PARSER_TYPE;
    }

    /**
     * 执行解析reader的逻辑。不需要close reader，会在上层方法中自动close。
     *
     * @param reader reader
     * @return 解析结果。
     * @throws IOException 可能会出现io异常。
     */
    @Override
    protected Map<String, Object> parseReader(Reader reader) throws IOException {
        Map<String, Object> confMap = new LinkedHashMap<>();

        Properties properties = new Properties();
        properties.load(reader);
        final Set<String> propKeys = properties.stringPropertyNames();
        for (String key : propKeys) {
            confMap.put(key, properties.getProperty(key));
        }

        return confMap;
    }
}

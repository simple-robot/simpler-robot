/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     LinkedConfigurationParserManagerBuilder.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration.impl;

import love.forte.common.configuration.ConfigurationParser;
import love.forte.common.configuration.ConfigurationParserManager;
import love.forte.common.configuration.ConfigurationParserManagerBuilder;
import love.forte.common.utils.convert.ConverterManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class LinkedConfigurationParserManagerBuilder implements ConfigurationParserManagerBuilder {

    private final ConverterManager converterManager;

    private final Map<String, ConfigurationParser> parsers;

    public LinkedConfigurationParserManagerBuilder(ConverterManager converterManager){
        this.converterManager = converterManager;
        this.parsers = new HashMap<>(4);
    }

     /**
     * 注册一个解析器
     *
     * @param type   类型
     * @param parser 解析器
     * @return this builder
     */
    @Override
    public ConfigurationParserManagerBuilder register(String type, ConfigurationParser parser) {
        parsers.put(type, parser);
        return this;
    }

    /**
     * 构建一个 {@link ConfigurationParserManager} 实例。
     *
     * @return {@link ConfigurationParserManager} 实例。
     */
    @Override
    public ConfigurationParserManager build() {
        final LinkedConfigurationParserManager manager = new LinkedConfigurationParserManager(converterManager);
        parsers.forEach(manager::setParser);
        return manager;
    }
}

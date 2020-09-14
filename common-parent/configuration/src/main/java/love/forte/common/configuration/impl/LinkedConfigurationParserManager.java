/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     LinkedConfigurationParserManager.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration.impl;

import love.forte.common.configuration.*;
import love.forte.common.configuration.exception.ConfigurationParseException;
import love.forte.common.utils.convert.ConverterManager;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * 基于链表的 {@link ConfigurationParserManager} 基础实现类。
 *
 * 在此manager下，同一个type的解析器可以存在多个，并通过链表连接，在解析的时候会尝试寻找第一个成功的结果。
 *
 * 需要提供一个 {@link ConverterManager} 来提供类型转化功能。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class LinkedConfigurationParserManager extends ReaderConfigurationParserManager {

    /**
     * 记录所有类型的解析器。
     */
    private final Map<String, LinkedList<ConfigurationParser>> parsers = new LinkedHashMap<>();

    private final ConverterManager converterManager;

    public LinkedConfigurationParserManager(ConverterManager converterManager){
        this.converterManager = converterManager;
    }

    /**
     * 获取一个指定类型的解析器。只会获取最后一次添加的解析器。
     *
     * @param type 类型
     * @return 解析器
     */
    @Override
    public ConfigurationParser getParser(String type) {
        final LinkedList<ConfigurationParser> parserList = parsers.get(type);
        if(parserList == null){
            return null;
        }else{
            return parserList.getLast();
        }
    }

    /**
     * 注册一个解析器。
     *
     * @param type   类型
     * @param parser 解析器
     * @return 固定返回为null。
     */
    public ConfigurationParser setParser(String type, ConfigurationParser parser) {
        LinkedList<ConfigurationParser> parserList = this.parsers.computeIfAbsent(type, k -> new LinkedList<>());
        parserList.addLast(parser);
        return null;
    }

    /**
     * 构建配置信息
     *
     * @param configMap config map.
     * @return {@link Configuration}
     */
    @Override
    protected Configuration createConfiguration(Map<String, Object> configMap) {
        Map<String, ConfigurationProperty> propMap = new HashMap<>(configMap.size());
        configMap.forEach((k, v) -> propMap.put(k, new ConverterConfigurationProperty(k, v, converterManager)));
        return new MapConfiguration(propMap);
    }



    @Override
    protected Configuration parseReader(String type, Reader reader) throws IOException {
        LinkedList<Exception> exs = new LinkedList<>();
        try (Reader rd = reader) {
            final LinkedList<ConfigurationParser> parserList = parsers.get(type);
            if(parserList != null){
                for (ConfigurationParser parser : parserList) {
                    try {
                        final Map<String, Object> parse = parser.parse(rd);
                        if(parse != null){
                            return createConfiguration(parse);
                        }else{
                            exs.add(new ConfigurationParseException("cannot parse this. (by "+ parser.getClass() +" for type "+ parser.getType() +")"));
                        }
                    }catch (Exception e){
                        exs.add(e);
                    }
                }
            }
            // maybe throw ex
            StringBuilder sb = new StringBuilder("config parse failed.");
            if(!exs.isEmpty()){
                sb.append("\nall exception message: \n");
                for (Exception ex : exs) {
                    sb.append("> ").append(ex.getLocalizedMessage()).append("\n");
                }
            }else{
                sb.append(" cannot found any parser for type '").append(type).append("'.");
            }

            throw new ConfigurationParseException(sb.toString());
        }
    }
}

/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     YamlParser.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration.impl;

import love.forte.common.configuration.ReaderConfigurationParser;
import org.yaml.snakeyaml.Yaml;

import java.io.Reader;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 针对 yml 格式文件的 {@link love.forte.common.configuration.ConfigurationParser} 实现。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class YamlParser extends ReaderConfigurationParser {

    private static final String YAML_PARSER_TYPE = "yaml";
    private static final YamlParser INSTANCE = new YamlParser();
    public static YamlParser getInstance() {
        return INSTANCE;
    }
    private YamlParser(){}
    /**
     * 每一个解析器都有一个对应的 <b> 类型 </b>，一般代表了配置文件的配置格式。例如 {@code properties}
     *
     * @return 解析类型
     */
    @Override
    public String getType() {
        return YAML_PARSER_TYPE;
    }


    /**
     * 执行解析reader的逻辑。不需要close reader，会在上层方法中自动close。
     *
     * @param reader reader
     * @return 解析结果。
     */
    @Override
    protected Map<String, Object> parseReader(Reader reader) {
        final Yaml yaml = new Yaml();
        //noinspection unchecked
        final Map<String, Object> map = yaml.loadAs(reader, Map.class);

        return map.entrySet().stream()
                .flatMap(e -> flatToEntry(null, e))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        throwingMerger(), LinkedHashMap::new));
    }

    /**
     * 如果出现key重复，抛出异常。
     */
    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate config key: %s", u));
        };
    }

    /**
     * map的entry扁平化并合并key值
     *
     * @param parentKey 父标签的key
     * @param entry     entry
     * @return 合并后的流
     */
    private static Stream<Map.Entry<String, Object>> flatToEntry(String parentKey, Map.Entry<String, Object> entry) {
        final Object value = entry.getValue();
        if (value instanceof Map) {
            //noinspection unchecked
            Map<String, Object> valueMap = (Map<String, Object>) value;
            String newParentKey;
            if (parentKey == null) {
                newParentKey = entry.getKey();
            } else {
                newParentKey = parentKey + '.' + entry.getKey();
            }
            return valueMap.entrySet().stream().flatMap(e -> flatToEntry(newParentKey, e));
        } else {
            if (parentKey == null) {
                return Stream.of(entry);
            } else {
                return Stream.of(new AbstractMap.SimpleEntry<>(parentKey + '.' + entry.getKey(), entry.getValue()));
            }
        }
    }
}

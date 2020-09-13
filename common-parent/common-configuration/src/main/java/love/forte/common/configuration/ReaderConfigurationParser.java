/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ReaderConfigurationParser.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration;

import love.forte.common.configuration.ConfigurationParser;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 *
 * 为一些需要使用 reader 作为解析途径的解析器提供基础功能实现。
 *
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public abstract class ReaderConfigurationParser implements ConfigurationParser {

    /**
     * 执行解析reader的逻辑。不需要close reader.
     *
     * @param reader reader
     * @return 解析结果。
     * @throws IOException 可能会出现io异常。
     */
    protected abstract Map<String, Object> parseReader(Reader reader) throws IOException;


    /**
     * 解析 URL 下的配置信息。
     *
     * @param url url
     * @return 解析结果
     */
    @Override
    public Map<String, Object> parse(URL url) throws IOException {
        return parseReader(new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)));
    }

    /**
     * 解析 InputStream 下的配置信息。
     *
     * @param inputStream inputStream
     * @return 解析结果
     */
    @Override
    public Map<String, Object> parse(InputStream inputStream) throws IOException {
        return parseReader(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
    }

    /**
     * 解析 file 下的配置信息。
     *
     * @param file file
     * @return 解析结果
     */
    @Override
    public Map<String, Object> parse(File file) throws IOException {
        return parseReader(new BufferedReader(new FileReader(file)));
    }

    /**
     * 解析 path 下的配置信息。
     *
     * @param path path
     * @return 解析结果
     */
    @Override
    public Map<String, Object> parse(Path path) throws IOException {
        return parseReader(Files.newBufferedReader(path, StandardCharsets.UTF_8));
    }

    /**
     * 解析 reader 下的配置信息。
     *
     * @param reader reader
     * @return 解析结果
     */
    @Override
    public Map<String, Object> parse(Reader reader) throws IOException {
        return parseReader(reader);
    }
}

/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ReaderConfigurationParserManager.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public abstract class ReaderConfigurationParserManager implements ConfigurationParserManager {

    /**
     * 构建配置信息
     * @param configMap config map.
     * @return {@link Configuration}
     */
    protected abstract Configuration createConfiguration(Map<String, Object> configMap);


    /**
     * 解析reader
     * @param type 资源类型
     * @param reader reader
     * @return {@link Configuration}
     * @throws IOException io ex
     */
    protected Configuration parseReader(String type, Reader reader) throws IOException {
        try (Reader rd = reader) {
            return createConfiguration(getParser(type).parse(rd));
        }
    }

    /**
     * 解析 URL 下的配置信息。
     *
     * @param url url
     * @return 解析结果
     */
    @Override
    public Configuration parse(String type, URL url) throws IOException {
        return parseReader(type, new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)));
    }

    /**
     * 解析 InputStream 下的配置信息。
     *
     * @param inputStream inputStream
     * @return 解析结果
     */
    @Override
    public Configuration parse(String type, InputStream inputStream) throws IOException {
        return parseReader(type, new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
    }

    /**
     * 解析 file 下的配置信息。
     *
     * @param file file
     * @return 解析结果
     */
    @Override
    public Configuration parse(String type, File file) throws IOException {
        return parseReader(type, new BufferedReader(new FileReader(file)));
    }

    /**
     * 解析 path 下的配置信息。
     *
     * @param path path
     * @return 解析结果
     */
    @Override
    public Configuration parse(String type, Path path) throws IOException {
        return parseReader(type, Files.newBufferedReader(path, StandardCharsets.UTF_8));
    }

    /**
     * 解析 reader 下的配置信息。
     *
     * @param reader reader
     * @return 解析结果
     */
    @Override
    public Configuration parse(String type, Reader reader) throws IOException {
        return parseReader(type, reader);
    }
}

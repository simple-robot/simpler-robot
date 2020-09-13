/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConfigurationParser.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

/**
 *
 * 配置信息解析器。
 *
 * 一般认为如果parse返回 null 即代表解析失败。
 *
 * 一般不需要close。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface ConfigurationParser {

    /**
     * 每一个解析器都有一个对应的 <b> 类型 </b>，一般代表了配置文件的配置格式。例如 {@code properties}
     * @return 解析类型
     */
    String getType();

    /**
     * 解析 URL 下的配置信息。
     * @param url url
     * @return 解析结果
     *
     * @throws IOException io exception
     */
    Map<String, Object> parse(URL url) throws IOException;

    /**
     * 解析 InputStream 下的配置信息。
     * @param inputStream inputStream
     * @return 解析结果
     *
     * @throws IOException io exception
     */
    Map<String, Object> parse(InputStream inputStream) throws IOException;

    /**
     * 解析 file 下的配置信息。
     * @param file file
     * @return 解析结果
     *
     * @throws IOException io exception
     */
    Map<String, Object> parse(File file) throws IOException;

    /**
     * 解析 path 下的配置信息。
     * @param path path
     * @return 解析结果
     *
     * @throws IOException io exception
     */
    Map<String, Object> parse(Path path) throws IOException;

    /**
     * 解析 reader 下的配置信息。
     * @param reader reader
     * @return 解析结果
     *
     * @throws IOException io exception
     */
    Map<String, Object> parse(Reader reader) throws IOException;


}

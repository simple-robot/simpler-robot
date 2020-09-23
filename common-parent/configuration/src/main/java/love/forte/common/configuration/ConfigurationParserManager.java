/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConfigurationParserManager.java
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

/**
 *
 * 解析管理器, 用于统合解析器并解析一个配置文件
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface ConfigurationParserManager {


    /**
     * 解析 URL 下的配置信息。
     * @param type 资源类型
     * @param url url
     * @return 解析结果
     *
     * @throws IOException io exception
     */
    Configuration parse(String type, URL url) throws IOException;

    /**
     * 解析 InputStream 下的配置信息。
     * @param type 资源类型
     * @param inputStream inputStream
     * @return 解析结果
     *
     * @throws IOException io exception
     */
    Configuration parse(String type, InputStream inputStream) throws IOException;

    /**
     * 解析 file 下的配置信息。
     * @param type 资源类型
     * @param file file
     * @return 解析结果
     *
     * @throws IOException io exception
     */
    Configuration parse(String type, File file) throws IOException;

    /**
     * 解析 path 下的配置信息。
     * @param type 资源类型
     * @param path path
     * @return 解析结果
     *
     * @throws IOException io exception
     */
    Configuration parse(String type, Path path) throws IOException;

    /**
     * 解析 reader 下的配置信息。
     * @param type 资源类型
     * @param reader reader
     * @return 解析结果
     *
     * @throws IOException io exception
     */
    Configuration parse(String type, Reader reader) throws IOException;


    /**
     * 获取一个指定类型的解析器。
     * @param type 类型
     * @return 解析器
     */
    ConfigurationParser getParser(String type);





}

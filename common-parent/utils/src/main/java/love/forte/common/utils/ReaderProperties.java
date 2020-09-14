/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-core
 * File     ReaderProperties.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 */

package love.forte.common.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 语言类Properties
 * @author ForteScarlet
 */
public class ReaderProperties extends Properties {

    /** 使用流读取的时候，默认使用utf-8编码读取 */
    private final Charset defaultCharset;

    public ReaderProperties(){
        defaultCharset = StandardCharsets.UTF_8;
    }

    public ReaderProperties(String charset){
        defaultCharset = Charset.forName(charset);
    }

    public ReaderProperties(Charset charset){
        defaultCharset = charset;
    }


    /**
     * override this load method
     */
    @Override
    public void load(Reader reader) throws IOException {
        super.load(new BufferedReader(reader));
    }
    /**
     * override this load method
     */
    @Override
    public void load(InputStream inputStream) throws IOException {
        load(new BufferedReader(new InputStreamReader(inputStream, defaultCharset)));
    }
    /**
     * override this load method
     */
    public void load(InputStream inputStream, String charset) throws IOException {
        load(new BufferedReader(new InputStreamReader(inputStream, charset)));
    }


}

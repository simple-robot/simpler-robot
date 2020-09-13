/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Language.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.language;


import love.forte.common.utils.ResourceUtil;

import java.io.Reader;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 国际化语言。
 * <p>
 * 文件使用 *.lang 的扩展名，且内容格式与properties相同。
 * <p>
 * 语言文件默认存放于 {@code language/} 路径下。
 * <p>
 * 同时支持 {@code str {0} str {1}}的格式与 {@code str {} str {} }的格式。
 * 但是要注意，如果使用，则每次只应该使用上述格式中的一种，而不要混用。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class Language {

    /**
     * 默认的资源文件路径
     */
    public static final String DEFAULT_PATH = "language";
    /**
     * 文件扩展名
     */
    private static final String FILE_TYPE = ".lang";

    /**
     * 切割正则
     */
    private static final Pattern SIMPLE_SPLIT_PATTERN = Pattern.compile("\\{}");

    /**
     * 默认会加载英语语言。
     */
    private static final Locale EN_LOCALE = Locale.US;

    /**
     * 语言列表
     */
    private static volatile Map<String, MessageFormat> messages = new HashMap<>();

    /**
     * 判断是否已经初始化(过一次)了
     */
    private static volatile boolean initialized = false;

    /**
     * 初始化语言.
     *
     * @param rootPath 语言文件资源存放的根路径。
     */
    public static void init(String rootPath, Locale locale) {
        init(null, null, null);
    }

    /**
     * 初始化语言.
     */
    public static void init(Locale locale) {
        init(null, null, locale);
    }

    /**
     * 初始化语言.
     */
    public static void init() {
        init(null, null, null);
    }

    /**
     * 初始化语言.
     *
     * @param rootPath 语言文件资源存放的根路径。nullable.
     * @param fileType 文件扩展名。nullable.
     */
    public static void init(String rootPath, String fileType, Locale locale) {
        rootPath = rootPath == null ? DEFAULT_PATH : rootPath;
        rootPath = rootPath.endsWith("/") ? rootPath : rootPath + '/';

        fileType = fileType == null ? FILE_TYPE : fileType;

        locale = locale == null ? Locale.getDefault() : locale;

        // do init
        init0(rootPath, fileType, locale);

        initialized = true;
    }

    /**
     * init language.
     *
     * @param rootPath path.
     * @param fileType file type.
     * @param locale   locale.
     */
    private static synchronized void init0(String rootPath, String fileType, Locale locale) {
        Map<String, MessageFormat> messages = new HashMap<>();

        // init en
        loadLang(messages, rootPath, fileType, EN_LOCALE);

        // init def locale
        loadLang(messages, rootPath, fileType, locale);

        Language.messages = messages;
    }


    private static boolean loadLang(Map<String, MessageFormat> messages, String rootPath, String fileType, Locale locale){
        boolean success;
        String path = rootPath + locale.toString() + fileType;
        success = appendLang(messages, path, locale);
        if(!success) {
            String enCountry = locale.getCountry();
            if(enCountry != null && enCountry.trim().length() > 0){
                path = rootPath + enCountry + fileType;
                success = appendLang(messages, path, locale);
            }
        }
        if(!success) {
            String enLanguage = locale.getLanguage();
            if(enLanguage != null && enLanguage.trim().length() > 0){
                path = rootPath + enLanguage + fileType;
                success = appendLang(messages, path, locale);
            }
        }
        return success;
    }

    /**
     * 根据资源路径加载数据并将结果追加到一个Map中。
     *
     * @param messages     messages map.
     * @param resourceName classpath resource name.
     * @return 读取资源是否成功。
     */
    private static boolean appendLang(Map<String, MessageFormat> messages, String resourceName, Locale locale) {
        // 只要有一个成功就算成功
        boolean success = false;
        for (Reader reader : ResourceUtil.getResourcesUtf8Reader(resourceName)) {
            try (Reader r = reader) {
                Properties prop = new Properties();
                prop.load(r);
                final Set<String> names = prop.stringPropertyNames();
                for (String name : names) {
                    messages.put(name, toMessageFormat(prop.getProperty(name), locale));
                }
                success = true;
            } catch (Exception ignored) {
            }
        }


        return success;
    }


    /**
     * 格式化一个语言。 使用 {@link MessageFormat} 进行格式化。
     *
     * 记得你需要先 {@link #init()}
     *
     * @param target 语言文字对应的键
     * @param format 格式化参数
     * @return 格式化后的结果。
     */
    public static String format(String target, Object... format) {
        if(!initialized){
            synchronized (Language.class) {
                if(!initialized) {
                    init();
                }
            }
        }

        final MessageFormat message = messages.get(target);
        if (message != null) {
            return message.format(format);
        } else {
            // 没有格式化参数，直接返回
            if (format.length == 0) {
                return target;
            } else {
                return toMessageFormat(target, Locale.getDefault()).format(format);
            }
        }
    }


    /**
     * 将文本转化为MessageFormat格式的文本。其中将兼容 {code {} }的格式。
     *
     * @param text 文本
     * @return {@link MessageFormat}
     */
    private static MessageFormat toMessageFormat(String text, Locale locale) {
        //noinspection AlibabaUndefineMagicConstant
        if (text.contains("{}")) {
            return simpleFormat(text, locale);
        } else {
            return normalFormat(text, locale);
        }
    }


    /**
     * 将 <code>{}</code> 的格式转化为 <code>{number}</code> 的格式。
     *
     * @param text   文本
     * @param locale locale
     * @return {@link MessageFormat}
     */
    private static MessageFormat simpleFormat(String text, Locale locale) {
        final String[] split = SIMPLE_SPLIT_PATTERN.split(text, -1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            sb.append(split[i]);
            if (i < split.length - 1) {
                sb.append('{').append(i).append('}');
            }
        }
        return normalFormat(sb.toString(), locale);
    }

    /**
     * 正常解析。
     *
     * @param text   文本
     * @param locale locale
     * @return {@link MessageFormat}
     */
    private static MessageFormat normalFormat(String text, Locale locale) {
        return new MessageFormat(text, locale);
    }

}

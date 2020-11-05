/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     SimbotAppProperties.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Configuration
@ConfigurationProperties(prefix = "simbot.core")
public class SimbotAppProperties {
    /**
     * 启动类路径。
     */
    private Class<?> appClass;

    /**
     * 指定的包扫描路径列表。
     */
    private String[] scanPackages;

    public Class<?> getAppClass() {
        return appClass;
    }

    public void setAppClass(Class<?> appClass) {
        this.appClass = appClass;
    }

    public String[] getScanPackages() {
        return scanPackages;
    }

    public void setScanPackages(String[] scanPackages) {
        this.scanPackages = scanPackages;
    }
}

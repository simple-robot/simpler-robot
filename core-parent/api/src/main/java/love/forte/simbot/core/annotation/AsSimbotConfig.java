/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     AsSimbotConfig.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.annotation;

import love.forte.common.configuration.annotation.AsConfig;
import love.forte.common.utils.annotation.AnnotateMapping;

import java.lang.annotation.*;

/**
 *
 * {@link AsConfig} 的 simbot子注解，{@link #prefix()} 默认为 'simbot'。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.TYPE}) //接口、类、枚举、注解、方法
@Documented
@AsConfig
@AnnotateMapping(AsConfig.class)
public @interface AsSimbotConfig {


    /** 前缀，默认为空 */
    String prefix() default "simbot";

    /** 后缀，默认为空 */
    String suffix() default "";

    /**
     * 如果在配置类上标注此注解且此参数为 {@code true},
     * 则没有标记 {@link love.forte.common.configuration.annotation.ConfigInject} 的字段也会被默认作为配置字段而添加。
     */
    boolean allField() default false;

    /**
     * 进行深层注入，即会扫描父类的字段。
     * @return
     */
    boolean deep() default true;


}


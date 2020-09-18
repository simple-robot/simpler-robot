/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     AnnotateMapping.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.utils.annotation;

import java.lang.annotation.*;

/**
 * 用于内部的注解继承映射
 *
 * 标注的注解方法的返回值类型应该与被映射的父类的方法返回值一致
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE}) // 使用在注解的方法上
public @interface AnnotateMapping {

    /**
     * 映射的父类注解
     */
    Class<? extends Annotation> type();

    /**
     * 映射的父类注解的名称
     * 默认为空，为空的时候默认认为name与当前方法名一致
     */
    String name() default "";

}

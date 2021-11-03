/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.spring.autoconfigure;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Configuration
public class SimbotAppPropertiesConfiguration implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(
            @NotNull AnnotationMetadata annotationMetadata,
            @NotNull BeanDefinitionRegistry registry,
            @NotNull BeanNameGenerator importBeanNameGenerator) {

        Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(EnableSimbot.class.getName());
        if (annotationAttributes == null) {
            annotationAttributes = Collections.emptyMap();
        }

        Class<?> appClass = (Class<?>) annotationAttributes.get("appClass");
        if (appClass == null) {
            appClass = SimbotSpringApp.class;
        }
        final SimbotAppProperties simbotAppProperties = new SimbotAppProperties(appClass);
        final AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SimbotAppProperties.class, () -> simbotAppProperties).getBeanDefinition();
        final String name = importBeanNameGenerator.generateBeanName(beanDefinition, registry);

        registry.registerBeanDefinition(name, beanDefinition);

    }
}

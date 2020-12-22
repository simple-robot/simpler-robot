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

import love.forte.common.configuration.Configuration;
import love.forte.common.configuration.ConfigurationProperty;
import love.forte.common.utils.convert.ConverterManager;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.stream.IntStream;

/**
 *
 * 整合Spring的Configuration
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class SpringEnvironmentConfiguration implements Configuration {

    private final ConfigurableEnvironment environment;

    private final ConverterManager converterManager;

    public SpringEnvironmentConfiguration(ConfigurableEnvironment environment, ConverterManager converterManager) {
        this.environment = environment;
        this.converterManager = converterManager;
    }

    @Override
    public ConfigurationProperty getConfig(String key) {
        return containsConfig(key) ? new SpringConfigurationProperty(key) : null;
    }

    @Override
    public ConfigurationProperty setConfig(String key, ConfigurationProperty config) {
        final MutablePropertySources propertySources = environment.getPropertySources();

        final Object value = config.getObject();
        final Map<String, Object> singletonMap = Collections.singletonMap(key, value);
        MapPropertySource propertySource = new MapPropertySource(key, singletonMap);
        propertySources.addLast(propertySource);
        return config;
    }



    @Override
    public boolean containsConfig(String key) {
        boolean contains = environment.containsProperty(key);
        if (!contains) {
            final String newKey = resetKey(key);
            contains = environment.containsProperty(newKey);
        }
        return contains;
    }

    @Override
    public int size() {
        return environment.getPropertySources().size();
    }

    private static String resetKey(String key){
        StringBuilder sb = new StringBuilder(key.length());
        // 将驼峰转化为短杠
        key.chars().flatMap(c -> {
            if (Character.isUpperCase(c)) {
                return IntStream.of('-', Character.toLowerCase(c));
            } else {
                return IntStream.of(c);
            }
        }).forEach(c -> sb.append((char) c));

        return sb.toString();
    }


    private class SpringConfigurationProperty implements ConfigurationProperty {

        private final String key;

        SpringConfigurationProperty(String key){
            String resetKey;
            if (environment.containsProperty(key)) {
                this.key = key;
            } else if (environment.containsProperty((resetKey = resetKey(key)))) {
                this.key = resetKey;
            } else {
                throw new IllegalArgumentException("cannot found config properties for key '"+key+"' or '"+ resetKey +"'");
            }
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Object getObject() {
            return getString();
        }

        @Override
        public <T> T getObject(Class<T> type) {
            return environment.getProperty(key, type);
        }

        @Override
        public <T> T getObject(Type type) {
            if (type instanceof Class<?>) {
                //noinspection unchecked
                return getObject((Class<T>) type);
            } else {
                String property = environment.getProperty(key);
                return converterManager.convert(type, property);
            }
        }

        @Override
        public String getString() {
            return environment.getProperty(key);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public int getInt() {
            return environment.getProperty(key, int.class);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public long getLong() {
            return environment.getProperty(key, long.class);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public boolean getBoolean() {
            return environment.getProperty(key, boolean.class);
        }
    }


}

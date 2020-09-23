package com.test;

import love.forte.common.ioc.annotation.ConfigBeans;
import love.forte.common.utils.annotation.AnnotationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@ConfigBeans("this is a test.")
public class Test {

    private String name;

    public static void main(String[] args) {
        Map<String, List<String>> map = new HashMap<>();

        List<String> list = map.computeIfAbsent("233", k -> new ArrayList<>());


    }
}

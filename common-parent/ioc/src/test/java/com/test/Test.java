package com.test;

import love.forte.common.ioc.DependCenter;
import love.forte.common.ioc.annotation.ConfigBeans;
import love.forte.common.utils.annotation.AnnotationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class Test {

    public static void main(String[] args) {
        final DependCenter center = new DependCenter();

        center.inject(ConfClass.class, B1.class, B2.class, B3.class);

        center.init();

        System.out.println("initialized.");

        // final BeansInt beansInt = center.get(BeansInt.class);
        //
        // System.out.println(beansInt);
        // System.out.println(beansInt.getName());
        //
        // System.out.println(center.get(BeansInt.class));
        // System.out.println(center.get(BeansInt.class));
        // System.out.println(center.get(BeansInt.class));
        // System.out.println(center.get(BeansInt.class));


    }
}

/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     PriorityConstant.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.constant;

/**
 * 优先级相关常量类。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class PriorityConstant {

    /** 最低的优先级 */
    public static final int LAST = Integer.MAX_VALUE;

    /* 核心中推荐使用的前10个优先级。10000 ~ 10090, 步长为10 */

    public static final int CORE_FIRST = 10_000;
    public static final int CORE_SECOND = 10_010;
    public static final int CORE_THIRD = 10_020;
    public static final int CORE_FOURTH = 10_030;
    public static final int CORE_FIFTH = 10_040;
    public static final int CORE_SIXTH = 10_050;
    public static final int CORE_SEVENTH = 10_060;
    public static final int CORE_EIGHTH = 10_070;
    public static final int CORE_NINTH = 10_080;
    public static final int CORE_TENTH = 10_090;
    public static final int CORE_LAST = CORE_TENTH;


    /* 组件中推荐使用的前10个优先级。1000 ~ 1090, 步长为10 */

    public static final int COMPONENT_FIRST = 1000;
    public static final int COMPONENT_SECOND = 1010;
    public static final int COMPONENT_THIRD = 1020;
    public static final int COMPONENT_FOURTH = 1030;
    public static final int COMPONENT_FIFTH = 1040;
    public static final int COMPONENT_SIXTH = 1050;
    public static final int COMPONENT_SEVENTH = 1060;
    public static final int COMPONENT_EIGHTH = 1070;
    public static final int COMPONENT_NINTH = 1080;
    public static final int COMPONENT_TENTH = 1090;
    public static final int COMPONENT_LAST = COMPONENT_TENTH;

    /* 模组中推荐使用的前10个优先级。100 ~ 190, 步长为10 */

    public static final int MODULE_FIRST = 100;
    public static final int MODULE_SECOND = 110;
    public static final int MODULE_THIRD = 120;
    public static final int MODULE_FOURTH = 130;
    public static final int MODULE_FIFTH = 140;
    public static final int MODULE_SIXTH = 150;
    public static final int MODULE_SEVENTH = 160;
    public static final int MODULE_EIGHTH = 170;
    public static final int MODULE_NINTH = 180;
    public static final int MODULE_TENTH = 190;
    public static final int MODULE_LAST = MODULE_TENTH;

    /* 正常使用中推荐使用的前10级的优先级. 0 ~ 90, 步长为10 */

    public static final int FIRST = 0;
    public static final int SECOND = 10;
    public static final int THIRD = 20;
    public static final int FOURTH = 30;
    public static final int FIFTH = 40;
    public static final int SIXTH = 50;
    public static final int SEVENTH = 60;
    public static final int EIGHTH = 70;
    public static final int NINTH = 80;
    public static final int TENTH = 90;
}

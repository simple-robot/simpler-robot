/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreFilterParameterMatcher.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.filter;

import love.forte.simbot.filter.FilterParameterMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter动态参数匹配器。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@SuppressWarnings("unused")
public class CoreFilterParameterMatcher implements FilterParameterMatcher {

    /**
     * 原始字符串
     */
    private final String original;
    /**
     * 用于匹配的正则
     */
    private final Pattern pattern;

    /**
     * point link.
     */
    private final Point point;

    /**
     * point size
     */
    private final int pointSize;

    // /**
    //  * 在对消息匹配之前，此处为前置处理器
    //  */
    // private final Function<String, String> textHandler;


    private CoreFilterParameterMatcher(String original, Pattern pattern, Function<String, String> textHandler, Point point) {
        this.original = original;
        this.pattern = pattern;
        this.point = point;
        // this.textHandler = textHandler;

        if (point != null) {
            int i = 0;
            Point p = point.first();
            while (p != null) {
                i++;
                p = p.next;
            }
            this.pointSize = i;
        } else {
            this.pointSize = 0;
        }

    }

    public static CoreFilterParameterMatcher compile(String string) {
        return compile(string, s -> s);
    }

    /**
     * 将一串字符串解析为{@link CoreFilterParameterMatcher}
     *
     * @param string 字符串
     * @return {@link CoreFilterParameterMatcher}
     */
    public static CoreFilterParameterMatcher compile(String string, Function<String, String> textHandler) {
        Objects.requireNonNull(string);
        Objects.requireNonNull(textHandler);

        String text = encode(string);
        final int textLength = text.length();
        boolean on = false;
        // 上一个符号
        Character last = null;

        Point point = null;

        StringBuilder builder = new StringBuilder(text.length() / 2);

        int skip = 0;
        // pattern group 的index，起始位置为1
        int index = 1;

        for (int i = 0; i < textLength; i++) {
            char t = text.charAt(i);
            if (on) {
                if (t == '{') {
                    skip++;
                }
                // 记录状态
                if (t == '}') {
                    if (skip > 0) {
                        skip--;
                        builder.append(t);
                        continue;
                    } else if (last != null && last.equals('}')) {
                        // 上一个也是结尾符，结束
                        if (builder.length() > 0) {
                            point = nextPoint(builder, index++, point, true);
                            final Pattern pointPattern = point.pointPattern;
                            final int groups = pointPattern.matcher("").groupCount();
                            if (groups > 0) {
                                index += groups;
                            }
                        }
                        builder.delete(0, builder.length());
                        on = false;
                    }
                } else {
                    if (last != null && last.equals('}')) {
                        // 上一个是{但是这个不是，追加一个{
                        builder.append('}');
                    }
                    builder.append(t);
                }
            } else {
                // 非记录状态
                if (t == '{') {
                    // 是{{开头的
                    if (last != null && last.equals('{')) {
                        if (builder.length() > 0) {
                            point = nextPoint(builder, -1, point, false);
                            final int groups = point.pointPattern.matcher("").groupCount();
                            if (groups > 0) {
                                index += groups;
                            }
                        }

                        builder.delete(0, builder.length());
                        on = true;
                    }
                } else {
                    if (last != null && last.equals('{')) {
                        // 上一个是{但是这个不是，追加一个{
                        builder.append('{');
                    }
                    builder.append(t);
                }
            }
            last = t;
        }

        if (builder.length() > 0) {
            point = nextPoint(builder, -1, point, on);
        }

        if (point == null) {
            return new CoreFilterParameterMatcher(string, Pattern.compile(string), textHandler, null);
        }

        final Pattern pattern = point.toPattern();
        return new CoreFilterParameterMatcher(string, pattern, textHandler, point.first());
    }


    private static Point nextPoint(CharSequence charSequence, int index, Point point, boolean isParameter) {
        String string = decode(charSequence.toString());
        if (point != null) {
            final Point compile = Point.compile(string, index, point, null, isParameter);
            point.next = compile;
            point = compile;
        } else {
            // the first
            point = Point.compile(string, index, null, null, isParameter);
        }
        return point;
    }

    /**
     * 转义
     * & -> &bsp;
     * \{ -> &000;
     *
     * @param text text
     */
    private static String encode(String text) {
        return text.replace("&", "&bsp;").replace("\\{", "&000;");
    }

    /**
     * 转义
     * &bsp; -> &
     * &000; -> \{
     *
     * @param text text
     */
    private static String decode(String text) {
        return text.replace("&000;", "\\{").replace("&bsp;", "&");
    }


    /**
     * 获取原始字符串
     *
     * @return 原始字符串
     */
    @Override
    public String getOriginal() {
        return original;
    }

    /**
     * 获取用于匹配的正则
     *
     * @return 匹配正则
     */
    @Override
    public Pattern getPattern() {
        return pattern;
    }


    /**
     * 根据变量名称获取一个动态参数。
     * 此文本需要符合正则表达式。
     *
     * @param name 变量名称
     * @param text 文本
     * @return 得到的参数
     */
    @Override
    public String getParam(String name, String text) {
        if (point == null) {
            return null;
        }

        final Matcher matcher = pattern.matcher(text);

        if (matcher.matches()) {
            Point p = point.first();
            while (p != null) {
                if(p.index > 0 && p.name.equals(name)) {
                    return matcher.group(p.index);
                }
                p = p.next;
            }
        }

        return null;
    }

    /**
     * 从一段匹配的文本中提取出需要的参数。
     * 此文本需要符合正则表达式。
     * 如果不符合表达式，返回null
     *
     * @param text 匹配的文本
     * @return 得到的参数
     */
    @Override
    public Map<String, String> getParams(String text) {
        if (point == null) {
            return null;
        }

        final Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            // 匹配
            Map<String, String> matches = new HashMap<>(pointSize);
            point.foreach(p -> {
                if (p.index > 0) {
                    matches.put(p.name, matcher.group(p.index));
                }
            });
            return matches;
        } else {
            return null;
        }
    }


    public Point getPoint() {
        return point;
    }

    public int getPointSize() {
        return pointSize;
    }

    /**
     * 点位，链表结构，得其一而得全部
     */
    private static class Point {
        // 匹配参数的name，纯字符串则为null
        String name;
        // 如果是纯字符串，则此处为字符串，如果是参数，则此处为正则
        String text;
        // index代表其group所在的index而不是分段的index, 如果当前为文本Point，index = -1
        int index;
        Point next;
        Point pre;
        Pattern pointPattern;

        Point(String name, String text, int index, Point pre, Point next) {
            this.name = name;
            this.text = text;
            this.pointPattern = Pattern.compile(text);
            this.index = index;
            this.next = next;
            this.pre = pre;
        }

        @SuppressWarnings("SameParameterValue")
        static Point compile(String text, int index, Point pre, Point next, boolean isParameter) {
            if (isParameter) {
                final String[] split = text.split(",", 2);
                String n;
                String p;
                if (split.length == 1) {
                    n = split[0];
                    p = ".*";
                } else {
                    n = split[0];
                    p = split[1];
                }
                return new Point(n, p, index, pre, next);
            } else {
                return new Point(null, text, index, pre, next);
            }
        }

        /**
         * 将链表转化为匹配正则
         *
         * @return {@link Pattern}
         */
        Pattern toPattern() {
            return Pattern.compile(toText());
        }

        Pattern pointPattern() {
            return pointPattern;
        }

        void foreach(Consumer<Point> consumer) {
            Point p = first();
            while (p != null) {
                consumer.accept(p);
                p = p.next;
            }

        }

        String toText() {
            final Point first = first();
            StringBuilder builder = new StringBuilder();
            Point p = first;
            while (p != null) {
                if (p.name == null) {
                    builder.append(p.text);
                } else {
                    builder.append('(').append(p.text).append(')');
                }
                p = p.next;
            }
            return builder.toString();
        }

        Point first() {
            if (pre != null) {
                return pre.first();
            } else {
                return this;
            }
        }

        Point last() {
            if (next != null) {
                return next.last();
            } else {
                return this;
            }
        }

        @Override
        public String toString() {
            if (name == null) {
                return text;
            } else {
                return name + "," + text;
            }
        }
    }


}

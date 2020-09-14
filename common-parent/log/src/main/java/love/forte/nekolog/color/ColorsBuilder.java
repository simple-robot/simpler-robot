package love.forte.nekolog.color;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author ForteScarlet <[163邮箱地址]ForteScarlet@163.com>
 * @since JDK1.8
 **/
@SuppressWarnings("unused")
public class ColorsBuilder {

    /** 颜色起始代码 */
    private final static String HEAD = "\u001b[";
    /** 颜色代码的结尾 */
    private final static String COLOR_END = "m";
    /** 颜色结尾代码 */
    private final static String END = "\u001b[0m";

    /** 颜色字符串结果区 */
    private final StringJoiner colorJoiner = new StringJoiner("", "", END);

    /** 按照顺序，使用的颜色列表 */
    private final List<Integer> colors = new LinkedList<>();
    /** 当前等待区的字体的颜色 */
    private int nowColor = -1;
    /** 当前等待区 */
    private final StringBuilder nowStr = new StringBuilder();

    //**************** 构造 ****************//

    private ColorsBuilder(Object str, int colorsIndex){
        this.nowStr.append(str);
        this.nowColor = colorsIndex;
    }
    private ColorsBuilder(Object str){
        this.nowStr.append(str);
    }
    private ColorsBuilder(){
    }

    //**************** 构建相关方法 ****************//

    /**
     * 增加一个字符串，未指定颜色
     */
    public ColorsBuilder add(Object str){
        //未指定颜色，则暂时作为无色处理
        add(str, -1);
        return this;
    }

    /**
     * 增加一个字符串，指定颜色
     */
    private ColorsBuilder add(Object str, int color){
        //判断增加的颜色是否相同或者是否为无色
        if(!(this.nowColor == -1 || color == this.nowColor)){
            //颜色不同，将上一次的字符串拼为颜色放入结果区
            flush();
        }
        //替换等待区内容
        color(color);
        this.nowStr.append(str);
        return this;
    }

    public ColorsBuilder addNoColor(String str){
        add(str, -1);
        flush();
        return this;
    }

    public ColorsBuilder add(Object str, ColorTypes colorTypes){
        return add(str, colorTypes.getColorIndex());
    }


    /**
     * 为当前等待区字符串设置颜色
     */
    private ColorsBuilder color(int color){
        this.nowColor = color;
        return this;
    }

    public ColorsBuilder color(ColorTypes color){
        return color(color.getColorIndex());
    }


    /**
     * 构建
     */
    public Colors build(){
        flush();
        ColorTypes[] colors = new ColorTypes[this.colors.size()];
        int i = 0;
        for (Integer color : this.colors) {
            colors[i++] = toColorTypes(color);
        }


        String colorStr = colorJoiner.toString();
        return new Colors(colorStr, colors);
    }

    /**
     * 构建一个字符串，而不构建为 {@link Colors} 实例。
     */
    public String buildString(){
        return colorJoiner.toString();
    }




    /**
     * 将当前等待区内容刷新到结果区
     */
    private void flush(){
        //输出到结果区
        String outStr = nowStr.toString();
        if(outStr.length() > 0){
            if(this.nowColor == -1){
                //如果为-1，则说明当前等待区字符无颜色，增加一个END并拼接
                colorJoiner.add(headEnd(outStr));
            }else{
                colorJoiner.add(toColorNoEnd(outStr, this.nowColor));
            }
            //记录颜色
            colors.add(this.nowColor);
        }

        //重置内容
        this.nowColor = -1;
        nowStr.delete(0, nowStr.length());
    }

    //**************** 颜色转化 ****************//

    /** 颜色代码转化为颜色枚举 */
    private ColorTypes toColorTypes(int colorIndex){
        //是否为背景
        BackGroundColorTypes backGround = BackGroundColorTypes.getColor(colorIndex);

        //如果不是背景则查看字体，如果字体没有则返回null
        return backGround != null ? backGround : FontColorTypes.getColor(colorIndex);
    }


    //**************** 获取构建器的工厂方法 ****************//

    /** 获取构建器 */
    public static ColorsBuilder getInstance(){
        return new ColorsBuilder();
    }
    /** 获取构建器 */
    public static ColorsBuilder getInstance(String str){
        return new ColorsBuilder(str);
    }
    /** 获取构建器 */
    public static ColorsBuilder getInstance(String str, FontColorTypes fontColor){
        return getInstance(str, fontColor.getColorIndex());
    }
    /** 获取构建器 */
    public static ColorsBuilder getInstance(String str, BackGroundColorTypes backGroundColor){
        return getInstance(str, backGroundColor.getColorIndex());
    }
    private static ColorsBuilder getInstance(String str, int colorsIndex){
        return new ColorsBuilder(str, colorsIndex);
    }

    /**
     * 将字符串转化为颜色字符串
     */
    private String toColor(String str, int colorIndex){
        return getColorHead(colorIndex) + str + getColorEnd();
    }

    /**
     * 将字符串转化为颜色字符串，没有结尾
     */
    private String toColorNoEnd(String str, int colorIndex){
        return getColorHead(colorIndex) + str;
    }

    /**
     * 为颜色字符串增加结尾
     */
    private String toEnd(String str){
        return str + getColorEnd();
    }

    /**
     * 在字符串头部增加结尾
     */
    private String headEnd(String str){
        return getColorEnd() + str;
    }

    //**************** 颜色字符串获取相关 ****************//

    /**
     * 获取颜色代码的头部
     */
    private String getColorHead(int colorIndex){
        return HEAD + colorIndex + COLOR_END;
    }

    /**
     * 获取颜色代码结尾
     */
    private String getColorEnd(){
        return END;
    }


}

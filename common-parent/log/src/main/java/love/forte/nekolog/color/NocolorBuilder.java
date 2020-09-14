/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     NoColorsBuilder.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.nekolog.color;

/**
 * 将会无视颜色的builder
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class NocolorBuilder extends ColorBuilder {
    public NocolorBuilder(CharSequence str, int colorsIndex) {
        super(str, colorsIndex);
    }
    public NocolorBuilder(CharSequence str) {
        super(str);
    }
    public NocolorBuilder() {
    }

    /**
     * 增加一个字符串，指定颜色
     */
    @Override
    protected ColorBuilder add(int color, CharSequence... str){
        for (CharSequence charSequence : str) {
            this.nowStr.append(charSequence);
        }
        return this;
    }

    @Override
    public Colors build() {
        return new Colors(buildString());
    }

    @Override
    protected void flush() {
        colorJoiner.add(nowStr.toString());
        nowStr.delete(0, nowStr.length());
    }
}

package love.forte.nekolog.color;


/**
 * 控制台可以用的颜色
 * @author ForteScarlet <[163邮箱地址]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class Colors {

    /** 颜色字体 */
    private final String COLOR_STR;

    /** 按照顺序的颜色数组 */
    private final ColorTypes[] COLORS;

    /**
     * 构造
     * 直接通过 {@link ColorsBuilder#build()} 构建.
     */
    Colors(String str, ColorTypes... colors){
        this.COLOR_STR = str;
        this.COLORS = colors;
    }

    /** 获取一个色彩字构建器 */
    public static ColorsBuilder builder(){
        return ColorsBuilder.getInstance();
    }

    public ColorTypes[] getColorTypes(){
        return COLORS;
    }

    /**
     * 重写toString
     */
    @Override
    public String toString(){
        return COLOR_STR;
    }
}

package love.forte.simbot.api

/**
 * 事件。
 * 所有事件的父类接口。
 * @author ForteScarlet
 */
public interface Event {

    /**
     * 每一个事件，都应存在一个对应的唯一ID。
     */
    public val id: String

    /**
     * 事件产生时的时间，毫秒值。
     * 此时间有两种可能：
     * 1. 对接组件本身支持或携带时间字段，可直接使用，或换算单位后使用。
     * 2. 对接组件本身不支持或未携带时间字段，则为当前时间实例创建出来的时间。
     */
    public val time: Long

    /**
     * 事件可能存在文本信息。
     */
    public val text: String?

}
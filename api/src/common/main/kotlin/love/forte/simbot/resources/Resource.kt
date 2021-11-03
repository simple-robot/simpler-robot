package love.forte.simbot.resources

/**
 *
 * 一个[资源][Resource].
 *
 * 资源可能是配置文件，亦或是一个[多媒体资源][MultimediaResource]。
 *
 *
 * @author ForteScarlet
 */ // external
public interface Resource {

    /**
     * 得到资源名称。
     */
    public val name: String

    /**
     * 得到资源的byte数据。
     */
    public val bytes: ByteArray


}

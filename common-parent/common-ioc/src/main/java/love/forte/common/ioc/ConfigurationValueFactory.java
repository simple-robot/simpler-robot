package love.forte.common.ioc;

/**
 *
 * 配置文件信息工厂，用于获取一些配置文件中的配置信息
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface ConfigurationValueFactory {

    /**
     * 根据一个配置的key得到一个配置信息字符串
     * @param key key 值
     * @return
     */
    String getConf(String key);

}

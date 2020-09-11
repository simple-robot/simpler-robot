package love.forte.common.ioc;

/**
 *
 * 依赖bean工厂，用于得到依赖工厂中管理的bean。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface DependBeanFactory {


    <T> T get(Class<T> type);



}

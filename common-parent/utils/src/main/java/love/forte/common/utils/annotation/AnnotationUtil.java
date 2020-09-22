/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     AnnotationUtil.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.utils.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对于一些注解的获取等相关的工具类。
 *
 * 注解可以通过 {@link AnnotateMapping} 实现注解间的继承与值映射。
 *
 * 继承即通过注解类标注来实现。例如 {@code @A} 标注在了 {@code @B}上，则认为 {@code @B} 继承了注解 {@code @A}，
 *
 * 则假如一个类上标注了 {@code @B} 的时候，便可以直接通过 {@code AnnotationUtil.getAnnotation(class, A.class)}来获取注解 {@code @A}实例。
 *
 *
 * @author ForteScarlet <[163邮箱地址]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class AnnotationUtil {

    /**
     * java原生注解所在包路径
     */
    private static final Package JAVA_ANNOTATION_PACKAGE = Target.class.getPackage();


    /**
     * 注解缓存，记录曾经保存过的注解与其所在类
     */
    private static final Map<AnnotatedElement, Set<Annotation>> ANNOTATION_CACHE = new ConcurrentHashMap<>();

    /**
     * 记录已经证实不存在的注解信息
     */
    private static final Map<AnnotatedElement, Set<Class<Annotation>>> NULL_CACHE = new ConcurrentHashMap<>();


    private static final Set<String> OBJECT_METHODS;

    static {
        OBJECT_METHODS = new HashSet<>();
        OBJECT_METHODS.add("toString");
        OBJECT_METHODS.add("equals");
        OBJECT_METHODS.add("hashCode");
        OBJECT_METHODS.add("getClass");
        OBJECT_METHODS.add("clone");
        OBJECT_METHODS.add("notify");
        OBJECT_METHODS.add("notifyAll");
        OBJECT_METHODS.add("wait");
        OBJECT_METHODS.add("finalize");
    }


    /**
     * 从某个类上获取注解对象，注解可以深度递归
     * 如果存在多个继承注解，则优先获取浅层第一个注解，如果浅层不存在，则返回第一个获取到的注解
     * 请尽可能保证仅存在一个或者一种继承注解，否则获取到的类型将不可控
     *
     * @param from           获取注解的某个类
     * @param annotationType 想要获取的注解类型
     * @return 获取到的第一个注解对象
     */
    public static <T extends Annotation> T getAnnotation(AnnotatedElement from, Class<T> annotationType) {
        return getAnnotation(from, annotationType, (Class<T>[]) new Class[]{});
    }

    /**
     * 从某个类上获取注解对象，注解可以深度递归
     * 如果存在多个继承注解，则优先获取浅层第一个注解，如果浅层不存在，则返回第一个获取到的注解
     * 请尽可能保证仅存在一个或者一种继承注解，否则获取到的类型将不可控
     *
     * @param from           获取注解的某个类
     * @param annotationType 想要获取的注解类型
     * @param ignored        获取注解列表的时候的忽略列表
     * @return 获取到的第一个注解对象
     */
    public static <T extends Annotation> T getAnnotation(AnnotatedElement from, Class<T> annotationType, Class<T>... ignored) {
        return getAnnotation(null, from, annotationType, ignored);
    }


    /**
     * 从某个类上获取注解对象。注解可以深度递归。
     *
     * 如果存在多个继承注解，则优先获取浅层第一个注解，如果浅层不存在，则返回第一个获取到的注解。
     *
     * 请尽可能保证仅存在一个或者一种继承注解，否则获取到的类型将不可控。
     *
     * @param fromInstance    from的实例类，一般都是注解才需要。
     * @param from           获取注解的某个类
     * @param annotationType 想要获取的注解类型
     * @param ignored        获取注解列表的时候的忽略列表
     * @return 获取到的第一个注解对象
     */
    private static <T extends Annotation> T getAnnotation(Annotation fromInstance, AnnotatedElement from, Class<T> annotationType, Class<T>... ignored) {
        // 首先尝试获取缓存
        T cache = getCache(from, annotationType);
        if(cache != null){
            return cache;
        }

        if(isNull(from, annotationType)){
            return null;
        }


        //先尝试直接获取
        T annotation = from.getAnnotation(annotationType);

        //如果存在直接返回，否则查询
        if (annotation != null) {
            return mappingAndSaveCache(fromInstance, from, annotation);
        }

        // 获取target注解
        Target target = annotationType.getAnnotation(Target.class);
        // 判断这个注解能否标注在其他注解上，如果不能，则不再深入获取
        boolean annotationable = false;
        if (target != null) {
            for (ElementType elType : target.value()) {
                if (elType == ElementType.TYPE || elType == ElementType.ANNOTATION_TYPE) {
                    annotationable = true;
                    break;
                }
            }
        }

        Annotation[] annotations = from.getAnnotations();
        annotation = annotationable ? getAnnotationFromArrays(fromInstance, annotations, annotationType, ignored) : null;



        // 如果最终不是null，计入缓存
        if(annotation != null){
            annotation = mappingAndSaveCache(fromInstance, from, annotation);
        }else{
            nullCache(from, annotationType);
        }

        return annotation;
    }


    /**
     *
     * @param from 如果是来自与另一个注解的, 此处是来源。可以为null
     * @param array
     * @param annotationType
     * @param <T>
     * @return
     */
    private static <T extends Annotation> T getAnnotationFromArrays(Annotation from, Annotation[] array, Class<T> annotationType, Class<T>... ignored) {
        //先浅查询第一层
        //全部注解
        Annotation[] annotations = Arrays.stream(array)
                .filter(a -> {
                    for (Class<? extends Annotation> aType : ignored) {
                        if (a.annotationType().equals(aType)) {
                            return false;
                        }
                    }
                    return true;
                })
                .filter(a -> {
                    if (a == null) {
                        return false;
                    }
                    //如果此注解的类型就是我要的，直接放过
                    if (a.annotationType().equals(annotationType)) {
                        return true;
                    }
                    //否则，过滤掉java原生注解对象
                    //通过包路径判断
                    return !JAVA_ANNOTATION_PACKAGE.equals(a.annotationType().getPackage());
                }).peek(a -> {
                    if(from != null){
                        mapping(from, a);
                    }
                }).toArray(Annotation[]::new);


        if (annotations.length == 0) {
            return null;
        }

        Class<? extends Annotation>[] annotationTypes = new Class[annotations.length];
        for (int i = 0; i < annotations.length; i++) {
            annotationTypes[i] = annotations[i].annotationType();
        }

        Class<T>[] newIgnored = new Class[annotationTypes.length + ignored.length];
        System.arraycopy(ignored, 0, newIgnored, 0, ignored.length);
        System.arraycopy(annotationTypes, 0, newIgnored, ignored.length, annotationTypes.length);


        //如果浅层查询还是没有，递归查询

        for (Annotation a : annotations) {
            T annotationGet = getAnnotation(a, a.annotationType(), annotationType, newIgnored);
            if (annotationGet != null) {
                return annotationGet;
            }
        }

        //如果还是没有找到，返回null
        return null;
    }

    /**
     * 从缓存中获取缓存注解
     * @param from          来源
     * @param annotatedType 注解类型
     * @return  注解缓存，可能为null
     */
    private static <T extends Annotation> T getCache(AnnotatedElement from, Class<T> annotatedType){
        Set<Annotation> list = ANNOTATION_CACHE.get(from);
        if(list != null){
            // 寻找
            for (Annotation a : list) {
                if(a.annotationType().equals(annotatedType)){
                    return (T) a;
                }
            }
        }
        // 找不到，返回null
        return null;
    }

    /**
     * 记录一个得不到的缓存
     * @param from {@link AnnotatedElement}
     * @param annotatedType annotation class
     */
    private static <T extends Annotation> void nullCache(AnnotatedElement from, Class<T> annotatedType){
        final Set<Class<Annotation>> classes = NULL_CACHE.computeIfAbsent(from, k -> new HashSet<>());
        classes.add((Class<Annotation>) annotatedType);
    }

    /**
     * 判断是否获取不到
     * @param from {@link AnnotatedElement}
     * @param annotatedType annotation class
     */
    private static <T extends Annotation> boolean isNull(AnnotatedElement from, Class<T> annotatedType){
        final Set<Class<Annotation>> classes = NULL_CACHE.get(from);
        if(classes == null || classes.isEmpty()){
            return false;
        }
        return classes.contains(annotatedType);
    }



    /**
     * 记录一条缓存记录。
     */
    private static boolean saveCache(AnnotatedElement from, Annotation annotation){
        Set<Annotation> set;
        synchronized (ANNOTATION_CACHE) {
            set = ANNOTATION_CACHE.computeIfAbsent(from, k -> new HashSet<>());
            // 如果为空，新建一个并保存
        }
        // 记录这个注解
        return set.add(annotation);
    }


    /**
     * 执行注解映射
     */
    private static <T extends Annotation> T mapping(Annotation from, T to){
        final Class<? extends Annotation> fromAnnotationType = from.annotationType();
        final Method[] methods = fromAnnotationType.getMethods();
        final Map<String, Object> params = new HashMap<>();
        final AnnotateMapping classAnnotateMapping = fromAnnotationType.getAnnotation(AnnotateMapping.class);
        AnnotateMapping annotateMapping;
        //noinspection unchecked
        final Class<T> toType = (Class<T>) to.annotationType();
        for (Method method : methods) {
            if(OBJECT_METHODS.contains(method.getName())){
                continue;
            }
            annotateMapping = method.getAnnotation(AnnotateMapping.class);
            if(annotateMapping == null){
                annotateMapping = classAnnotateMapping;
            }
            if(annotateMapping != null){
                if(annotateMapping.type().equals(toType)){
                    String name = annotateMapping.name();
                    if(name.length() == 0){
                        name = method.getName();
                    }
                    try {
                        Object value = method.invoke(from);
                        params.put(name, value);
                    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("cannot map " + name + " for " + method, e);
                    }
                }
            }
        }
        return AnnotationProxyUtil.proxy(toType, to, params);
    }
    
    /**
     * 进行注解值映射，并缓存，返回
     */
    private static <T extends Annotation> T  mappingAndSaveCache(Annotation fromInstance, AnnotatedElement from, T annotation){
        // 如果from是一个注解, 则构建一个新的annotation实例
        if(fromInstance != null && from instanceof Class && ((Class<?>) from).isAnnotation()){
            return mapping(fromInstance, annotation);
        }else{
            saveCache(from, annotation);
            return annotation;
        }
    }


    /**
     * 清除缓存
     */
    public static void cleanCache(){
        ANNOTATION_CACHE.clear();
    }

}

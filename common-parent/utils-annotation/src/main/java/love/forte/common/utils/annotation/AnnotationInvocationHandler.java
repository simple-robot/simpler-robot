/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     AnnotationInvocationHandler.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.utils.annotation;

import sun.reflect.annotation.ExceptionProxy;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * 注解动态代理实例
 *
 * @author ForteScarlet
 */
public class AnnotationInvocationHandler implements InvocationHandler {
    private final Class<? extends Annotation> type;
    private final Map<String, Object> memberValues;
    private transient volatile Method[] memberMethods = null;
    private static Method exceptionProxyGenerateException;

    static {
        Method exceptionProxyGenerateException;
        try {
            final Class<?> exceptionProxyClass = Class.forName("sun.reflect.annotation.ExceptionProxy");
            exceptionProxyGenerateException = exceptionProxyClass.getDeclaredMethod("generateException");
        }catch (Exception e){
            exceptionProxyGenerateException = null;
        }
        AnnotationInvocationHandler.exceptionProxyGenerateException = exceptionProxyGenerateException;
    }

    <T extends Annotation> AnnotationInvocationHandler(Class<T> annotationType, Map<String, Object> memberValues) {
        this.type = annotationType;
        this.memberValues = memberValues;

    }

    /**
     * 一个注解的代理逻辑实例。
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        final String name = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if ("equals".equals(name) && parameterTypes.length == 1 && parameterTypes[0] == Object.class) {
            return this.equalsImpl(args[0]);
        } else if (parameterTypes.length != 0) {
            // same as sun.reflect.annotation.AnnotationInvocationHandler
            throw new AssertionError("Too many parameters for an annotation method");
        } else {
            switch (name) {
                case "toString":
                    return this.toStringImpl();
                case "hashCode":
                    return this.hashCodeImpl();
                case "annotationType":
                    return this.type;
                default:
                    Object value = this.memberValues.get(name);
                    if (value == null) {
                        // final Object defaultValue = method.getDefaultValue();
                        // if(defaultValue != null){
                        //     return defaultValue;
                        // }
                        throw new IncompleteAnnotationException(this.type, name);
                    } else if (value instanceof ExceptionProxy && exceptionProxyGenerateException != null) {
                        try {
                            throw (RuntimeException) exceptionProxyGenerateException.invoke(value);
                        }catch (Exception e){
                            throw new RuntimeException("An ExceptionProxy. " + this.type);
                        }
                    } else {
                        if (value.getClass().isArray() && Array.getLength(value) != 0) {
                            value = this.cloneArray(value);
                        }

                        return value;
                    }
            }

        }
    }

    private boolean equalsImpl(Object other) {
        if (this == other) {
            return true;
        } else if (!this.type.isInstance(other)) {
            return false;
        } else {
            Method[] methods = this.getMemberMethods();
            int methodLength = methods.length;

            for (int i = 0; i < methodLength; ++i) {
                Method var5 = methods[i];
                String var6 = var5.getName();
                Object var7 = this.memberValues.get(var6);
                Object var8;
                AnnotationInvocationHandler var9 = this.asOneOfUs(other);
                if (var9 != null) {
                    var8 = var9.memberValues.get(var6);
                } else {
                    try {
                        var8 = var5.invoke(other);
                    } catch (InvocationTargetException var11) {
                        return false;
                    } catch (IllegalAccessException var12) {
                        throw new AssertionError(var12);
                    }
                }

                if (!memberValueEquals(var7, var8)) {
                    return false;
                }
            }
            return true;
        }
    }


    private AnnotationInvocationHandler asOneOfUs(Object var1) {
        if (Proxy.isProxyClass(var1.getClass())) {
            InvocationHandler var2 = Proxy.getInvocationHandler(var1);
            if (var2 instanceof AnnotationInvocationHandler) {
                return (AnnotationInvocationHandler) var2;
            }
        }
        return null;
    }

    private Method[] getMemberMethods() {
        if (this.memberMethods == null) {
            this.memberMethods = AccessController.doPrivileged((PrivilegedAction<Method[]>) () -> {
                Method[] var1 = AnnotationInvocationHandler.this.type.getDeclaredMethods();
                AnnotationInvocationHandler.this.validateAnnotationMethods(var1);
                AccessibleObject.setAccessible(var1, true);
                return var1;
            });
        }

        return this.memberMethods;
    }


    private void validateAnnotationMethods(Method[] methods) {
        boolean notMalformed = true;
        int methodsLength = methods.length;
        int i = 0;

        while (i < methodsLength) {
            Method method = methods[i];
            if (method.getModifiers() == (Modifier.PUBLIC | Modifier.ABSTRACT) && !method.isDefault() && method.getParameterCount() == 0 && method.getExceptionTypes().length == 0) {
                Class<?> returnType = method.getReturnType();
                if (returnType.isArray()) {
                    returnType = returnType.getComponentType();
                    if (returnType.isArray()) {
                        notMalformed = false;
                        break;
                    }
                }

                if ((!returnType.isPrimitive() || returnType == Void.TYPE) && returnType != String.class && returnType != Class.class && !returnType.isEnum() && !returnType.isAnnotation()) {
                    notMalformed = false;
                    break;
                }

                String methodName = method.getName();
                if ((!"toString".equals(methodName) || returnType != String.class) && (!"hashCode".equals(methodName) || returnType != Integer.TYPE) && (!"annotationType".equals(methodName) || returnType != Class.class)) {
                    ++i;
                    continue;
                }

                notMalformed = false;
                break;
            }

            notMalformed = false;
            break;
        }

        if (!notMalformed) {
            throw new AnnotationFormatError("Malformed method on an annotation type");
        }
    }


    private static boolean memberValueEquals(Object o1, Object o2) {
        Class<?> var2 = o1.getClass();
        if (!var2.isArray()) {
            return o1.equals(o2);
        } else if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) ((Object[]) o1), (Object[]) ((Object[]) o2));
        } else if (o2.getClass() != var2) {
            return false;
        } else if (var2 == byte[].class) {
            return Arrays.equals((byte[]) ((byte[]) o1), (byte[]) ((byte[]) o2));
        } else if (var2 == char[].class) {
            return Arrays.equals((char[]) ((char[]) o1), (char[]) ((char[]) o2));
        } else if (var2 == double[].class) {
            return Arrays.equals((double[]) ((double[]) o1), (double[]) ((double[]) o2));
        } else if (var2 == float[].class) {
            return Arrays.equals((float[]) ((float[]) o1), (float[]) ((float[]) o2));
        } else if (var2 == int[].class) {
            return Arrays.equals((int[]) ((int[]) o1), (int[]) ((int[]) o2));
        } else if (var2 == long[].class) {
            return Arrays.equals((long[]) ((long[]) o1), (long[]) ((long[]) o2));
        } else if (var2 == short[].class) {
            return Arrays.equals((short[]) ((short[]) o1), (short[]) ((short[]) o2));
        } else {
            assert var2 == boolean[].class;

            return Arrays.equals((boolean[]) ((boolean[]) o1), (boolean[]) ((boolean[]) o2));
        }
    }

    private String toStringImpl() {
        StringBuilder var1 = new StringBuilder(128);
        var1.append('@');
        var1.append(this.type.getName());
        var1.append('(');
        boolean var2 = true;
        Iterator var3 = this.memberValues.entrySet().iterator();

        while (var3.hasNext()) {
            Map.Entry var4 = (Map.Entry) var3.next();
            if (var2) {
                var2 = false;
            } else {
                var1.append(", ");
            }

            var1.append((String) var4.getKey());
            var1.append('=');
            var1.append(memberValueToString(var4.getValue()));
        }

        var1.append(')');
        return var1.toString();
    }

    private static String memberValueToString(Object var0) {
        Class<?> var1 = var0.getClass();
        if (!var1.isArray()) {
            return var0.toString();
        } else if (var1 == byte[].class) {
            return Arrays.toString((byte[]) ((byte[]) var0));
        } else if (var1 == char[].class) {
            return Arrays.toString((char[]) ((char[]) var0));
        } else if (var1 == double[].class) {
            return Arrays.toString((double[]) ((double[]) var0));
        } else if (var1 == float[].class) {
            return Arrays.toString((float[]) ((float[]) var0));
        } else if (var1 == int[].class) {
            return Arrays.toString((int[]) ((int[]) var0));
        } else if (var1 == long[].class) {
            return Arrays.toString((long[]) ((long[]) var0));
        } else if (var1 == short[].class) {
            return Arrays.toString((short[]) ((short[]) var0));
        } else {
            return var1 == boolean[].class ? Arrays.toString((boolean[]) ((boolean[]) var0)) : Arrays.toString((Object[]) ((Object[]) var0));
        }
    }


    private Object cloneArray(Object var1) {
        Class<?> var2 = var1.getClass();
        if (var2 == byte[].class) {
            byte[] var6 = (byte[]) ((byte[]) var1);
            return var6.clone();
        } else if (var2 == char[].class) {
            char[] var5 = (char[]) ((char[]) var1);
            return var5.clone();
        } else if (var2 == double[].class) {
            double[] var4 = (double[]) ((double[]) var1);
            return var4.clone();
        } else if (var2 == float[].class) {
            float[] var11 = (float[]) ((float[]) var1);
            return var11.clone();
        } else if (var2 == int[].class) {
            int[] var10 = (int[]) ((int[]) var1);
            return var10.clone();
        } else if (var2 == long[].class) {
            long[] var9 = (long[]) ((long[]) var1);
            return var9.clone();
        } else if (var2 == short[].class) {
            short[] var8 = (short[]) ((short[]) var1);
            return var8.clone();
        } else if (var2 == boolean[].class) {
            boolean[] var7 = (boolean[]) ((boolean[]) var1);
            return var7.clone();
        } else {
            Object[] var3 = (Object[]) ((Object[]) var1);
            return var3.clone();
        }
    }

    private int hashCodeImpl() {
        int var1 = 0;

        Map.Entry var3;
        for (Iterator var2 = this.memberValues.entrySet().iterator(); var2.hasNext(); var1 += 127 * ((String) var3.getKey()).hashCode() ^ memberValueHashCode(var3.getValue())) {
            var3 = (Map.Entry) var2.next();
        }

        return var1;
    }

    private static int memberValueHashCode(Object var0) {
        Class<?> var1 = var0.getClass();
        if (!var1.isArray()) {
            return var0.hashCode();
        } else if (var1 == byte[].class) {
            return Arrays.hashCode((byte[]) ((byte[]) var0));
        } else if (var1 == char[].class) {
            return Arrays.hashCode((char[]) ((char[]) var0));
        } else if (var1 == double[].class) {
            return Arrays.hashCode((double[]) ((double[]) var0));
        } else if (var1 == float[].class) {
            return Arrays.hashCode((float[]) ((float[]) var0));
        } else if (var1 == int[].class) {
            return Arrays.hashCode((int[]) ((int[]) var0));
        } else if (var1 == long[].class) {
            return Arrays.hashCode((long[]) ((long[]) var0));
        } else if (var1 == short[].class) {
            return Arrays.hashCode((short[]) ((short[]) var0));
        } else {
            return var1 == boolean[].class ? Arrays.hashCode((boolean[]) ((boolean[]) var0)) : Arrays.hashCode((Object[]) ((Object[]) var0));
        }
    }
}
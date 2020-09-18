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
    private final Annotation baseAnnotation;

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

    <T extends Annotation> AnnotationInvocationHandler(Class<T> annotationType, Map<String, Object> memberValues, Annotation baseAnnotation) {
        this.type = annotationType;
        this.memberValues = memberValues;
        this.baseAnnotation = baseAnnotation;
    }

    <T extends Annotation> AnnotationInvocationHandler(Class<T> annotationType, Map<String, Object> memberValues) {
        this.type = annotationType;
        this.memberValues = memberValues;
        this.baseAnnotation = null;
    }

    /**
     * 一个注解的代理逻辑实例。
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        final String name = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        //noinspection AlibabaUndefineMagicConstant
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
                        if(baseAnnotation != null) {
                            return method.invoke(baseAnnotation);
                        }

                        final Object defaultValue = method.getDefaultValue();
                        if(defaultValue != null){
                            return defaultValue;
                        }

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

            for (Method method : methods) {
                String methodName = method.getName();
                Object value = this.memberValues.get(methodName);
                Object otherValue;
                AnnotationInvocationHandler otherHandler = this.asOneOfUs(other);
                if (otherHandler != null) {
                    otherValue = otherHandler.memberValues.get(methodName);
                } else {
                    try {
                        otherValue = method.invoke(other);
                    } catch (InvocationTargetException var11) {
                        return false;
                    } catch (IllegalAccessException var12) {
                        throw new AssertionError(var12);
                    }
                }

                if (!memberValueEquals(value, otherValue)) {
                    return false;
                }
            }
            return true;
        }
    }


    private AnnotationInvocationHandler asOneOfUs(Object other) {
        if (Proxy.isProxyClass(other.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(other);
            if (handler instanceof AnnotationInvocationHandler) {
                return (AnnotationInvocationHandler) handler;
            }
        }
        return null;
    }

    private Method[] getMemberMethods() {
        if (this.memberMethods == null) {
            this.memberMethods = AccessController.doPrivileged((PrivilegedAction<Method[]>) () -> {
                Method[] methods = AnnotationInvocationHandler.this.type.getDeclaredMethods();
                AnnotationInvocationHandler.this.validateAnnotationMethods(methods);
                AccessibleObject.setAccessible(methods, true);
                return methods;
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

                //noinspection AlibabaAvoidComplexCondition
                if ((!returnType.isPrimitive() || returnType == Void.TYPE) && returnType != String.class && returnType != Class.class && !returnType.isEnum() && !returnType.isAnnotation()) {
                    notMalformed = false;
                    break;
                }

                String methodName = method.getName();
                //noinspection AlibabaAvoidComplexCondition
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
        Class<?> o1Type = o1.getClass();
        if (!o1Type.isArray()) {
            return o1.equals(o2);
        } else if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals(((Object[]) o1), ((Object[]) o2));
        } else if (o2.getClass() != o1Type) {
            return false;
        } else if (o1Type == byte[].class) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        } else if (o1Type == char[].class) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        } else if (o1Type == double[].class) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        } else if (o1Type == float[].class) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        } else if (o1Type == int[].class) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        } else if (o1Type == long[].class) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        } else if (o1Type == short[].class) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        } else {
            assert o1Type == boolean[].class;

            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
    }

    private String toStringImpl() {
        StringBuilder builder = new StringBuilder(128);
        builder.append('@');
        builder.append(this.type.getName());
        builder.append('(');
        boolean first = true;

        for (Map.Entry<String, Object> entry : this.memberValues.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }

            builder.append(entry.getKey());
            builder.append('=');
            builder.append(memberValueToString(entry.getValue()));
        }

        builder.append(')');
        return builder.toString();
    }

    private static String memberValueToString(Object var0) {
        Class<?> var1 = var0.getClass();
        if (!var1.isArray()) {
            return var0.toString();
        } else if (var1 == byte[].class) {
            return Arrays.toString((byte[]) var0);
        } else if (var1 == char[].class) {
            return Arrays.toString((char[]) var0);
        } else if (var1 == double[].class) {
            return Arrays.toString((double[]) var0);
        } else if (var1 == float[].class) {
            return Arrays.toString((float[]) var0);
        } else if (var1 == int[].class) {
            return Arrays.toString((int[]) var0);
        } else if (var1 == long[].class) {
            return Arrays.toString((long[]) var0);
        } else if (var1 == short[].class) {
            return Arrays.toString((short[]) var0);
        } else {
            return var1 == boolean[].class ? Arrays.toString((boolean[]) var0) : Arrays.toString((Object[]) var0);
        }
    }


    private Object cloneArray(Object var1) {
        Class<?> var2 = var1.getClass();
        if (var2 == byte[].class) {
            byte[] var6 = (byte[]) var1;
            return var6.clone();
        } else if (var2 == char[].class) {
            char[] var5 = (char[]) var1;
            return var5.clone();
        } else if (var2 == double[].class) {
            double[] var4 = (double[]) var1;
            return var4.clone();
        } else if (var2 == float[].class) {
            float[] var11 = (float[]) var1;
            return var11.clone();
        } else if (var2 == int[].class) {
            int[] var10 = (int[]) var1;
            return var10.clone();
        } else if (var2 == long[].class) {
            long[] var9 = (long[]) var1;
            return var9.clone();
        } else if (var2 == short[].class) {
            short[] var8 = (short[]) var1;
            return var8.clone();
        } else if (var2 == boolean[].class) {
            boolean[] var7 = (boolean[]) var1;
            return var7.clone();
        } else {
            Object[] var3 = (Object[]) var1;
            return var3.clone();
        }
    }

    private int hashCodeImpl() {
        int var1 = 0;

        Map.Entry<String, Object> var3;
        for (Iterator<Map.Entry<String, Object>> var2 = this.memberValues.entrySet().iterator(); var2.hasNext(); var1 += 127 * var3.getKey().hashCode() ^ memberValueHashCode(var3.getValue())) {
            var3 = var2.next();
        }

        return var1;
    }

    private static int memberValueHashCode(Object var0) {
        Class<?> var1 = var0.getClass();
        if (!var1.isArray()) {
            return var0.hashCode();
        } else if (var1 == byte[].class) {
            return Arrays.hashCode((byte[]) var0);
        } else if (var1 == char[].class) {
            return Arrays.hashCode((char[]) var0);
        } else if (var1 == double[].class) {
            return Arrays.hashCode((double[]) var0);
        } else if (var1 == float[].class) {
            return Arrays.hashCode((float[]) var0);
        } else if (var1 == int[].class) {
            return Arrays.hashCode((int[]) var0);
        } else if (var1 == long[].class) {
            return Arrays.hashCode((long[]) var0);
        } else if (var1 == short[].class) {
            return Arrays.hashCode((short[]) var0);
        } else {
            return var1 == boolean[].class ? Arrays.hashCode((boolean[]) var0) : Arrays.hashCode((Object[]) var0);
        }
    }
}
package org.util;

import java.lang.reflect.Method;

/**
 *   @desc : 反射工具，通过类名和函数名称调用函数
 *   @auth : tyf
 *   @date : 2024-10-30 14:53:56
 */
public class ReflectionTools {

    // 通过类名和方法名称调用非静态方法
    public static Object invokeNonStaticMethodByName(Class<?> clazz, String methodName, Object instance, Object... params) throws Exception {
        // 获取参数类型数组
        Class<?>[] paramTypes = null;

        if (params != null) {
            paramTypes = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                // 如果参数为null，需要特别处理
                if (params[i] == null) {
                    paramTypes[i] = Object.class; // 使用 Object.class 表示参数类型为 Object
                } else {
                    paramTypes[i] = params[i].getClass();
                }
            }
        } else {
            paramTypes = new Class[0]; // 如果 params 为 null，设置参数类型数组为空
        }

        // 获取指定方法
        Method method = clazz.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true); // 如果方法是私有的，设置为可访问
        // 调用方法并返回结果
        return method.invoke(instance, params);
    }

    // 通过类名和方法名称调用静态方法
    public static Object invokeStaticMethodByName(Class<?> clazz, String methodName, Object... params) throws Exception {
        // 获取参数类型数组
        Class<?>[] paramTypes = null;

        if (params != null) {
            paramTypes = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                // 如果参数为null，需要特别处理
                if (params[i] == null) {
                    paramTypes[i] = Object.class; // 使用 Object.class 表示参数类型为 Object
                } else {
                    paramTypes[i] = params[i].getClass();
                }
            }
        } else {
            paramTypes = new Class[0]; // 如果 params 为 null，设置参数类型数组为空
        }

        // 获取指定方法
        Method method = clazz.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true); // 如果方法是私有的，设置为可访问
        // 调用静态方法并返回结果
        return method.invoke(null, params);
    }

    // 直接传入非静态 Method 对象进行调用
    public static Object invokeNonStaticMethodByMethod(Method method, Object instance, Object... params) throws Exception {
        method.setAccessible(true); // 如果方法是私有的，设置为可访问
        return method.invoke(instance, params);
    }

    // 直接传入静态 Method 对象进行调用
    public static Object invokeStaticMethodByMethod(Method method, Object... params) throws Exception {
        method.setAccessible(true); // 如果方法是私有的，设置为可访问
        return method.invoke(null, params);
    }
}

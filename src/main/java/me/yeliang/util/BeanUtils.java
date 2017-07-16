package me.yeliang.util;

import java.lang.reflect.*;


/**
 * 一些反射方法的封装
 */
public class BeanUtils {

    public static <T> T instanceClass(Class<T> clazz){
        if(!clazz.isInterface()){
            try {
                return clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static <T> T instanceClass(Constructor<T> ctor, Object... args)
            throws IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        makeAccessible(ctor);
        return ctor.newInstance(args);//调用构造方法实例化
    }


    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes){
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            return findDeclaredMethod(clazz, methodName, paramTypes);//返回共有的方法
        }
    }

    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes){
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null) {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
            return null;
        }
    }

    public static Method [] findDeclaredMethods(Class<?> clazz){
        return clazz.getDeclaredMethods();
    }

    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers())
                || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
                && !ctor.isAccessible()) {
            ctor.setAccessible(true);//如果是私有的 设置为true 使其可以访问
        }
    }

    public static Field[] findDeclaredFields(Class<?> clazz){
        return clazz.getDeclaredFields();
    }
}
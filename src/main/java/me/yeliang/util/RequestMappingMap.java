package me.yeliang.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 路径-方法 存储map
 */
public class RequestMappingMap {

    private static Map<String, Class<?>> requesetMap = new HashMap<String, Class<?>>();

    public static Class<?> getClassName(String path) {
        return requesetMap.get(path);
    }


    public static void put(String path, Class<?> className) {
        requesetMap.put(path, className);
    }

    public static Map<String, Class<?>> getRequesetMap() {
        return requesetMap;
    }
}
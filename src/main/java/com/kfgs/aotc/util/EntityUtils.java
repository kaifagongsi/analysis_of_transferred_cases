package com.kfgs.aotc.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaobu
 * @version JDK1.8.0_171
 * @date on  2018/11/16 11:06
 * @description V1.0
 */
@Slf4j
public class EntityUtils {

    /**
     * 数组集合转化为指定对象集合
     * 指定的实体对象必须包含所以字段的构造方法，数组的元素的顺序将和构造方法顺序和类型一一对应
     *
     * @param list  集合
     * @param clazz c
     * @param <T>   类型
     * @return List<T>
     */
    public static <T> List<T> castEntity(List<Object[]> list, Class<T> clazz) {
        List<T> returnList = new ArrayList<>();
        if (list.size() == 0) {
            return returnList;
        }
        Class[] c2 = null;
        Constructor[] constructors = clazz.getConstructors();
        for (Constructor constructor : constructors) {
            Class[] tClass = constructor.getParameterTypes();
            if (tClass.length == list.get(0).length) {
                c2 = tClass;
                break;
            }
        }
        //构造方法实例化对象
        for (Object[] o : list) {
            Constructor<T> constructor = null;
            try {
                constructor = clazz.getConstructor(c2);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                assert constructor != null;
                returnList.add(constructor.newInstance(o));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return returnList;
    }
}

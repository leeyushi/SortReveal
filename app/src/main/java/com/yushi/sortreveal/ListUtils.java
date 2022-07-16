package com.yushi.sortreveal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class ListUtils<T> extends ArrayList<T> {


    @SafeVarargs
    public final ListUtils<T> add(T... t) {
        addAll(Arrays.asList(t));
        return this;
    }

    public ListUtils<T> addList(List<T> list) {
        addAll(list);
        return this;
    }

    public ListUtils<T> arraysToList(T[] ts) {
        addAll(Arrays.asList(ts));
        return this;
    }

    /**
     * 更新数据
     *
     * @param oldData 数据源
     * @param newData 新的数据
     * @param append  是否追加数据，默认为false
     */
    public static <T> void updateListData(List<T> oldData, List<T> newData, boolean... append) {
        if (append.length == 0 || append[0]) oldData.clear();
        if (newData != null) oldData.addAll(newData);
    }

    /**
     * 深度拷贝List
     *
     * @param src 目标list
     * @return list
     */
    public static <T extends Serializable> List<T> deepCopy(List<T> src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
            out.flush();
            out.close();
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            List<T> dest = (List<T>) in.readObject();
            return dest;
        } catch (IOException ignored) {

        } catch (ClassNotFoundException ignored) {

        }
        return null;

    }





    /**
     * 从List<Map<String,Object>>里获取指定key 为List
     *
     * @param maps maps
     * @param key  key
     * @return List<String>
     */
    public static <T> List<T> listMapToList(List<Map<String, Object>> maps, String key) {
        List<T> tList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            if (map.get(key) != null) {
                T value = (T) map.get(key);
                tList.add(value);
            }
        }
        return tList;
    }

    /**
     * 把List截取到指定长度，返回新List
     *
     * @param tList  tList
     * @param length length
     */
    public static <T> List<T> listToLengthBack(List<T> tList, int length) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (i < tList.size())
                result.add(tList.get(i));
        }
        return result;
    }

    /**
     * 把源List截取到指定长度
     *
     * @param tList  tList
     * @param length length
     */
    public static <T> void listToLength(List<T> tList, int length) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (i < tList.size())
                result.add(tList.get(i));
        }
        tList.clear();
        tList.addAll(result);
    }
}

package yu.rainash.yix.patchmaker.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class ReflectUtils {

    public static Object invokeMethod(Class clz, Object instance, String methodName, Class[] classes, Object... params) throws MethodInvokeException {
        Method method = null;
        try {
            method = clz.getDeclaredMethod(methodName, classes);
            method.setAccessible(true);
            return method.invoke(instance, params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MethodInvokeException(methodName);
        }
    }

    public static Object invokeMethod(Class clz, Object instance, String methodName, Object... params) throws MethodInvokeException{
        Class[] parameterTypes = null;
        if (params != null) {
            parameterTypes = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                parameterTypes[i] = params[i].getClass();
            }
        }
        return invokeMethod(clz, instance, methodName, parameterTypes, params);
    }

    public static Object getField(Class clz, Object instance, String fieldName) {
        try {
            Field field = clz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean setField(Class clz, Object instance, String fieldName, Object newField) {
        try {
            Field field = clz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, newField);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void printAllField(Class clz, Object instance) {
        Field[] field = clz.getDeclaredFields();
        for (Field f : field) {
            f.setAccessible(true);
            try {
                System.out.println(f.getName() + ": " + f.get(instance));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class MethodInvokeException extends Exception {

        public MethodInvokeException(String methodName) {
            super("exception occurred where invoking method " + methodName);
        }

    }

}

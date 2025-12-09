package com.excel.autoExcel.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class TypeUtils {
    private TypeUtils() {}

    /** List<T> 에서 T 반환 */
    public static Type firstArg(Type type) {
        if(type instanceof ParameterizedType) {
            Type[] a = ((ParameterizedType) type).getActualTypeArguments();
            if(a.length > 0) {
                return a[0];
            }

        }
        return Object.class;
    }

    /** Type -> Raw Class (없으면 null)*/
    public static Class<?> toRaw(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            Type r = ((ParameterizedType) type).getRawType();
            if (r instanceof Class<?>) {}
        }
        return null;
    }

    /** 단순 타입 여부 */
    public static boolean isLeaf(Class<?> c) {
        return c.isPrimitive() || Number.class.isAssignableFrom(c)
                || CharSequence.class.isAssignableFrom(c)
                ||Boolean.class.equals(c)
                ||java.util.Date.class.isAssignableFrom(c)
                ||java.time.temporal.Temporal.class.isAssignableFrom(c);
    }

    /** 타입 짧은 이름 (ex : List -> List) */
    public static String javaName(Type type) {
        if (type instanceof Class<?>) {
            return ((Class<?>) type).getSimpleName();
        }
        if (type instanceof ParameterizedType) {
            Type raw = ((ParameterizedType) type).getRawType();
            if (raw instanceof Class<?>) {
                return ((Class<?>) raw).getSimpleName();
            }
        }
        return String.valueOf(type);
    }

    /** 길이(문자/숫자 기본 값) */
    public static String lengthHint(Class<?> c) {
        if (CharSequence.class.isAssignableFrom(c)) {
            return "50";
        }
        if (Number.class.isAssignableFrom(c)) {
            return "18";
        }
        return "-";
    }

    /** 한글 라벨 표시용 포멧 */
    public static String display(String path) {
        return path.replace(".","·");
    }

    /** null -> "" */
    public static String nvl(String s) {
        return s == null ? "" : s;
    }
}

package com.excel.autoExcel.util;

import com.excel.autoExcel.vo.FieldRow;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;

import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.excel.autoExcel.util.DocUtils.description;

public class SpecIntrospector {



    //루트 클래스를 받아 필드 행 생성
    public static List<FieldRow> introspect(String interfaceId,String ioType,Class<?> root) {
        List<FieldRow> fieldRows = new ArrayList<>();
        Set<Type> types = new HashSet<>();
        walk(root, "", ioType,interfaceId,fieldRows, types);
        return fieldRows;
    }



    private static FieldRow buildRow (String ioType , String path, Schema schema, Class<?> javaType, boolean required) {
        String desc = (schema != null) ? schema.description() : null;
        String example = (schema != null) ? schema.example() : null;
        String enums = (schema != null && schema.allowableValues().length > 0) ? String.join(",", schema.allowableValues()) : null;
         return  FieldRow.builder()
                 .ioType(ioType)
                 .path(path)
                 .javaType(javaType.getSimpleName())
                 .required(required)
                 .description(desc)
                 .example(example)
                 .enums(enums)
                 .build();
    }

    private static void walk(Type type, String basePath, String ioType, String interfaceId,
                             List<FieldRow> filedRowList, Set<Type> visited) {
        if(type == null) return;
        Class<?> raw = getRaw(type);
        if(raw == null) return;

        if (!isLeaf(raw) && !visited.add(type)) return; //순환 참조 방지

        for(Field field : raw.getDeclaredFields()) {
            field.setAccessible(true);


            String desc = DocUtils.description(field);
            String ex = DocUtils.example(field);
            String enumStr = DocUtils.enums(field,getRaw(field.getGenericType()));
            boolean req = DocUtils.required(field);

            String name = field.getName();
            //경로
            String path = basePath.isEmpty() ? name : basePath + "." + name;
            Type tp = field.getGenericType();

            Class<?> r = getRaw(tp);

        }
        //ENUM
        if(raw.isEnum()) {
            filedRowList.add(FieldRow.builder()
                            .interfaceId(interfaceId)
                            .ioType(ioType)
                            .path(basePath.isEmpty() ? raw.getSimpleName() : basePath)
                            .javaType(raw.getSimpleName())
                            .required(false)
                            .description(null)
                            .example(null)
                            .enums(String.join(",", enumConstants(raw)))
                            .build());
        }


        //단순 타입이면 더 탐색하지 않음
        if (isLeaf(raw)) {
            return;
        }
    }


    private static boolean isLeaf(Class<?> raw) {
        if(raw.isPrimitive()) return true;
        if (Number.class.isAssignableFrom(raw)) return true;
        if(CharSequence.class.equals(raw)) return true;
        if(Boolean.class.equals(raw)) return true;
        if(java.util.Date.class.isAssignableFrom(raw)) return true;
        if(java.time.temporal.Temporal.class.isAssignableFrom(raw)) return true;
        return false;

    }

    private static Class<?> getRaw(Type type) {
        if(type instanceof Class<?>) return (Class<?>) type;
        if(type instanceof ParameterizedType param) {
            Type raw = param.getRawType();
            if(raw instanceof Class<?>) return (Class<?>) raw;

        }

        return null;
    }

    private static String typeName(Type type) {
        Class<?> raw = getRaw(type);
        if(raw == null) return String.valueOf(type);
        if(raw.isArray()) return raw.getComponentType().getSimpleName() + "[]";
        if (Collection.class.isAssignableFrom(raw)){
            Type elem =firstGenericArg(type);
            Class<?> e = getRaw(elem);
            return "List<" + (e != null ? e.getSimpleName() : "?") + ">";
        }
        return raw.getSimpleName();
    }
    private static List<String> enumConstants(Class<?> raw) {
        Object[] arr = raw.getEnumConstants();
        List<String> list = new ArrayList<>();
        if (arr != null) for(Object o : arr) list.add(((Enum<?>) o).name());
        return list;
    }

    private static Type firstGenericArg(Type type) {
        if(type instanceof ParameterizedType param) {
            Type[] args = param.getActualTypeArguments();
            if(args.length > 0) return args[0];
        }

        return Object.class;
    }
}


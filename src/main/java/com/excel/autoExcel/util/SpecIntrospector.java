package com.excel.autoExcel.util;

import com.excel.autoExcel.vo.FieldRow;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpecIntrospector {

    public static List<FieldRow> introspectReq(Class<?> requestVo) {
        return introspectWithTag(requestVo, "REQUEST");
    }

    public static List<FieldRow> introspectResp(Class<?> responseVo) {
        return introspectWithTag(responseVo,"RESPONSE");

    }

    private static List<FieldRow> introspectWithTag (Class<?> rootClass, String ioType) {
        List<FieldRow> acc = new ArrayList<>();
        Type(rootClass,"",acc, new HashSet<>(), ioType);

        return acc;

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

    private static void Type(Type type, String basePath, List<FieldRow> acc, Set<Type> visited, String ioType) {
        if(type == null) return;
        if (visited.contains(type)) return; //순환 참조 방지
        visited.add(type);

        Class<?> raw = getRaw(type);
        if(raw == null) return;

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

    private static Type firstGenericArg(Type type) {
        if(type instanceof ParameterizedType param) {
            Type[] args = param.getActualTypeArguments();
            if(args.length > 0) return args[0];
        }

        return Object.class;
    }
}


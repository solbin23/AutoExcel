package com.excel.autoExcel.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

/** 필드/타입에서 설명/예시/필수/enum 정보를 가져오는 유틸*/
public final class DocUtils {

    private DocUtils(){

    }

    //description
    public static String description(AnnotatedElement annotatedElement){
        // Swagger v3
        Schema v3 = annotatedElement.getAnnotation(Schema.class);
        if(v3 != null && !v3.description().isBlank()) return v3.description();

        JsonPropertyDescription jsonPropertyDescription = annotatedElement.getAnnotation(JsonPropertyDescription.class);
        if(jsonPropertyDescription != null && !jsonPropertyDescription.value().isBlank()) return jsonPropertyDescription.value();

        return null;
    }


    //example
    public static  String example(AnnotatedElement annotatedElement){
        Schema v3 = annotatedElement.getAnnotation(Schema.class);
        if(v3 != null && !v3.example().isBlank()) return v3.example();
        return null;

    }

    //enum
    public static String enums(AnnotatedElement annotatedElement, Class<?> rawType){
        Schema v3 = annotatedElement.getAnnotation(Schema.class);
        if (v3 != null && v3.allowableValues().length >0) {
            return String.join(",", v3.allowableValues());
        }
        if (rawType != null && rawType.isEnum()) {
            List<String> names = new ArrayList<>();
            Object[] arr = rawType.getEnumConstants();
            if(arr != null) {
                for (Object o : arr) {
                    names.add(((Enum<?>)o).name());
                    return String.join(",", names);
                }

            }
        }
        return null;
    }

    public static boolean required(AnnotatedElement annotatedElement){
        if(has(annotatedElement,"jakarta.validation.constraints.NotNull")) return true;
        if(has(annotatedElement,"jakarta.validation.constraints.NotBlank")) return true;
        if(has(annotatedElement,"jakarta.validation.constraints.NotEmpty")) return true;

        if(has(annotatedElement,"javax.validation.constraints.NotNull")) return true;
        if(has(annotatedElement,"javax.validation.constraints.NotEmpty")) return true;
        if(has(annotatedElement,"javax.validation.constraints.NotBlank")) return true;

        Schema v3 = annotatedElement.getAnnotation(Schema.class);
        if (v3 != null && v3.required()) {
            return true;
        }

        JsonProperty jsonProperty = annotatedElement.getAnnotation(JsonProperty.class);
        return jsonProperty!=null && jsonProperty.required();
    }

    private static boolean has(AnnotatedElement annotatedElement,String fn){
        try{
            Class<?> c = Class.forName(fn);
            return annotatedElement.getAnnotation((Class<? extends Annotation>) c) != null;
        } catch (Throwable ignore) {
            return false;
        }
    }
}

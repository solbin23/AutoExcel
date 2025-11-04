package com.excel.autoExcel.mapping;



import com.excel.autoExcel.vo.ExcelRow;

import com.excel.autoExcel.vo.FieldRow;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class FieldMapping {


    private final RequestMappingHandlerMapping handlerMapping;

    //ExcelRow 목록 생성
    public List<ExcelRow> scanAll(String basePackagePre) {

        Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();

        List<ExcelRow> excelRows = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> e : map.entrySet()) {
            HandlerMethod handlerMethod = e.getValue();
            Class<?> controllerClass = handlerMethod.getBeanType();
            if(basePackagePre != null && !controllerClass.getName().startsWith(basePackagePre)) {
                continue;
            }

        }
    return excelRows;
    }


    private ExcelRow buildExcelRow(RequestMappingInfo info, HandlerMethod method) {
        //HTTP Method
        String httpMethod = info.getMethodsCondition().getMethods().isEmpty()
                ? "UNSPECIFIED"
                : info.getMethodsCondition().getMethods().stream().map(Enum::name).collect(Collectors.joining(","));

        // Path
        String path = extractFirstPath(info);

        //content-Type
        String produce = info.getProducesCondition().getProducibleMediaTypes().isEmpty()
                ? null
                : info.getProducesCondition().getProducibleMediaTypes().iterator().next().toString();

        String consumes = info.getConsumesCondition().getConsumableMediaTypes().isEmpty()
                ? null
                : info.getConsumesCondition().getConsumableMediaTypes().iterator().next().toString();

        String contentType = firstNonBlank(produce, consumes, MediaType.APPLICATION_JSON_VALUE);

        // RequestVo
        Class<?> requestVo = resolveRequestBodyType(method.getMethod());

        //ResponseVo
        Class<?> responseVo = resolveResponseBodyType(method.getMethod());

        //InterfaceID
        String interfaceId = resolveInterfaceId(method);

        return ExcelRow.builder()
                .interfaceId(interfaceId)
                .httpMethod(httpMethod)
                .path(path)
                .contentType(contentType)
                .requestClassName(requestVo != null ? requestVo.getName() : "")
                .responseClassName(responseVo != null ? responseVo.getName() : "")
                .build();
    }

    private String extractFirstPath(RequestMappingInfo info) {
        try {
            PathPatternsRequestCondition condition = info.getPathPatternsCondition();
            if(condition != null && !condition.getPatterns().isEmpty()) {
                return condition.getPatternValues().iterator().next();
            }
        } catch (NoSuchMethodError ignore) {

        }
        if(info.getPathPatternsCondition() != null && !info.getPathPatternsCondition().getPatterns().isEmpty()) {
            return info.getPathPatternsCondition().getPatternValues().iterator().next();

        }
        return "";
    }

    private String firstNonBlank(String... values){
        for (String s : values) {
            if(s != null && s.isBlank()){return s;}
        }
        return null;
    }

    private String resolveInterfaceId(HandlerMethod method) {
    Operation operation = method.getMethodAnnotation(Operation.class);
    if(operation != null && ! operation.operationId().isBlank()) {
        return operation.operationId();

    }
    return method.getBeanType().getSimpleName() + "@" + method.getMethod().getName();
     }


    private Class<?> resolveRequestBodyType(Method m) {
        for(Parameter p : m.getParameters()) {
            if(p.isAnnotationPresent(RequestBody.class)) {
                if(HttpEntity.class.isAssignableFrom(p.getType())){
                    Type t = p.getParameterizedType();
                    return unwrapSingleGeneric(t);
                }
                return p.getType();
            }
        }

        for(Parameter p : m.getParameters()) {
            return null;
        }

        return null;
    }






    private Class<?> resolveResponseBodyType(Method m) {
        Type t = m.getGenericReturnType();
        if(t instanceof ParameterizedType pt) {
            Class<?> raw = (Class<?>) pt.getRawType();
            if(HttpEntity.class.isAssignableFrom(raw) || org.springframework.http.ResponseEntity.class.isAssignableFrom(raw)) {
            return unwrapSingleGeneric(t);

            }
        }
        if(t instanceof Class<?>) return (Class<?>) t;
        ResolvableType r = ResolvableType.forMethodReturnType(m);
        return r.resolve();

    }


private Class<?> unwrapSingleGeneric(Type t) {
        if(t instanceof ParameterizedType pt) {

            Type[] types = pt.getActualTypeArguments();
            if(types.length > 0) {
                Type type = types[0];
                if(type instanceof Class<?> ) { return (Class<?>) type;}
                if (type instanceof ParameterizedType pt2 && pt2.getRawType() instanceof Class<?> c) {return c;}
            }
        }

        return Object.class;
}
}

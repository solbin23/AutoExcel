package com.excel.autoExcel.mapping;


import com.excel.autoExcel.vo.ExcelRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FieldMapping {


    private final RequestMappingHandlerMapping handlerMapping;

    //ExcelRow 목록 생성
    public List<ExcelRow> scanAll(String basePackagePre) {

        Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();

    return null;
    }

}

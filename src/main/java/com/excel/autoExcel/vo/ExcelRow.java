package com.excel.autoExcel.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExcelRow {
    private String interfaceId; //인터페이스 ID
    private String httpMethod; // GET , POST 등
    private String path;
    private String contentType; // application/json
    private Class<?> requestClass;
    private Class<?> responseClass;
}

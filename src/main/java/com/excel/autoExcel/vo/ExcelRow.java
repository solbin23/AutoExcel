package com.excel.autoExcel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcelRow {
    private String interfaceId; //인터페이스 ID
    private String httpMethod; // GET , POST 등
    private String path;
    private String contentType; // application/json

    //시트 중간 설명 영역
    private String funcReq;
    private String condReq;
    private String note;

    //VO FQCN
    private Class<?> requestClass;
    private Class<?> responseClass;
}

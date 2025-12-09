package com.excel.autoExcel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecLayout {
    private String interfaceId; //인터페이스 ID
    private String httpMethod; // GET , POST 등
    private String path;
    private String contentType; // application/json
    private String target;
    private String source;

    //시트 중간 설명 영역
    private String funcReq; //기능요건
    private String condReq; //조건요건
    private String note;

    /** 컨버터 사용 시 바인딩 됨 (String -> Class<?>*/
    private Class<?> requestClass;
    private Class<?> responseClass;
}

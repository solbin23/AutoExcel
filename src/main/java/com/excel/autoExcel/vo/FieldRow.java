package com.excel.autoExcel.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldRow {
    private String ioType; // REQUEST , RESPONSE
    private String path;
    private String javaType;  // String, Long, List<String> ...
    private boolean required; //true면 Y
    private String description;
    private String example;
    private String enums;
}

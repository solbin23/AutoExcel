package com.excel.autoExcel.vo;

import lombok.*;

import static com.excel.autoExcel.util.TypeUtils.nvl;

@Data
@Builder
@Value
public class FieldRow {
    private String interfaceId;
    private String ioType; // REQUEST , RESPONSE
    private String path;
    private String javaType;  // String, Long, List<String> ...
    private boolean required; //true면 Y
    private String description;
    private String example;
    private String enums;


    public static FieldRow buildRow(String interfaceId, String ioType, String path, String javaType, boolean required, String description, String example,String enums) {
        return FieldRow.builder()
                .interfaceId(interfaceId)
                .ioType(ioType)
                .path(path)
                .javaType(javaType)
                .required(required)
                .description(nvl(description))
                .example(nvl(example))
                .enums(nvl(enums))
                .build();
    }
}

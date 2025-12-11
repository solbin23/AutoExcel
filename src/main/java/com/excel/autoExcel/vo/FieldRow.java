package com.excel.autoExcel.vo;

import lombok.*;

import static com.excel.autoExcel.util.TypeUtils.nvl;

@Data
@Builder
@Value
public class FieldRow {
     String interfaceId;
     String ioType; // REQUEST , RESPONSE
     String path;
     String javaType;  // String, Long, List<String> ...
     boolean required; //true면 Y
     String description;
     String example;
     String enums;

     boolean groupHeader; //true면 그룹(리스트) 머리행
     String displayPath;

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

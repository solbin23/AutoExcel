package com.excel.autoExcel.controller;


import com.excel.autoExcel.mapping.FieldMapping;
import com.excel.autoExcel.service.ExcelService;
import com.excel.autoExcel.vo.ExcelRow;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auto")
public class ExcelController {

    private final ExcelService excelService;
    private final FieldMapping fieldMapping;

    @GetMapping(value = "/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam String type) throws Exception {
        List<ExcelRow> metas = fieldMapping.scanAll("");



    return null;
    }
}

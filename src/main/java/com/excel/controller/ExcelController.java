package com.excel.controller;


import com.excel.autoExcel.service.ExcelService;
import com.excel.autoExcel.vo.ExcelRow;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auto")
public class ExcelController {

    private final ExcelService excelService;

    @GetMapping(value = "/excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam String type) throws Exception {

        //type 매핑
        ExcelRow meta;
        Class<?> reqClass;
        Class<?> resClass;
        String fileName;


    return null;
    }
}

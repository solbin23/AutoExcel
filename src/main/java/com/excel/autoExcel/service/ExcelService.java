package com.excel.autoExcel.service;

import com.excel.autoExcel.util.SpecIntrospector;
import com.excel.autoExcel.vo.SpecLayout;
import com.excel.autoExcel.vo.FieldRow;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ExcelService {

    public byte[] buildExcel(List<SpecLayout> rows) throws Exception{

        try(XSSFWorkbook wb = new XSSFWorkbook()){

            Sheet sheet = wb.createSheet("HEAD");
            int r = 0;

            //header
            Row headerRow = sheet.createRow(r++);
            String[] headers = {"Interface ID","HTTP Method", "Path","Content-Type", "RequestVo", "ResponseVo"};
            for (int c = 0; c < headers.length; c++) {
                headerRow.createCell(c).setCellValue(headers[c]);
            }

            //row
            for (SpecLayout specLayout : rows) {
                Row v = sheet.createRow(r++);
                int i = 0;
                v.createCell(i++).setCellValue(nvl(specLayout.getInterfaceId()));
                v.createCell(i++).setCellValue(nvl(specLayout.getHttpMethod()));
                v.createCell(i++).setCellValue(nvl(specLayout.getPath()));
                v.createCell(i++).setCellValue(nvl(specLayout.getContentType()));
                v.createCell(i++).setCellValue(specLayout.getRequestClass() != null ? specLayout.getRequestClass().getName() : "" );
                v.createCell(i++).setCellValue(specLayout.getResponseClass() != null ? specLayout.getResponseClass().getName() : "" );
            }


            for (int c = 0; c < headers.length; c++) {
                sheet.autoSizeColumn(c);
            }

            // Field
            Sheet fieldSheet = wb.createSheet("FIELDS");
            int fieldRow = 0;

            Row fieldHeaderRow = fieldSheet.createRow(fieldRow++);
            String[] fieldHeaders = { "IO TYPE", "PATH","TYPE","Required","Description","Example","Enum"};
            for (int c = 0; c<fieldHeaders.length; c++) {
                fieldHeaderRow.createCell(c).setCellValue(fieldHeaders[c]);

            }

            List<FieldRow> allFields = new ArrayList<>();
            for (SpecLayout specLayout : rows) {
                if (specLayout.getRequestClass() != null) {
                    allFields.addAll(SpecIntrospector.introspect(specLayout.getInterfaceId(),"REQUEST", specLayout.getRequestClass()));

                }

                if (specLayout.getResponseClass() != null) {
                    allFields.addAll(SpecIntrospector.introspect(specLayout.getInterfaceId(), "RESPONSE", specLayout.getResponseClass()));
                }
            }

            allFields.sort(Comparator
                    .comparing(FieldRow::getInterfaceId,Comparator.nullsFirst(String::compareTo))
                    .thenComparing(FieldRow::getIoType)
                    .thenComparing(FieldRow::getPath));

            for (FieldRow fr : allFields) {
                Row v = fieldSheet.createRow(fieldRow++);
                int i = 0;
                v.createCell(i++).setCellValue(nvl(fr.getInterfaceId()));
                v.createCell(i++).setCellValue(nvl(fr.getIoType()));
                v.createCell(i++).setCellValue(nvl(fr.getPath()));
                v.createCell(i++).setCellValue(nvl(fr.getJavaType()));
                v.createCell(i++).setCellValue(fr.isRequired() ? "Y" : "N");
                v.createCell(i++).setCellValue(nvl(fr.getDescription()));
                v.createCell(i++).setCellValue(nvl(fr.getExample()));
                v.createCell(i++).setCellValue(nvl(fr.getEnums()));
            }
            for (int c = 0; c < fieldHeaders.length; c++) {
                sheet.autoSizeColumn(c);
            }

            //엑셀로 반환
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            wb.write(stream);
            return stream.toByteArray();

        }

    }


    private String nvl(String s) {
        return (s == null) ? "" : s;
    }
}

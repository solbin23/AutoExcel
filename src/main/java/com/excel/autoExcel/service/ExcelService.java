package com.excel.autoExcel.service;

import com.excel.autoExcel.util.SpecIntrospector;
import com.excel.autoExcel.vo.ExcelRow;
import com.excel.autoExcel.vo.FieldRow;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelService {

    public byte[] buildExcel(ExcelRow excel, Class<?>  reqVoClass, Class<?> resVoClass) throws Exception{

    //1. 리플렉션으로 필드 정보 수집
        List<FieldRow> reqFields = SpecIntrospector.introspectReq(reqVoClass);
        List<FieldRow> resFields = SpecIntrospector.introspectReq(resVoClass);

        List<FieldRow> allFields = new ArrayList<>();
        allFields.addAll(reqFields);
        allFields.addAll(resFields);

        try(XSSFWorkbook wb = new XSSFWorkbook()){

            Sheet sheet = wb.createSheet();
            int r = 0;

            //header
            Row headerRow = sheet.createRow(r++);
            String[] headers = {"Interface ID","HTTP Method", "Path","Content-Type", "RequestVo", "ResponseVo"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            //row
            Row v = sheet.createRow(r++);
            int i = 0;
            v.createCell(i++).setCellValue(nvl(excel.getInterfaceId()));
            v.createCell(i++).setCellValue(nvl(excel.getHttpMethod()));
            v.createCell(i++).setCellValue(nvl(excel.getPath()));
            v.createCell(i++).setCellValue(nvl(excel.getContentType()));
            v.createCell(i++).setCellValue(nvl(excel.getRequestClassName()));
            v.createCell(i++).setCellValue(nvl(excel.getResponseClassName()));

            for (int e = 0; e < headers.length; e++) {
                sheet.autoSizeColumn(e);
            }

            // Field
            Sheet fieldSheet = wb.createSheet("FIELDS");
            int fieldRow = 0;

            Row fieldHeaderRow = fieldSheet.createRow(fieldRow++);
            String[] fieldHeaders = { "IO TYPE", "PATH","TYPE","Required","Description","Example","Enum"};
            for (int e = 0; e<fieldHeaders.length; e++) {
                fieldHeaderRow.createCell(e).setCellValue(fieldHeaders[e]);

            }

            for (FieldRow rowData : allFields) {
                Row row = fieldSheet.createRow(fieldRow);
                int col = 0;
                row.createCell(col++).setCellValue(nvl(rowData.getIoType()));
                row.createCell(col++).setCellValue(nvl(rowData.getPath()));
                row.createCell(col++).setCellValue(nvl(rowData.getJavaType()));
                row.createCell(col++).setCellValue(nvl(rowData.isRequired() ? "Y" : "N"));
                row.createCell(col++).setCellValue(nvl(rowData.getDescription()));
                row.createCell(col++).setCellValue(nvl(rowData.getExample()));
                row.createCell(col++).setCellValue(nvl(rowData.getEnums()));
            }

            for (int e = 0; e < fieldHeaders.length; e++) {
                sheet.autoSizeColumn(e);
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

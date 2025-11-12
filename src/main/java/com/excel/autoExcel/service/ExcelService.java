package com.excel.autoExcel.service;

import com.excel.autoExcel.util.SpecIntrospector;
import com.excel.autoExcel.vo.SpecLayout;
import com.excel.autoExcel.vo.FieldRow;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {

    public byte[] buildExcel(SpecLayout layout) throws Exception{

        try(XSSFWorkbook wb = new XSSFWorkbook()){

            Sheet sheet = wb.createSheet("문서양식");
            int r = 0;

            //header
            Row headerRow = sheet.createRow(r++);
            String[] headers = {"Interface ID","HTTP Method", "Path","Content-Type", "RequestVo", "ResponseVo"};
            for (int c = 0; c < headers.length; c++) {
                headerRow.createCell(c).setCellValue(headers[c]);
            }

            //row
            for (SpecLayout specLayout : layout) {
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

    // 셀 스타일
    private void setWidths(Sheet ws, Map<Integer,Double> widths) {widths.forEach((oneBasedCol, w) -> {
        int zeroBased = oneBasedCol-1;
        int poiWidth = (int) Math.round(w * 256); //문자 폭 기준
        poiWidth = Math.max(0, Math.min(poiWidth, 255 *256));
        ws.setColumnWidth(zeroBased, poiWidth);
    });
    }
    private void merge(Sheet ws, int r1, int c1, int r2, int c2) {
        ws.addMergedRegion(new CellRangeAddress(r1-1,r2-1,c1-1,c2-1));
    }

    private void cell(Sheet ws, int r, int c, String v, CellStyle st) {
        Row row = ws.getRow(r-1);
        if (row == null) {
            row = ws.createRow(r-1);
        }
         Cell cell = row.createCell(c-1);
        if(cell == null) {
            cell = row.createCell(c-1);
            cell.setCellValue(v==null?"":v);
        }
        if(st != null) {
            cell.setCellStyle(st);
        }
    }

    private void text(Sheet ws, int r, int c, String v) {
        cell(ws,r,c,v,wrap(ws));
    }
    private void left(Sheet ws, int r, int c, String v) {
        CellStyle s = wrap(ws);
        s.setAlignment(HorizontalAlignment.LEFT);
        cell(ws,r,c,v,s);
    }

    private void center(Sheet ws, int r, int c) {
        Cell cell = ensureCell(ws,r,c);
        CellStyle ct = ws.getWorkbook().createCellStyle();
        ct.cloneStyleFrom(cell.getCellStyle());
        ct.setAlignment(HorizontalAlignment.CENTER);
        ct.setVerticalAlignment(VerticalAlignment.CENTER);
        cell.setCellStyle(ct);

    }


    private Cell ensureCell(Sheet ws, int r, int c) {
        Row row = ws.getRow(r-1);
        if (row == null) {
            row = ws.createRow(r-1);

        }
        Cell cell = row.getCell(c-1);
        if (cell == null) {
            cell = row.createCell(c-1);
        }
        return  cell;
    }

    private CellStyle wrap(Sheet ws) {
        CellStyle s = ws.getWorkbook().createCellStyle();
        s.setWrapText(true);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;

    }
    private String nvl(String s) {
        return (s == null) ? "" : s;
    }
}

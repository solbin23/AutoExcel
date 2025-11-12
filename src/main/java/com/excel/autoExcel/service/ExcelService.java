package com.excel.autoExcel.service;

import com.excel.autoExcel.util.SpecIntrospector;
import com.excel.autoExcel.vo.SpecLayout;
import com.excel.autoExcel.vo.FieldRow;
import jakarta.persistence.Index;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
public class ExcelService {

    public byte[] buildExcel(SpecLayout layout) throws Exception{

        try(XSSFWorkbook wb = new XSSFWorkbook()){

            Sheet sheet = wb.createSheet("문서양식");
            setWidths(sheet,1,4d,2,16d,3,12d,4,22d,5,14d,6,22d,7,14d,8,22d,9,14d,10,22d,11,16d,12,24d);
            int r = 1;

            //header
            merge(sheet,r,2,r,12);
            cell(sheet,r,2,"항목 LAYOUT", headFill(wb));
            bold(sheet,r,2,12);
            center(sheet,r,2);
            r+=2;



            //엑셀로 반환
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            wb.write(stream);
            return stream.toByteArray();

        }

    }

    // 셀 스타일
    private void setWidths(Sheet ws, Object... pairs) {
        for (int i=0; i < pairs.length; i+=2) {
            int oneBased = (Integer) pairs[i];
            double widthChars = ((Number) pairs[i+1]).doubleValue();
            int width = (int) Math.round(widthChars * 256);
            width = Math.max(0, Math.min(width, 255 * 256));
            ws.setColumnWidth(oneBased -1 ,width);
        }
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

    private void bold(Sheet ws,int r,int c, int size) {
        Cell cell = ensureCell(ws, r, c);
        CellStyle ct = ws.getWorkbook().createCellStyle();
        ct.cloneStyleFrom(cell.getCellStyle());
        Font font = ws.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short)size);
        ct.setFont(font);
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
    private CellStyle fill(XSSFWorkbook wb, short color) {
        CellStyle s = wb.createCellStyle();
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setFillForegroundColor(color);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setWrapText(true);
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
        return s;
    }
    private CellStyle headFill(XSSFWorkbook wb) {
     return fill(wb, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
    }
    private String nvl(String s) {
        return (s == null) ? "" : s;
    }
}

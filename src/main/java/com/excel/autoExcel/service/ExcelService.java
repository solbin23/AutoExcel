package com.excel.autoExcel.service;


import com.excel.autoExcel.vo.SpecLayout;

import org.apache.poi.ss.usermodel.*;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;



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

            //표




            //엑셀로 반환
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            wb.write(stream);
            return stream.toByteArray();

        }

    }

    private int metaRow(Sheet sheet, XSSFWorkbook wb, int r, String k, String v){
        merge(sheet,r,2,r,3);
        cell(sheet,r,2,k,headFill(wb));
        bold(sheet,r,2,10);
        center(sheet,r,2);
        merge(sheet,r,4,r,12);
        cell(sheet,r,4,nullToEmpty(v),wrap(sheet));
        border(sheet,r,2,r,12);
        return r+1;
    }

    private static String nullToEmpty(String str){
        return str==null?"":str;
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
    private void thin(CellStyle cellStyle){
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
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

    private void border(Sheet sheet, int r1, int c1,int r2, int c2) {
        for(int r= r1; r<=r2 ; r++) {
            if(sheet.getRow(r-1) == null) {
                sheet.createRow(r-1);
            }
            for(int c= c1; c<=c2 ; c++) {
                Cell cell = sheet.getRow(r-1).getCell(c-1);
                CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
                cellStyle.cloneStyleFrom(cell.getCellStyle());
                thin(cellStyle);
                cell.setCellStyle(cellStyle);
            }
        }
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

    // ===== 스타일 / 셀 유틸 ====
    private CellStyle wrap(Sheet ws) {
        CellStyle s = ws.getWorkbook().createCellStyle();
        s.setWrapText(true);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;

    }
    private CellStyle fill(XSSFWorkbook wb, String fg) {
        CellStyle s = wb.createCellStyle();
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setFillForegroundColor(IndexedColors.valueOf(fg).getIndex());
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setWrapText(true);
        thin(s);
        return s;
    }
    private CellStyle headFill(XSSFWorkbook wb) {
     return fill(wb, "BDD7EE");
    }

    private CellStyle headerFill(XSSFWorkbook wb) {
        return fill(wb, "DCE6F1");
    }
    private String nvl(String s) {
        return (s == null) ? "" : s;
    }
}

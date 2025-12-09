package com.excel.autoExcel.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.swing.*;

public final class ExcelCellUtils {
    private ExcelCellUtils() {}

    /** 컬럼 폭 설정 */
    public static void setWidths(Sheet sheet, Object... pairs) {
        for (int i = 0; i<pairs.length; i+=2) {
            int oneBaseCol = (Integer) pairs[i];
            double widthChars = ((Number) pairs[i+1]).doubleValue();
            int poiWidth = (int) Math.ceil(widthChars * 256);
            poiWidth = Math.max(0, Math.min(poiWidth,255 * 256));
            sheet.setColumnWidth(oneBaseCol, poiWidth);
        }
    }

    /**병합 */
    public static void merge(Sheet sheet, int r1, int c1, int r2, int c2) {
        sheet.addMergedRegion(new CellRangeAddress(r1-1, c1-1, r2-1, c2-1));
    }

    /** 범위 테두리 */
    public static void border(Sheet sheet, int r1, int c1, int r2, int c2) {
        for (int r = r1; r <= r2; r++) {
            Row row = sheet.getRow(r - 1);
            if (row == null) {
                row = sheet.createRow(r-1);

            }
            for (int c = c1; c <= c2; c++) {
                Cell cell = row.getCell(c-1);
                if (cell == null) {
                    cell = row.createCell(c-1);
                }
                CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
                cellStyle.cloneStyleFrom(cell.getCellStyle());
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cell.setCellStyle(cellStyle);

            }
        }
    }

    /** 셀 보장 생성 */
    public static Cell ensure(Sheet sheet, int r, int c) {
        Row row = sheet.getRow(r-1);
        if (row == null) {
            row = sheet.createRow(r-1);
        }
        Cell cell = row.getCell(c-1);
        if (cell == null) {
            cell = row.createCell(c-1);
        }
        return cell;
    }

    /** 줄바꿈/정렬/볼드 */
    public static void setWrap(Cell cell, boolean wrap) {
        Workbook wb = cell.getSheet().getWorkbook();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.cloneStyleFrom(cell.getCellStyle());
        cellStyle.setWrapText(wrap);
        cell.setCellStyle(cellStyle);
    }


    public static void setBold(Cell cell, int size){
        Workbook wb = cell.getSheet().getWorkbook();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.cloneStyleFrom(cell.getCellStyle());

        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) size);
        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
    }


    public static void setCenter(Cell cell) {
        Workbook wb = cell.getSheet().getWorkbook();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.cloneStyleFrom(cell.getCellStyle());
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cell.setCellStyle(cellStyle);
    }
}

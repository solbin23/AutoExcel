package com.excel.autoExcel.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class ExcelLayoutWriter {
    private ExcelLayoutWriter() {

    }

    /**META 행 (B~C 제목, D~L 값) */
    public static int metaRow(Sheet sheet, Workbook wb, int row, String key, String value) {
        //B~C 병합 : 제목
        ExcelCellUtils.merge(sheet,row,2,row,3);
        Cell c1 = ExcelCellUtils.ensure(sheet,row,2);
        c1.setCellValue(nvl(key));
        c1.setCellStyle(headerFill(wb));
        ExcelCellUtils.setBold(c1,10);
        ExcelCellUtils.setCenter(c1);

        //D~L 병합: 값
        ExcelCellUtils.merge(sheet,row, 4, row,12);
        Cell c2 = ExcelCellUtils.ensure(sheet,row,4);
        c2.setCellValue(nvl(value));
        c2.setCellStyle(wrap(wb));

        //테두리
        ExcelCellUtils.border(sheet,row,2,row,12);
        return row + 1;
    }

    /** 텍스트 블록(기능요건/조건요건/유의사항) */
    public static int textBlock(Sheet sheet, Workbook wb, int row, String title, String text, int height) {
        //B~C 병합 : 제목
        ExcelCellUtils.merge(sheet,row,2,row,3);
        Cell c1 = ExcelCellUtils.ensure(sheet,row,2);
        c1.setCellValue(nvl(title));
        c1.setCellStyle(headerFill(wb));
        ExcelCellUtils.setBold(c1,10);
        ExcelCellUtils.setCenter(c1);

        //D~L 병합 : 내용
        ExcelCellUtils.merge(sheet,row, 4, row + height -1,12);
        Cell c2 = ExcelCellUtils.ensure(sheet,row,4);
        c2.setCellValue(nvlMulti(text));
        c2.setCellStyle(wrapLeft(wb));

        //테두리
        ExcelCellUtils.border(sheet,row,2,row + height -1,12);
        return row + height;
    }


    //===== 스타일 =====

    public static CellStyle wrap(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setWrapText(true);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;

    }

    public static CellStyle wrapLeft(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setAlignment(HorizontalAlignment.LEFT);
        return s;
    }

    public static CellStyle headFill(Workbook wb) {
        CellStyle s = headerFill(wb);
        s.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        return s;
    }

    public static CellStyle headerFill(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setWrapText(true);
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
        return s;
    }

    public static CellStyle titleFill(Workbook wb) {
        CellStyle s = headerFill(wb);
        s.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        return s;
    }


    //==== 문자열 유틸 ====
    public static String nvl(String str) {
        return str == null ? "" : str;
    }
    public static String nvlMulti(String str) {
        return str == null ? "" : str;
    }
}

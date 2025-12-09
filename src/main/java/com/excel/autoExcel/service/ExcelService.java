package com.excel.autoExcel.service;


import com.excel.autoExcel.util.FlattenUtils;
import com.excel.autoExcel.util.TypeUtils;
import com.excel.autoExcel.vo.FieldRow;
import com.excel.autoExcel.vo.SpecLayout;

import org.apache.poi.ss.usermodel.*;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.*;

import static com.excel.autoExcel.util.ExcelCellUtils.*;
import static com.excel.autoExcel.util.ExcelLayoutWriter.*;



@Service
public class ExcelService {

    public byte[] buildExcel(SpecLayout layout) throws Exception{

        try(XSSFWorkbook wb = new XSSFWorkbook()){

            Sheet sheet = wb.createSheet("문서양식");
            setWidths(sheet,1,4d,2,16d,3,12d,4,22d,5,14d,6,22d,7,14d,8,22d,9,14d,10,22d,11,16d,12,24d);
            int r = 1;

            //header
            merge(sheet,r,2,r,12);
            Cell title = ensure(sheet,r,2);
            title.setCellValue("항목 LAYOUT");
            title.setCellStyle(headFill(wb));
            setBold(title, 12);
            setCenter(title);
            r+=2;

            // ==== 메타 표 ====
            r = metaRow(sheet, wb, r, "인터페이스 ID", TypeUtils.nvl(layout.getInterfaceId()));
            r = metaRow(sheet, wb, r, "API URL", TypeUtils.nvl(layout.getPath()));
            r = metaRow(sheet, wb, r, "HTTP Method", TypeUtils.nvl(layout.getHttpMethod()));
            r = metaRow(sheet, wb, r, "Content Type", TypeUtils.nvl(layout.getContentType()));
            r = metaRow(sheet, wb, r, "Request VO", layout.getRequestClass() != null ? layout.getRequestClass().getName() : "");
            r = metaRow(sheet, wb, r, "Response VO", layout.getResponseClass() != null ? layout.getResponseClass().getName() : "");
            r = metaRow(sheet, wb, r, "Target", TypeUtils.nvl(layout.getTarget()));
            r = metaRow(sheet, wb, r, "Source", TypeUtils.nvl(layout.getSource()));
            r++;

            // ==== 기능/조건/유의사항 ====
            r = textBlock(sheet, wb ,r,"기능요건", TypeUtils.nvl(layout.getFuncReq()),3);
            r = textBlock(sheet, wb, r, "조건요건", TypeUtils.nvl(layout.getCondReq()),3);
            r = textBlock(sheet, wb, r, "유의사항", TypeUtils.nvl(layout.getNote()),3);

            // ==== 요청/응답 명세 ====
            List<FieldRow> reqRows = layout.getRequestClass() != null ? FlattenUtils.flatten(layout.getInterfaceId(),"REQUEST",layout.getRequestClass()) : Collections.emptyList();

            //엑셀로 반환
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            wb.write(stream);
            return stream.toByteArray();

        }

    }



}

package com.excel.autoExcel.util;

import com.excel.autoExcel.vo.FieldRow;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.excel.autoExcel.util.TypeUtils.*;

public final class FlattenUtils {

    private FlattenUtils() {}


    /**
     * rootClass를 리플렉션으로 펼쳐 FieldRow 리스트 생성
     * @param interfaceId 인터페이스ID
     * @param ioType      "REQUEST" | "RESPONSE"
     * @param root   VO 클래스
     */

    // ========필드 전개========
    public static List<FieldRow> flatten(String interfaceId, String ioType, Class<?> root){
        List<FieldRow> rows = new ArrayList<>();
        walk(interfaceId, ioType,root,"",rows,new HashSet<Type>());
        return rows;
    }

    private static void walk(String interfaceId,String ioType,Type type, String prefix, List<FieldRow> rows, Set<Type> visited){
        Class<?> raw = toRaw(type);
        if (raw == null) {
            return ;
        }

        if (!isLeaf(raw) && ! raw.isEnum()) {
            if (!visited.add(type)){
                return; //순환 참조 방지
            }
        }

        //List/Collection
        if (java.util.Collection.class.isAssignableFrom(raw)) {
            Type elem = firstArg(type);
            String path = prefix + "[]";
            rows.add(FieldRow.buildRow(interfaceId,ioType, path, javaName(type),false,null,null,enumValuesString(toRaw(elem))));
            walk(interfaceId,ioType,elem,path,rows,visited);
            return ;
        }

        //배열
        if(raw.isArray()){
            Class<?> comp = raw.getComponentType();
            String path = prefix + "[]";
            rows.add(FieldRow.buildRow(interfaceId,ioType, path, comp.getSimpleName() + "[]",false,null,null,enumValuesString(comp)));
            walk(interfaceId,ioType,comp,path,rows,visited);
            return;
        }
    }



    /** Enum 클래스면 모든 상수 이름을 "A,B,C" 형태로 반환 */
    private static String enumValuesString(Class<?> c) {
        if (c == null || !c.isEnum()) return "";
        Object[] arr = c.getEnumConstants();
        if (arr == null || arr.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(((Enum<?>) arr[i]).name());
        }
        return sb.toString();
    }
}

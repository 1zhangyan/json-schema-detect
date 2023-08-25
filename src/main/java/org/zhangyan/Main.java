package org.zhangyan;


import java.util.Collections;
import java.util.List;
import org.zhangyan.dao.StructTreeNodeDo;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String args[]) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String listA = "";
        StructTreeNodeDo doo = new StructTreeNodeDo();
        String strA = objectMapper.writeValueAsString(listA);
        String strB = objectMapper.writeValueAsString(doo.getKey());
        System.out.println(strA);
        System.out.println(strB);

    }

}
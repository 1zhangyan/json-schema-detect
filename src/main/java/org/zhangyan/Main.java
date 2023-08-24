package org.zhangyan;


import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    private static String jsonExample= "";
    private static String listExample = "[{\"fa\" : 21},{\"fb\": 22,\"fc\": 23}]";

    private static String arrayExample = "[[{\"fa\":21}],[]]";

    public static void main(String args[]) throws Exception {

        System.out.println(getJsonStruct(jsonExample));
//        getJsonMapper(arrayExample);
    }

    public static String getJsonMapper(String jsonStr) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonStr);
        return "";
    }

    public static String getJsonStruct(String jsonStr) throws IOException {
        StructTree tree= new StructTree("test",jsonStr);
        return tree.getSchemaStr();
    }

}
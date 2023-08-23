package org.zhangyan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StructTreeNodeConverter {

    //TODO: 考虑 List 且元素不同
    //TODO: 元素字段多时取交集
    private static String PATH_FORMAT = "%s.%s";
    private static int FIST_ELEMENT = 0;
    private static String BLACK_STRING = "";
    private static String UNKNOWN_FORMAT ="\"%s\":\"unknown\"";
    private static String LIST_FORMAT = "\"%s\": [%s]";
    private static String MAP_FORMAT = "\"%s\": {%s}";
    private static String VALUE_FORMAT = "\"%s\": \"%s\"";
    private static String UNKNOWN_INLIST_FORMAT ="\"unknown\"";
    private static String MAP_INLIST_FORMAT = "{%s}";
    private static String LIST_INLIST_FORMAT = "[%s]";
    private static String VALUE_INLIST_FORMAT = "\"%s\"";
    private static String CONNECT_FORMAT= "%s,%s";

    public static String generateSchemaStr(StructTreeNode node) {
        if (node == null || !Utils.isValidStr(node.getKey())) {
            return BLACK_STRING;
        }
        String schemaStr = BLACK_STRING;
        String format = BLACK_STRING;

        if (node.getType() == null || node.getType().equals(StructTreeNode.DataType.UNKNOWN)) {
            format = node.isInList()?UNKNOWN_INLIST_FORMAT:UNKNOWN_FORMAT;
            return String.format(format,node.getKey());
        }
        if (node.getType().equals(StructTreeNode.DataType.MAP)) {
            schemaStr = node.isInList()?String.format(MAP_INLIST_FORMAT,genChildrenSchemaStr(node.getChildren()))
                    :String.format(MAP_FORMAT,node.getKey(),genChildrenSchemaStr(node.getChildren()));
        } else if (node.getType().equals(StructTreeNode.DataType.LIST)){
            schemaStr = node.isInList()
                    ?String.format(LIST_INLIST_FORMAT,genChildrenSchemaStr(node.getChildren()))
                    :String.format(LIST_FORMAT,node.getKey(),genChildrenSchemaStr(node.getChildren()));
        } else {
            schemaStr = node.isInList()?String.format(VALUE_INLIST_FORMAT,node.getType())
                    :String.format(VALUE_FORMAT,node.getKey(),node.getType());
        }
        return schemaStr;
    }

    private static String genChildrenSchemaStr(List<StructTreeNode> children) {
        if ( children == null || children.isEmpty()) {
            return BLACK_STRING;
        }
        String schemaStr = BLACK_STRING;
        for (StructTreeNode child : children) {
            String curSchema = generateSchemaStr(child);
            if (schemaStr == BLACK_STRING) {
                schemaStr = curSchema;
            } else {
                schemaStr = String.format(CONNECT_FORMAT, schemaStr, curSchema);
            }
        }
        return schemaStr;
    }

    public static StructTreeNode generateTreeFromJsonExample(String exampleJsonStr, String structName) {
        StructTreeNode structTreeNode = null;
        if (Utils.isValidStr(exampleJsonStr) && Utils.isValidStr(structName)) {

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(exampleJsonStr);
                structTreeNode = generateTreeNode(structName, 1, jsonNode, "", false);
            } catch (IOException exception) {

            }
        }
        return structTreeNode;
    }
    private static StructTreeNode generateTreeNode(String key, int layer, JsonNode jsonNode, String parentPath, boolean isParentList) {
        if (jsonNode == null) {
            return null;
        }
        StructTreeNode treeNode = new StructTreeNode(key);
        treeNode.setKey(key);
        treeNode.setLayer(layer);
        treeNode.setType(jsonNode);
        String currentPath = BLACK_STRING;
        if (parentPath.equals(BLACK_STRING)) {
            currentPath = key;
        } else {
            currentPath = String.format(PATH_FORMAT,parentPath,key);
        }
        treeNode.setPath(currentPath);
        treeNode.setInList(isParentList);
        if (jsonNode.isObject()) {
            List<StructTreeNode> children = new ArrayList<>();
            Iterator<String> iterator =  jsonNode.fieldNames();
            while (iterator.hasNext()) {
                String field  = iterator.next();
                StructTreeNode child = generateTreeNode(field,layer + 1, jsonNode.get(field), currentPath, false);
                children.add(child);
            }
            treeNode.setChildren(children);
        } else if (jsonNode.isArray() && jsonNode.size() > 0) {
            jsonNode.get(FIST_ELEMENT);
            StructTreeNode child = generateTreeNode(treeNode.getKey(),layer + 1, jsonNode.get(0), currentPath, true);
            treeNode.setChildren(Collections.singletonList(child));
        } else if (jsonNode.isValueNode()) {
            //
        }
        if (treeNode.getChildren() != null) {
            Collections.sort(treeNode.getChildren());
        }
        return treeNode;
    }
}

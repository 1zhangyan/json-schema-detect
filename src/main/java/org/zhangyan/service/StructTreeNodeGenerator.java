package org.zhangyan.service;

import static org.zhangyan.utils.Utils.BLANCK_STRING;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.zhangyan.utils.Utils;
import org.zhangyan.data.StructTreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StructTreeNodeGenerator {
    private static String PATH_FORMAT = "%s.%s";
    private static int FIST_ELEMENT = 0;
    private static String UNKNOWN_FORMAT ="\"%s\":\"unknown\"";
    private static String LIST_FORMAT = "\"%s\": [%s]";
    private static String MAP_FORMAT = "\"%s\": {%s}";
    private static String VALUE_FORMAT = "\"%s\": \"%s\"";
    private static String UNKNOWN_INLIST_FORMAT ="\"unknown\"";
    private static String MAP_INLIST_FORMAT = "{%s}";
    private static String LIST_INLIST_FORMAT = "[%s]";
    private static String VALUE_INLIST_FORMAT = "\"%s\"";
    private static String CONNECT_FORMAT= "%s,%s";

    private static String LIST_KEY = "@list";

    public static String generateSchemaStr(StructTreeNode node) {
        if (node == null || !Utils.isValidStr(node.getKey())) {
            return BLANCK_STRING;
        }
        String schemaStr = BLANCK_STRING;
        String format = BLANCK_STRING;

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
            return BLANCK_STRING;
        }
        String schemaStr = BLANCK_STRING;
        for (StructTreeNode child : children) {
            String curSchema = generateSchemaStr(child);
            if (schemaStr == BLANCK_STRING) {
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
                structTreeNode = generateTreeNode(structName, jsonNode, BLANCK_STRING, false);
            } catch (IOException exception) {

            }
        }
        return structTreeNode;
    }
    private static StructTreeNode generateTreeNode(String key, JsonNode jsonNode, String parentPath, boolean isParentList) {
        if (jsonNode == null) {
            return null;
        }
        StructTreeNode treeNode = new StructTreeNode(key);
        treeNode.setKey(key);
        treeNode.setType(jsonNode);
        String currentPath = BLANCK_STRING;
        if (parentPath.equals(BLANCK_STRING)) {
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
                StructTreeNode child = generateTreeNode(field, jsonNode.get(field), currentPath, false);
                children.add(child);
            }
            treeNode.setChildren(children);
        } else if (jsonNode.isArray() && jsonNode.size() > 0) {
            jsonNode.get(FIST_ELEMENT);
            StructTreeNode child = generateTreeNode(LIST_KEY, jsonNode.get(0), currentPath, true);
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

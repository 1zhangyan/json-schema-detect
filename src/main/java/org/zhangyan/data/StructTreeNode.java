package org.zhangyan.data;


import static org.zhangyan.utils.Utils.BLANCK_STRING;

import java.util.Collections;
import java.util.List;
import org.zhangyan.utils.Utils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class StructTreeNode implements Comparable {

    private Long id = 0L;
    private String key = BLANCK_STRING;
    private DataType type = DataType.UNKNOWN;
    private String path = BLANCK_STRING;
    private boolean inList = false;
    private boolean uncertainType = false;

    private List<StructTreeNode> children = Collections.emptyList();

    public StructTreeNode(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<StructTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<StructTreeNode> children) {
        this.children = children;
    }

    public void setInList(boolean inList) {
        this.inList = inList;
    }

    public boolean isInList() {
        return inList;
    }

    public void setType(JsonNode node) {
        if (node.getNodeType() == JsonNodeType.BOOLEAN) {
            this.setType(DataType.BOOLEAN);
        } else if (node.getNodeType() == JsonNodeType.NUMBER && node.numberType() == JsonParser.NumberType.INT) {
            this.setType(DataType.INT);
        } else if (node.getNodeType() == JsonNodeType.NUMBER && node.numberType() == JsonParser.NumberType.LONG) {
            this.setType(DataType.LONG);
        } else if (node.getNodeType() == JsonNodeType.NUMBER && node.numberType() == JsonParser.NumberType.FLOAT) {
            this.setType(DataType.FLOAT);
        } else if (node.getNodeType() == JsonNodeType.NUMBER && node.numberType() == JsonParser.NumberType.DOUBLE) {
            this.setType(DataType.DOUBLE);
        } else if (node.getNodeType() == JsonNodeType.STRING) {
            this.setType(DataType.STRING);
        } else if (node.isArray()) {
            this.setType(DataType.LIST);
        } else if (node.isObject()) {
            this.setType(DataType.MAP);
        } else {
            this.setType(DataType.UNKNOWN);
        }
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof StructTreeNode) {
            return getKey().compareTo(((StructTreeNode) o).getKey());
        }
        throw new RuntimeException("Not StructTreeNode Class can not compare!");
    }

    public boolean isUncertainType() {
        return uncertainType;
    }

    public void setUncertainType(boolean uncertainType) {
        this.uncertainType = uncertainType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public enum DataType {

        BOOLEAN(0, "boolean", "布尔型"),
        SHORT(1, "short", "短整型"),
        INT(2, "int", "整型，整型默认值"),
        LONG(3, "long", "长整型"),
        FLOAT(4, "float", "浮点型，浮点默认值"),
        DOUBLE(5, "double", "双精度浮点"),
        STRING(6, "string", "字符串类型"),
        LIST(7, "list", "列表型"),
        MAP(8, "map", "字典型"),
        UNKNOWN(9, "unknown", "未知类型");
        private int order;
        private String name;
        private String description;

        public DataType getDataType(String name) {
            Utils.validStr(name);
            if (BOOLEAN.equals(name)) {
                return BOOLEAN;
            } else if (SHORT.equals(name)) {
                return SHORT;
            } else if (INT.equals(name)) {
                return INT;
            } else if (LONG.equals(name)) {
                return LONG;
            } else if (FLOAT.equals(name)) {
                return FLOAT;
            } else if (DOUBLE.equals(name)) {
                return DOUBLE;
            } else if (STRING.equals(name)) {
                return STRING;
            } else if (LIST.equals(name)) {
                return LIST;
            } else if (MAP.equals(name)) {
                return MAP;
            } else {
                return UNKNOWN;
            }
        }

        DataType(int order, String name, String description) {
            this.name = name;
            this.order = order;
        }

        public boolean isNumber() {
            return getOrder() > SHORT.getOrder()
                    && getOrder() < DOUBLE.getOrder();
        }

        public boolean equals(DataType dataType) {
            return this.name.equals(dataType.name) && this.order == dataType.order;
        }

        public boolean equals(int dataType) {
            return this.order == dataType;
        }

        public boolean equals(String dataType) {
            return this.name.equals(dataType);
        }

        @Override
        public String toString() {
            return this.name;
        }

        public String getDescription() {
            return description;
        }

        public int getOrder() {
            return order;
        }

        public String getName() {
            return name;
        }
    }
}

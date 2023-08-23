package org.zhangyan;


public class StructTree {


    private String structName;

    private String exampleJsonStr;

    private String schemaStr;

    private StructTreeNode structTreeNode;

    public StructTree() {

    }
    public StructTree(String structName) {
        this.structName = structName;
    }

    public StructTree(String structName, String exampleJsonStr) {
        this.structName = structName;
        this.exampleJsonStr = exampleJsonStr;
    }
    public String getStructName() {
        return structName;
    }

    public void setStructName(String structName) {
        this.structName = structName;
    }

    public StructTreeNode getStructTreeNode() {
        return structTreeNode;
    }

    public void setStructTreeNode(StructTreeNode structTreeNode) {
        this.structTreeNode = structTreeNode;
    }

    public void generateTree() {
        if (!Utils.isValidStr(structName) && Utils.isValidStr(exampleJsonStr)) {
             return;
        }
        setStructTreeNode(StructTreeNodeConverter.generateTreeFromJsonExample(this.exampleJsonStr , this.structName));
    }

    public void generateSchemaStr() {
        setSchemaStr(StructTreeNodeConverter.generateSchemaStr(this.structTreeNode));
    }

    public String getSchemaStr() {
        return schemaStr;
    }

    public void setSchemaStr(String schemaStr) {
        this.schemaStr = schemaStr;
    }

    public String getExampleJsonStr() {
        return exampleJsonStr;
    }

    public void setExampleJsonStr(String exampleJsonStr) {
        this.exampleJsonStr = exampleJsonStr;
    }
}

package org.zhangyan.data;


import org.zhangyan.converter.StructTreeNodeConverter;
import org.zhangyan.utils.Utils;

public class StructTree {


    private String structName;

    private String exampleJsonStr;

    private StructTreeNode rootNode;


    public StructTree(String structName, String exampleJsonStr) {
        this.structName = structName;
        this.exampleJsonStr = exampleJsonStr;
        generateTree();

    }
    public String getStructName() {
        return structName;
    }

    private void generateTree() {
        if (!Utils.isValidStr(structName) && Utils.isValidStr(exampleJsonStr)) {
             return;
        }
        setRootNode(StructTreeNodeConverter.generateTreeFromJsonExample(this.exampleJsonStr , this.structName));
    }
    public String getSchemaStr() {
        return StructTreeNodeConverter.generateSchemaStr(this.rootNode);
    }

    public String getExampleJsonStr() {
        return exampleJsonStr;
    }

    public void setExampleJsonStr(String exampleJsonStr) {
        this.exampleJsonStr = exampleJsonStr;
    }

    public void setRootNode(StructTreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public StructTreeNode getRootNode() {
        return this.rootNode;
    }

    @Override
    public boolean equals(Object o) {
        if (null == o || !(o instanceof StructTree)){
            return false;
        }
        StructTree target = (StructTree) o;
        return getSchemaStr().equals(target.getSchemaStr());
    }
}

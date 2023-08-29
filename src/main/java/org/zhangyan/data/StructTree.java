package org.zhangyan.data;



import static org.zhangyan.constant.SchemaDetectConstant.BLANCK_STRING;
import static org.zhangyan.constant.SchemaDetectConstant.ILLEGAL_ID;

public class StructTree {

    private Long id = ILLEGAL_ID;

    private String structName = BLANCK_STRING;

    private StructTreeNode rootNode;

    private String exampleJsonStr;

    public StructTree() {

    }
    public StructTree(String structName, String exampleJsonStr) {
        this.structName = structName;
        this.exampleJsonStr = exampleJsonStr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStructName() {
        return structName;
    }

    public void setStructName(String structName) {
        this.structName = structName;
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

}

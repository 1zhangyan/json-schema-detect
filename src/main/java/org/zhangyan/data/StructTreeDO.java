package org.zhangyan.data;


import static org.zhangyan.constant.SchemaDetectConstant.BLANCK_STRING;
import static org.zhangyan.constant.SchemaDetectConstant.ILLEGAL_ID;

public class StructTreeDO {

    private Long id = ILLEGAL_ID;

    private String structName = BLANCK_STRING;

    private String exampleJsonStr = BLANCK_STRING;

    private Long rootNodeId = 0L;

    private String nodeList = BLANCK_STRING;


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

    public String getNodeList() {
        return nodeList;
    }

    public void setNodeList(String nodeList) {
        this.nodeList = nodeList;
    }

    public Long getRootNodeId() {
        return rootNodeId;
    }

    public void setRootNodeId(Long rootNodeId) {
        this.rootNodeId = rootNodeId;
    }
}

package org.zhangyan.dao;


import static org.zhangyan.constant.DataTrackConstant.BLANCK_STRING;

public class StructTreeDo {

    private Long id = 0L;

    private String structName = BLANCK_STRING;

    private String exampleJsonStr = BLANCK_STRING;

    private Long rootNodeId = 0L;

    private String nodeSet = BLANCK_STRING;


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

    public String getNodeSet() {
        return nodeSet;
    }

    public void setNodeSet(String nodeSet) {
        this.nodeSet = nodeSet;
    }

    public Long getRootNodeId() {
        return rootNodeId;
    }

    public void setRootNodeId(Long rootNodeId) {
        this.rootNodeId = rootNodeId;
    }
}

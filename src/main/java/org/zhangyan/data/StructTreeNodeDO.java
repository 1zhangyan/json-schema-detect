package org.zhangyan.data;


import static org.zhangyan.constant.DataTrackConstant.BLANCK_STRING;
import static org.zhangyan.constant.DataTrackConstant.ILLEGAL_ID;

public class StructTreeNodeDO {
    private Long id = ILLEGAL_ID;
    private String key = BLANCK_STRING;
    private String type = BLANCK_STRING;
    private String path = BLANCK_STRING;
    private boolean inList = false;
    private boolean certainType = false;
    private String childrenIds = BLANCK_STRING;
    private String allNodeContains = BLANCK_STRING;
    private String exampleJsonStr = BLANCK_STRING;
    private String schemaJsonStr = BLANCK_STRING;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isInList() {
        return inList;
    }

    public void setInList(boolean inList) {
        this.inList = inList;
    }

    public boolean isCertainType() {
        return certainType;
    }

    public void setCertainType(boolean certainType) {
        this.certainType = certainType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(String childrenIds) {
        this.childrenIds = childrenIds;
    }

    public String getAllNodeContains() {
        return allNodeContains;
    }

    public void setAllNodeContains(String allNodeContains) {
        this.allNodeContains = allNodeContains;
    }
}

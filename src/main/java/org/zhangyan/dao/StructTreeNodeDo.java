package org.zhangyan.dao;


import static org.zhangyan.constant.DataTrackConstant.BLANCK_STRING;

public class StructTreeNodeDo {
    private Long id = 0L;
    private String key = BLANCK_STRING;
    private String type = BLANCK_STRING;
    private String path = BLANCK_STRING;
    private boolean inList = false;
    private boolean uncertainType = false;
    private String childrenIds = BLANCK_STRING;

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

    public boolean isUncertainType() {
        return uncertainType;
    }

    public void setUncertainType(boolean uncertainType) {
        this.uncertainType = uncertainType;
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
}

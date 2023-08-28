package org.zhangyan.data;



import static org.zhangyan.constant.DataTrackConstant.BLANCK_STRING;

import org.springframework.util.StringUtils;
import org.zhangyan.service.StructTreeNodeGenerator;

public class StructTree {

    private Long id = 0L;

    private String structName = BLANCK_STRING;

    private String exampleJsonStr = BLANCK_STRING;

    private StructTreeNode rootNode;

    public StructTree() {

    }
    public StructTree(String structName, String exampleJsonStr) {
        this.structName = structName;
        this.exampleJsonStr = exampleJsonStr;
        generateTree();
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

    private void generateTree() {
        if (StringUtils.isEmpty(structName) || StringUtils.isEmpty(exampleJsonStr)) {
            return;
        }
        setRootNode(StructTreeNodeGenerator.generateTreeFromJsonExample(this.exampleJsonStr , this.structName));
    }

    public String getSchemaStr() {
        return StructTreeNodeGenerator.generateSchemaStr(this.rootNode);
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

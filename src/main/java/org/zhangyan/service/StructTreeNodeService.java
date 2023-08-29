package org.zhangyan.service;

import org.zhangyan.data.StructTreeNode;
import org.zhangyan.data.StructTreeNodeVO;

public interface StructTreeNodeService {
    StructTreeNode createWithChildren(StructTreeNode structTreeNode);
    void update(StructTreeNode structTreeNode);
    void update(StructTreeNodeVO structTreeNodeVO);
    String generateSchemaStr(StructTreeNode node);
    StructTreeNode generateTreeFromJsonExample(String path, String exampleJsonStr);
    boolean equals(StructTreeNode structTreeNodeA, StructTreeNode structTreeNodeB);
    StructTreeNode getByPath(String path);
}

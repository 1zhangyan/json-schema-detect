package org.zhangyan.service;

import org.zhangyan.dao.StructTreeDo;
import org.zhangyan.data.StructTree;

public class StructTreeConvertor {
    public static StructTree convert(StructTreeDo structTreeDo) {
        StructTree structTree = new StructTree();
        structTree.setId(structTreeDo.getId());
        structTree.setStructName(structTreeDo.getStructName());
        structTree.setExampleJsonStr(structTreeDo.getExampleJsonStr());
        return structTree;
    }

    public static StructTreeDo convert(StructTree structTree) {
        StructTreeDo structTreeDo = new StructTreeDo();
        structTreeDo.setId(structTree.getId());
        structTreeDo.setStructName(structTree.getStructName());
        structTreeDo.setExampleJsonStr(structTree.getExampleJsonStr());
        structTreeDo.setRootNodeId(structTree.getRootNode().getId());
        return structTreeDo;
    }



}

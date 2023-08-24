package org.zhangyan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StructTreeConverter {
    //TODO: 考虑 List 且元素不同、暂不考虑
    //TODO: 类型不兼容，标记字段
    //TODO: 字段为空
    //TODO: 类型兼容 ： double > int > float. string > number.

    public static StructTree mergeTree(StructTree originTree, StructTree targetTree) {
        if (!originTree.getStructName().equals(targetTree.getStructName())) {
            throw new RuntimeException("Can not merge tree that is not same filed!");
        }
        if (originTree.equals(targetTree)) {
            return originTree;
        }
        StructTree structTree = new StructTree(originTree.getStructName(),originTree.getExampleJsonStr());
        structTree.setRootNode(mergeNodeWithSameKey(originTree.getRootNode(),targetTree.getRootNode()));
        return structTree;
    }
    private static StructTreeNode mergeNodeWithSameKey(StructTreeNode originNode, StructTreeNode targetNode) {
        if (!originNode.getKey().equals(targetNode.getKey()) || !originNode.getPath().equals(targetNode.getPath())
        ){
            throw new RuntimeException("can not merge tow node with different key/path.");
        }
        StructTreeNode treeNode = new StructTreeNode(originNode.getKey());
        treeNode.setType(originNode.getType());
        treeNode.setInList(originNode.isInList()||targetNode.isInList());
        if (originNode.getType().equals(targetNode.getType())) {
            treeNode.setUncertainType(true);
        }
        treeNode.setChildren(mergeNodes(originNode.getChildren(),targetNode.getChildren()));
        return treeNode;
    }
    private static List<StructTreeNode> mergeNodes(List<StructTreeNode> originNodes, List<StructTreeNode> targetNodes) {
        if (Utils.isEmpty(originNodes) ||  Utils.isEmpty(targetNodes)) {
            return Utils.isEmpty(originNodes) ? targetNodes : originNodes;
        }
        List<StructTreeNode> mergedNodes = new ArrayList<>();
        Map<String, StructTreeNode> originNodesMap =  originNodes.stream().collect(Collectors.toMap(StructTreeNode::getKey, Function.identity()));
        Map<String, StructTreeNode> targetNodesMap =  targetNodes.stream().collect(Collectors.toMap(StructTreeNode::getKey, Function.identity()));
        Set<String> keys = new HashSet<>();
        keys.addAll(targetNodesMap.keySet());
        keys.addAll(originNodesMap.keySet());
        keys.forEach(key -> {
            if (originNodesMap.containsKey(key) && targetNodesMap.containsKey(key)) {
                mergedNodes.add(mergeNodeWithSameKey(originNodesMap.get(key),targetNodesMap.get(key)));
            } else {
                mergedNodes.add(originNodesMap.containsKey(key)?originNodesMap.get(key):targetNodesMap.get(key));
            }
        });
        Collections.sort(mergedNodes);
        return mergedNodes;
    }



}

package org.zhangyan.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;
import org.zhangyan.data.StructTree;
import org.zhangyan.data.StructTreeNode;

public class StructTreeGenerator {
    //TODO: 考虑 List 且元素不同、暂不考虑
    public static StructTree mergeTree(StructTree originTree, StructTree targetTree) {
        if (!originTree.getStructName().equals(targetTree.getStructName())) {
            throw new RuntimeException("Can not merge tree that is not same filed!");
        }
        if (originTree.equals(targetTree)) {
            return originTree;
        }
        StructTree structTree = new StructTree(originTree.getStructName(), originTree.getExampleJsonStr());
        structTree.setRootNode(mergeNodeWithSameKey(originTree.getRootNode(), targetTree.getRootNode()));
        return structTree;
    }

    private static StructTreeNode mergeNodeWithSameKey(StructTreeNode originNode, StructTreeNode targetNode) {
        if (!originNode.getKey().equals(targetNode.getKey()) || !originNode.getPath().equals(targetNode.getPath())
        ) {
            throw new RuntimeException("can not merge tow node with different key/path.");
        }
        StructTreeNode treeNode = new StructTreeNode(originNode.getKey());
        treeNode.setType(originNode.getType());
        treeNode.setInList(originNode.isInList() || targetNode.isInList());
        if (!originNode.getType().equals(targetNode.getType())) {
            if (originNode.getType().isNumber() && targetNode.getType().isNumber()) {
                treeNode.setType(mergeNumberDataType(originNode.getType(), targetNode.getType()));
            } else {
                treeNode.setUncertainType(true);
            }
        }
        treeNode.setChildren(mergeNodes(originNode.getChildren(), targetNode.getChildren()));
        return treeNode;
    }

    private static List<StructTreeNode> mergeNodes(List<StructTreeNode> originNodes, List<StructTreeNode> targetNodes) {
        if (CollectionUtils.isEmpty(originNodes) || CollectionUtils.isEmpty(targetNodes)) {
            return CollectionUtils.isEmpty(originNodes) ? targetNodes : originNodes;
        }
        List<StructTreeNode> mergedNodes = new ArrayList<>();
        Map<String, StructTreeNode> originNodesMap = originNodes.stream()
                .collect(Collectors.toMap(StructTreeNode::getKey, Function.identity()));
        Map<String, StructTreeNode> targetNodesMap = targetNodes.stream()
                .collect(Collectors.toMap(StructTreeNode::getKey, Function.identity()));
        Set<String> keys = new HashSet<>();
        keys.addAll(targetNodesMap.keySet());
        keys.addAll(originNodesMap.keySet());
        keys.forEach(key -> {
            if (originNodesMap.containsKey(key) && targetNodesMap.containsKey(key)) {
                mergedNodes.add(mergeNodeWithSameKey(originNodesMap.get(key), targetNodesMap.get(key)));
            } else {
                mergedNodes.add(originNodesMap.containsKey(key) ? originNodesMap.get(key) : targetNodesMap.get(key));
            }
        });
        Collections.sort(mergedNodes);
        return mergedNodes;
    }

    public static StructTreeNode.DataType mergeNumberDataType(StructTreeNode.DataType typeA, StructTreeNode.DataType typeB) {
        if (!typeA.isNumber() || !typeB.isNumber()) {
            throw new RuntimeException("Only number Can be merged.");
        }
        return typeA.getOrder() > typeB.getOrder() ? typeA : typeB;
    }

}

package org.zhangyan.service;

import static org.zhangyan.constant.SchemaDetectConstant.ILLEGAL_ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.zhangyan.data.StructTreeNode;

@Service
public class NodeMergeService {
    public StructTreeNode mergeNodesWithSameKey(List<StructTreeNode> structTreeNodes) {
        //TODO:分治法
        return null;

    }
    public StructTreeNode mergeNodeWithSameKey(StructTreeNode originNode, StructTreeNode targetNode) {
        if (!originNode.getKey().equals(targetNode.getKey()) || !originNode.getPath().equals(targetNode.getPath())
        ) {
            throw new RuntimeException("can not merge tow node with different key/path.");
        }
        StructTreeNode treeNode = new StructTreeNode();
        BeanUtils.copyProperties(originNode, treeNode);
        treeNode.setKey(originNode.getKey());
        treeNode.setInList(originNode.isInList() || targetNode.isInList());
        treeNode.setId(originNode.getId() == ILLEGAL_ID ? targetNode.getId():originNode.getId());
        if (!originNode.getType().equals(targetNode.getType())) {
            if (originNode.getType().isNumber() && targetNode.getType().isNumber()) {
                treeNode.setType(mergeNumberDataType(originNode.getType(), targetNode.getType()));
            } else {
                treeNode.setUncertainType(true);
            }
        }
        treeNode.setChildren(mergeChildren(originNode.getChildren(), targetNode.getChildren()));
        return treeNode;
    }

    private List<StructTreeNode> mergeChildren(List<StructTreeNode> originNodes, List<StructTreeNode> targetNodes) {
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

    private StructTreeNode.DataType mergeNumberDataType(StructTreeNode.DataType typeA, StructTreeNode.DataType typeB) {
        if (!typeA.isNumber() || !typeB.isNumber()) {
            throw new RuntimeException("Only number Can be merged.");
        }
        return typeA.getOrder() > typeB.getOrder() ? typeA : typeB;
    }

}

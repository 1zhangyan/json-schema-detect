package org.zhangyan.service;

import static org.zhangyan.constant.DataTrackConstant.ILLEGAL_ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.zhangyan.data.StructTreeNode;
import com.google.common.collect.Iterables;

@Service
public class NodeMergeService {
    private static final Logger LOG =  LoggerFactory.getLogger(NodeMergeService.class);
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(30);

    public StructTreeNode mergeNodesWithSameKey(List<StructTreeNode> structTreeNodes) {
        //分治法合并
        structTreeNodes = structTreeNodes.parallelStream().filter(it -> null != it).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(structTreeNodes)) {
            return null;
        } else if (structTreeNodes.size() == 1) {
            return structTreeNodes.get(0);
        } else if(structTreeNodes.size() == 2) {
            return mergeNodeWithSameKey(structTreeNodes.get(0),structTreeNodes.get(1));
        } else {
            Iterator<List<StructTreeNode>> itr = Iterables
                    .partition(structTreeNodes,(structTreeNodes.size()) / 2).iterator();
            List<StructTreeNode> firstHalf = new ArrayList<>(itr.next());
            List<StructTreeNode> lastHalf = new ArrayList<>(itr.next());
            Future<StructTreeNode> firstHalfFuture =  threadPool.submit(new Callable<StructTreeNode>() {
                @Override
                public StructTreeNode call() {
                    try  {
                        return mergeNodesWithSameKey(firstHalf);
                    } catch (Exception e) {
                        LOG.error("[merge list error]pool thread encounter exception when merge firstHalf.", e);
                        return null;
                    }
                }
            });
            Future<StructTreeNode> lastHalfFuture =  threadPool.submit(new Callable<StructTreeNode>() {
                @Override
                public StructTreeNode call() {
                    try  {
                        return mergeNodesWithSameKey(lastHalf);
                    } catch (Exception e) {
                        LOG.error("[merge list error]pool thread encounter exception when merge lastHalf.", e);
                        return null;
                    }
                }
            });

            try {
                return mergeNodeWithSameKey(firstHalfFuture.get(),lastHalfFuture.get());
            } catch (Exception e) {
                LOG.error("[merge list error]future result encounter exception when merge tow half.", e);
                return null;
            }
        }
    }
    public StructTreeNode mergeNodeWithSameKey(StructTreeNode originNode, StructTreeNode targetNode) {
        if (originNode == null || targetNode == null) {
            return originNode == null?targetNode:originNode;
        }
        if (!originNode.getKey().equals(targetNode.getKey()) || !originNode.getPath().equals(targetNode.getPath())) {
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

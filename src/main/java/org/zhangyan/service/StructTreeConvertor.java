package org.zhangyan.service;

import static org.zhangyan.constant.SchemaDetectConstant.ILLEGAL_ID;


import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zhangyan.dao.StructTreeDO;
import org.zhangyan.dao.StructTreeNodeDO;
import org.zhangyan.dao.StructTreeNodeDao;
import org.zhangyan.data.StructTree;
import org.zhangyan.data.StructTreeNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StructTreeConvertor {

    private static final Logger LOG =  LoggerFactory.getLogger(StructTreeConvertor.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final StructTreeNodeDao structTreeNodeDao = new  StructTreeNodeDao();

    private static final StructTreeNodeConvertor structTreeNodeConvertor = new StructTreeNodeConvertor();

    public Long create(StructTree structTree) throws JsonProcessingException {
        StructTreeDO structTreeDO = new StructTreeDO();
        structTreeDO.setStructName(structTree.getStructName());
        structTreeDO.setExampleJsonStr(structTree.getExampleJsonStr());

        StructTreeNodeConvertor.IdPair idPair = structTreeNodeConvertor.create(structTree.getRootNode());
        structTreeDO.setNodeList(objectMapper.writeValueAsString(idPair.allNodeIds));
        structTreeDO.setRootNodeId(idPair.curId);
        return structTreeNodeDao.create(structTreeDO);
    }

    public StructTree convert(StructTreeDO structTreeDo) {
        StructTree structTree = new StructTree();
        structTree.setId(structTreeDo.getId());
        structTree.setStructName(structTreeDo.getStructName());
        structTree.setExampleJsonStr(structTreeDo.getExampleJsonStr());
        structTree.setRootNode(genStructTreeFromDO(structTreeDo));
        return structTree;
    }

    public StructTreeDO convert(StructTree structTree) throws JsonProcessingException {
        StructTreeDO structTreeDo = new StructTreeDO();
        structTreeDo.setId(structTree.getId());
        structTreeDo.setStructName(structTree.getStructName());
        structTreeDo.setExampleJsonStr(structTree.getExampleJsonStr());
        structTreeDo.setRootNodeId(structTree.getRootNode().getId());
        structTreeDo.setNodeList(objectMapper.writeValueAsString(getNodeSet(structTree.getRootNode())));
        return structTreeDo;
    }

    private List<Long> getNodeSet(StructTreeNode node) {
        if (CollectionUtils.isEmpty(node.getChildren())) {
            return Collections.emptyList();
        }
        Set<Long> nodeSet = new HashSet();
        nodeSet.add(node.getId());
        node.getChildren().stream().forEach(child -> {
            nodeSet.add(child.getId());
            nodeSet.addAll(getNodeSet(child));
        });
        return nodeSet.stream().collect(Collectors.toList());
    }


    private StructTreeNode genStructTreeFromDO(StructTreeDO structTreeDO) {
        if (StringUtils.isEmpty(structTreeDO.getNodeList()) || structTreeDO.getId() == ILLEGAL_ID) {
            return null;
        }
        try {
            List<Long> nodeIdList = objectMapper.readValue(structTreeDO.getNodeList(), List.class);
            List<StructTreeNodeDO> structTreeNodeDOS = structTreeNodeDao.getListByIds(nodeIdList);
            if (CollectionUtils.isEmpty(structTreeNodeDOS)) {
                return null;
            }
            Map<Long,StructTreeNodeDO> id2NodeMap = structTreeNodeDOS.stream()
                    .collect(Collectors.toMap(StructTreeNodeDO::getId, Function.identity()));
            return StructTreeNodeConvertor.convert(structTreeDO.getRootNodeId(),id2NodeMap);
        } catch (IOException e) {
            LOG.info("[StructTreeConvertor]: can not convert nodeSet from {}",structTreeDO.getNodeList(),e);
            return null;
        }
    }




}

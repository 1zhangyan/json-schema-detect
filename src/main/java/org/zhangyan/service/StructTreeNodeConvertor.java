package org.zhangyan.service;


import static org.zhangyan.constant.SchemaDetectConstant.ILLEGAL_ID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zhangyan.dao.StructTreeDO;
import org.zhangyan.dao.StructTreeNodeDO;
import org.zhangyan.dao.StructTreeNodeDao;
import org.zhangyan.data.StructTree;
import org.zhangyan.data.StructTreeNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StructTreeNodeConvertor {
    private static final Logger LOG = LoggerFactory.getLogger(StructTreeConvertor.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static StructTreeNodeDao structTreeNodeDao = new StructTreeNodeDao();

    public class IdPair {
        public IdPair(){}
        public Long curId = ILLEGAL_ID;
        public List<Long> allNodeIds = new ArrayList<>();
    }

    public IdPair create(StructTreeNode structTreeNode) throws JsonProcessingException {
        List<Long> allNodeIds = new ArrayList<>();
        for (StructTreeNode treeNode : structTreeNode.getChildren()) {
            IdPair idPair = create(treeNode);
            allNodeIds.addAll(idPair.allNodeIds);
        }
        StructTreeNodeDO structTreeNodeDO = new StructTreeNodeDO();
        structTreeNodeDO.setType(structTreeNode.getType().getName());
        structTreeNodeDO.setChildrenIds(objectMapper.writeValueAsString(allNodeIds));
        structTreeNodeDO.setKey(structTreeNode.getKey());
        structTreeNodeDO.setPath(structTreeNode.getPath());
        structTreeNodeDO.setInList(structTreeNode.isInList());
        structTreeNodeDO.setUncertainType(structTreeNode.isUncertainType());
        Long id = Long.valueOf(structTreeNodeDao.create(structTreeNodeDO));
        allNodeIds.add(id);
        Collections.sort(allNodeIds);
        IdPair idPair = new IdPair();
        idPair.allNodeIds = allNodeIds;
        idPair.curId = id;
        return idPair;
    }

    public static StructTreeNode convert(StructTreeNodeDO structTreeDo) {
        StructTreeNode structTreeNode = new StructTreeNode();
        structTreeNode.setType(StructTreeNode.DataType.getDataType(structTreeDo.getType()));
        BeanUtils.copyProperties(structTreeDo, structTreeNode);
        structTreeNode.setChildren(genChildrenTree(structTreeDo));
        return structTreeNode;
    }

    private static List<StructTreeNode> genChildrenTree(StructTreeNodeDO structTreeNodeDO) {
        if (StringUtils.isEmpty(structTreeNodeDO.getChildrenIds())) {
            return Collections.emptyList();
        }
        try {
            List<Long> childrenIds = objectMapper.readValue(structTreeNodeDO.getChildrenIds(), List.class);
            List<StructTreeNodeDO> treeNodeDOS = structTreeNodeDao.getListByIds(childrenIds);
            List<StructTreeNode> structTreeNodes = treeNodeDOS.stream()
                    .map(StructTreeNodeConvertor::convert)
                    .collect(Collectors.toList());
            return structTreeNodes;
        } catch (IOException e) {
            LOG.info("[StructTreeNodeConvertor]: can not convert childrenList from {}", structTreeNodeDO.getChildrenIds(), e);
            return Collections.emptyList();
        }
    }

    public static StructTreeNode convert(Long structTreeNodeId, Map<Long, StructTreeNodeDO> id2NodeMap) {
        if (structTreeNodeId == ILLEGAL_ID) {
            return null;
        }
        StructTreeNodeDO structTreeNodeDO = id2NodeMap.get(structTreeNodeId);
        StructTreeNode structTreeNode = new StructTreeNode();
        structTreeNode.setType(StructTreeNode.DataType.getDataType(structTreeNodeDO.getType()));
        BeanUtils.copyProperties(structTreeNodeDO, structTreeNode);
        structTreeNode.setChildren(genChildrenTree(structTreeNodeDO, id2NodeMap));
        return structTreeNode;
    }

    private static List<StructTreeNode> genChildrenTree(StructTreeNodeDO structTreeNodeDO, Map<Long, StructTreeNodeDO> id2NodeMap) {
        if (StringUtils.isEmpty(structTreeNodeDO.getChildrenIds())) {
            return Collections.emptyList();
        }
        try {
            List<Long> childrenIds = objectMapper.readValue(structTreeNodeDO.getChildrenIds(), List.class);
            if (CollectionUtils.isEmpty(childrenIds)) {
                return Collections.emptyList();
            }
            List<StructTreeNodeDO> treeNodeDOS = new ArrayList<>();
            for (Long childrenId : childrenIds) {
                StructTreeNodeDO structTreeDO = id2NodeMap.get(childrenId);
                treeNodeDOS.add(structTreeDO);
            }
            List<StructTreeNode> structTreeNodes = treeNodeDOS.stream()
                    .map(StructTreeNodeConvertor::convert)
                    .collect(Collectors.toList());
            return structTreeNodes;
        } catch (IOException e) {
            LOG.info("[StructTreeNodeConvertor]: can not convert childrenList from {}", structTreeNodeDO.getChildrenIds(), e);
            return Collections.emptyList();
        }
    }

    public StructTreeNodeDO convert(StructTreeNode structTreeNode) throws JsonProcessingException {
        StructTreeNodeDO structTreeNodeDO = new StructTreeNodeDO();
        structTreeNodeDO.setType(structTreeNode.getType().getName());
        BeanUtils.copyProperties(structTreeNode, structTreeNodeDO);
        List<Long> childrenIdList = structTreeNode.getChildren().stream()
                .map(StructTreeNode::getId)
                .collect(Collectors.toList());
        structTreeNodeDO.setChildrenIds(objectMapper.writeValueAsString(childrenIdList));
        return structTreeNodeDO;
    }

}

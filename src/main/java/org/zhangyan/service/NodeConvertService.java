package org.zhangyan.service;


import static org.zhangyan.constant.DataTrackConstant.ILLEGAL_ID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zhangyan.data.StructTreeNodeDO;
import org.zhangyan.dao.StructTreeNodeDao;
import org.zhangyan.data.StructTreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class NodeConvertService {
    private static final Logger LOG = LoggerFactory.getLogger(NodeConvertService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private StructTreeNodeDao structTreeNodeDao;

    public StructTreeNode convert(StructTreeNodeDO structTreeNodeDo) {
        StructTreeNode structTreeNode = new StructTreeNode();
        BeanUtils.copyProperties(structTreeNodeDo, structTreeNode);
        structTreeNode.setType(StructTreeNode.DataType.getDataType(structTreeNodeDo.getType()));
        try {
            structTreeNode.setChildrenIds(objectMapper.readValue(structTreeNodeDo.getChildrenIds(), new TypeReference<List<Long>>() {}));
            structTreeNode.setAllNodeContains(objectMapper.readValue(structTreeNodeDo.getAllNodeContains(), new TypeReference<List<Long>>() {}));
        } catch (Exception e) {
            LOG.error("[NodeConvertor]: can not convert childrenList or allNodeContains.",e);
        }

        if (!CollectionUtils.isEmpty(structTreeNode.getAllContainNodeIds())) {
            Map<Long, StructTreeNodeDO> id2NodeMap = structTreeNodeDao.getListByIds(structTreeNode.getAllContainNodeIds())
                    .stream().collect(Collectors.toMap(StructTreeNodeDO::getId, Function.identity()));
            structTreeNode.setChildren(genChildrenTree(structTreeNodeDo, id2NodeMap));
        }
        return structTreeNode;
    }

    private List<StructTreeNode> genChildrenTree(StructTreeNodeDO structTreeNodeDO) {
        if (StringUtils.isEmpty(structTreeNodeDO.getChildrenIds())) {
            return Collections.emptyList();
        }
        try {
            List<Long> childrenIds = objectMapper.readValue(structTreeNodeDO.getChildrenIds(), new TypeReference<List<Long>>() {});
            List<StructTreeNodeDO> treeNodeDOS = structTreeNodeDao.getListByIds(childrenIds);
            treeNodeDOS.add(structTreeNodeDO);
            List<StructTreeNode> structTreeNodes = treeNodeDOS.stream()
                    .map(it -> convert(it))
                    .collect(Collectors.toList());
            return structTreeNodes;
        } catch (IOException e) {
            LOG.info("[NodeConvertor]: can not convert childrenList from {}", structTreeNodeDO.getChildrenIds(), e);
            return Collections.emptyList();
        }
    }

    private StructTreeNode convert(Long structTreeNodeId, Map<Long, StructTreeNodeDO> id2NodeMap) {
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

    private List<StructTreeNode> genChildrenTree(StructTreeNodeDO structTreeNodeDO, Map<Long, StructTreeNodeDO> id2NodeMap) {
        if (StringUtils.isEmpty(structTreeNodeDO.getChildrenIds())) {
            return Collections.emptyList();
        }
        try {
            List<Long> childrenIds = objectMapper.readValue(structTreeNodeDO.getChildrenIds(), new TypeReference<List<Long>>() {});
            if (CollectionUtils.isEmpty(childrenIds)) {
                return Collections.emptyList();
            }
            List<StructTreeNodeDO> treeNodeDOS = new ArrayList<>();
            for (Long childrenId : childrenIds) {
                StructTreeNodeDO structTreeDO = id2NodeMap.get(childrenId);
                treeNodeDOS.add(structTreeDO);
            }
            List<StructTreeNode> structTreeNodes = treeNodeDOS.stream()
                    .map(it -> convert(it.getId(), id2NodeMap))
                    .collect(Collectors.toList());
            return structTreeNodes;
        } catch (IOException e) {
            LOG.info("[StructTreeNodeConvertor]: can not convert childrenList from {}", structTreeNodeDO.getChildrenIds(), e);
            return Collections.emptyList();
        }
    }

}

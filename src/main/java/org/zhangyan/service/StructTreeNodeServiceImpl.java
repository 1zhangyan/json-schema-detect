package org.zhangyan.service;

import static com.fasterxml.jackson.databind.node.JsonNodeType.STRING;
import static org.zhangyan.constant.DataTrackConstant.BLANCK_STRING;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zhangyan.dao.StructTreeNodeDao;
import org.zhangyan.data.StructTreeNode;
import org.zhangyan.data.StructTreeNodeDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StructTreeNodeServiceImpl implements  StructTreeNodeService{
    private static String PATH_FORMAT = "%s.%s";
    private static int FIST_ELEMENT = 0;
    private static String UNKNOWN_FORMAT = "\"%s\":\"unknown\"";
    private static String LIST_FORMAT = "\"%s\": [%s]";
    private static String MAP_FORMAT = "\"%s\": {%s}";
    private static String VALUE_FORMAT = "\"%s\": \"%s\"";
    private static String UNKNOWN_INLIST_FORMAT = "\"unknown\"";
    private static String MAP_INLIST_FORMAT = "{%s}";
    private static String LIST_INLIST_FORMAT = "[%s]";
    private static String VALUE_INLIST_FORMAT = "\"%s\"";
    private static String CONNECT_FORMAT = "%s,%s";

    private static String LIST_KEY = "@list";

    private static final Logger LOG = LoggerFactory.getLogger(StructTreeNodeServiceImpl.class);

    private final StructTreeNodeDao structTreeNodeDao;

    private final ObjectMapper objectMapper;

    private final NodeConvertService nodeConvertService;

    public StructTreeNodeServiceImpl(StructTreeNodeDao structTreeNodeDao, ObjectMapper objectMapper, NodeConvertService nodeConvertService ) {
        this.structTreeNodeDao = structTreeNodeDao;
        this.objectMapper = objectMapper;
        this.nodeConvertService = nodeConvertService;
    }

    @Override
    public StructTreeNode upsertWithChildren(StructTreeNode structTreeNode) {
        List<Long> allNodeIds = Collections.emptyList();
        List<Long> childrenIds = Collections.emptyList();
        if (!CollectionUtils.isEmpty(structTreeNode.getChildren())) {
            allNodeIds = new ArrayList<>();
            childrenIds = new ArrayList<>();
            for (StructTreeNode treeNode : structTreeNode.getChildren()) {
                StructTreeNode leafNode = upsertWithChildren(treeNode);
                allNodeIds.addAll(leafNode.getAllContainNodeIds());
                childrenIds.add(leafNode.getId());
            }
            allNodeIds.addAll(childrenIds);
        }
        try {
            StructTreeNodeDO structTreeNodeDO = new StructTreeNodeDO();
            structTreeNodeDO.setType(structTreeNode.getType().getName());
            structTreeNodeDO.setChildrenIds(objectMapper.writeValueAsString(childrenIds));
            structTreeNodeDO.setKey(structTreeNode.getKey());
            structTreeNodeDO.setPath(structTreeNode.getPath());
            structTreeNodeDO.setInList(structTreeNode.isInList());
            structTreeNodeDO.setCertainType(structTreeNode.isCertainType());
            structTreeNodeDO.setAllNodeContains(objectMapper.writeValueAsString(allNodeIds));
            Long id = Long.valueOf(structTreeNodeDao.upsert(structTreeNodeDO));
            structTreeNode.setId(id);
            structTreeNode.setAllNodeContains(allNodeIds);
            structTreeNode.setChildrenIds(childrenIds);
            return structTreeNode;
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("[DataTrack]:create with child failed.caused by json process.");
        }
    }

    @Override
    public void update(StructTreeNode structTreeNode) {

    }

    @Override
    public String generateSchemaStr(StructTreeNode node) {
        if (node == null || StringUtils.isEmpty(node.getKey())) {
            return BLANCK_STRING;
        }
        String schemaStr = BLANCK_STRING;
        String format = BLANCK_STRING;

        if (node.getType() == null || node.getType().equals(StructTreeNode.DataType.UNKNOWN)) {
            format = node.isInList() ? UNKNOWN_INLIST_FORMAT : UNKNOWN_FORMAT;
            return String.format(format, node.getKey());
        }
        if (node.getType().equals(StructTreeNode.DataType.MAP)) {
            schemaStr = node.isInList() ? String.format(MAP_INLIST_FORMAT, genChildrenSchemaStr(node.getChildren()))
                    : String.format(MAP_FORMAT, node.getKey(), genChildrenSchemaStr(node.getChildren()));
        } else if (node.getType().equals(StructTreeNode.DataType.LIST)) {
            schemaStr = node.isInList()
                    ? String.format(LIST_INLIST_FORMAT, genChildrenSchemaStr(node.getChildren()))
                    : String.format(LIST_FORMAT, node.getKey(), genChildrenSchemaStr(node.getChildren()));
        } else {
            schemaStr = node.isInList() ? String.format(VALUE_INLIST_FORMAT, node.getType())
                    : String.format(VALUE_FORMAT, node.getKey(), node.getType());
        }
        return schemaStr;
    }
    @Override
    public StructTreeNode generateTreeFromJsonExample(String structName, String exampleJsonStr, boolean withExample) {
        StructTreeNode structTreeNode = null;

        if (!StringUtils.isEmpty(exampleJsonStr) && !StringUtils.isEmpty(structName)) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(exampleJsonStr);
                structTreeNode = generateTreeNode(structName, jsonNode, BLANCK_STRING, false, withExample);
                if (null != structTreeNode) {
                    structTreeNode.setExampleJsonStr(exampleJsonStr);
                }
            } catch (IOException exception) {
                LOG.debug("Can not deal struct，path is {},  element is {}",structName, exampleJsonStr);
            }
        }
        return structTreeNode;
    }

    @Override
    public boolean equals(StructTreeNode structTreeNodeA, StructTreeNode structTreeNodeB) {
        if (structTreeNodeA == structTreeNodeB) {
            return true;
        }
        if (structTreeNodeA == null || structTreeNodeB == null) {
            return false;
        }
        return generateSchemaStr(structTreeNodeA).equals(generateSchemaStr(structTreeNodeB));
    }

    @Override
    public StructTreeNode getByPath(String path) {
        StructTreeNodeDO structTreeNodeDO = structTreeNodeDao.getByPath(path);
        if (structTreeNodeDO == null) {
            return null;
        }
        return nodeConvertService.convert(structTreeNodeDO);
    }

    private StructTreeNode generateTreeNode(String key, JsonNode jsonNode, String parentPath, boolean isParentList, boolean withExample) {
        if (jsonNode == null) {
            return null;
        }
        StructTreeNode treeNode = new StructTreeNode(key);
        treeNode.setKey(key);
        treeNode.setType(jsonNode);
        if (withExample) {
            treeNode.setExampleJsonStr(jsonNode.asText());
        }
        String currentPath = BLANCK_STRING;
        if (parentPath.equals(BLANCK_STRING)) {
            currentPath = key;
        } else {
            currentPath = String.format(PATH_FORMAT, parentPath, key);
        }
        treeNode.setPath(currentPath);
        treeNode.setInList(isParentList);
        treeNode.setExampleJsonStr(jsonNode.asText());
        if (jsonNode.isObject()) {
            List<StructTreeNode> children = new ArrayList<>();
            Iterator<String> iterator = jsonNode.fieldNames();
            while (iterator.hasNext()) {
                String field = iterator.next();
                StructTreeNode child = generateTreeNode(field, jsonNode.get(field), currentPath, false,withExample);
                children.add(child);
            }
            treeNode.setChildren(children);
        } else if (jsonNode.isArray() && jsonNode.size() > 0) {
            jsonNode.get(FIST_ELEMENT);
            StructTreeNode child = generateTreeNode(LIST_KEY, jsonNode.get(0), currentPath, true, withExample);
            treeNode.setChildren(Collections.singletonList(child));
        } else if (jsonNode.getNodeType() == STRING) {
            try {
                StructTreeNode childNode = generateTreeFromJsonExample(treeNode.getPath(), jsonNode.asText(),withExample);
                if (childNode != null) {
                    treeNode.setType(childNode.getType());
                    if (childNode.getType().equals(StructTreeNode.DataType.MAP)) {
                        treeNode.setChildren(childNode.getChildren());
                    }
                    if (childNode.getType().equals(StructTreeNode.DataType.LIST)) {
                        treeNode.setChildren(childNode.getChildren());
                    }
                }
            } catch (Exception e) {
              LOG.debug("value element will not be deal with");
            }
        } if (treeNode.getChildren() != null) {
            Collections.sort(treeNode.getChildren());
        }
        return treeNode;
    }
    private String genChildrenSchemaStr(List<StructTreeNode> children) {
        if (children == null || children.isEmpty()) {
            return BLANCK_STRING;
        }
        String schemaStr = BLANCK_STRING;
        for (StructTreeNode child : children) {
            String curSchema = generateSchemaStr(child);
            if (schemaStr == BLANCK_STRING) {
                schemaStr = curSchema;
            } else {
                schemaStr = String.format(CONNECT_FORMAT, schemaStr, curSchema);
            }
        }
        return schemaStr;
    }
}

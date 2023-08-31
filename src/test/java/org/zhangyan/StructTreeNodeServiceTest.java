package org.zhangyan;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.zhangyan.data.StructTreeNode;
import org.zhangyan.service.NodeMergeService;
import org.zhangyan.service.StructTreeNodeService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StructTreeNodeServiceTest {
    private static final Logger LOG =  LoggerFactory.getLogger(StructTreeNodeServiceTest.class);

    private static String exampleStr= "{\"layer1-3\":122.2,\"layer1-2\":[\"test1\",\"test2\"],\"layer1-1\":{\"layer2-2\":\"steam\",\"layer2-1\":\"wegame\"},\"layer1-5\":278.222}";


    @Autowired
    private StructTreeNodeService structTreeNodeService;

    @Autowired
    private NodeMergeService nodeMergeService;

    @Test
    public void testGenAndMerge() {
        StructTreeNode tree1= structTreeNodeService.generateTreeFromJsonExample("tree","{\"layer1-3\":122.2,\"layer1-2\":[\"test1\",\"test2\"],\"layer1-1\":{\"layer2-2\":\"steam\",\"layer2-1\":\"wegame\"},\"layer1-5\":278.222}");
        StructTreeNode tree2 = null;
        if (structTreeNodeService.getByPath(tree1.getPath()) == null) {
            tree2  = structTreeNodeService.upsertWithChildren(tree1);
        }
        LOG.info(structTreeNodeService.generateSchemaStr(tree2));
        StructTreeNode tree3= structTreeNodeService.generateTreeFromJsonExample("tree","{\"layer1-4\":278.33,\"layer1-3\":171,\"layer1-2\":[\"tea\",\"tcoco\"],\"layer1-1\":{\"layer2-3\":\"ll\",\"layer2-2\":\"cheat\",\"layer2-4\":\"heat\"}}");
        StructTreeNode tree4 = nodeMergeService.mergeNodeWithSameKey(tree3,tree2);
        LOG.info(structTreeNodeService.generateSchemaStr(tree3));
        LOG.info(structTreeNodeService.generateSchemaStr(tree4));
        LOG.info("execute finished!");
    }

    @Test
    public void testGetByPath(){
        StructTreeNode tree1= structTreeNodeService.generateTreeFromJsonExample("tree","{\"layer1-3\":122.2,\"layer1-2\":[\"test1\",\"test2\"],\"layer1-1\":{\"layer2-2\":\"steam\",\"layer2-1\":\"wegame\"},\"layer1-5\":278.222}");
//        StructTreeNode tree1= structTreeNodeService.generateTreeFromJsonExample("tree","{\"layer\":[1,2,3]}");
        StructTreeNode tree2  = structTreeNodeService.upsertWithChildren(tree1);
        StructTreeNode tree3  = structTreeNodeService.getByPath(tree1.getPath());
        LOG.info(structTreeNodeService.generateSchemaStr(tree1));
        LOG.info(structTreeNodeService.generateSchemaStr(tree2));
        LOG.info(structTreeNodeService.generateSchemaStr(tree3));
    }

    @Test
    public void testUpsert() {
        StructTreeNode tree1= structTreeNodeService.generateTreeFromJsonExample("tree","{\"layer1-3\":122.2,\"layer1-2\":[\"test1\",\"test2\"],\"layer1-1\":{\"layer2-2\":\"steam\",\"layer2-1\":\"wegame\"},\"layer1-5\":278.222}");
        StructTreeNode tree2  = structTreeNodeService.upsertWithChildren(tree1);
        StructTreeNode tree3  = structTreeNodeService.getByPath(tree1.getPath());
        LOG.info(structTreeNodeService.generateSchemaStr(tree2));
        LOG.info(structTreeNodeService.generateSchemaStr(tree3));
        StructTreeNode tree4= structTreeNodeService.generateTreeFromJsonExample("tree","{\"layer1-4\":278.33,\"layer1-3\":171,\"layer1-2\":[\"tea\",\"tcoco\"],\"layer1-1\":{\"layer2-3\":\"ll\",\"layer2-2\":\"cheat\",\"layer2-4\":\"heat\"}}");
        LOG.info(structTreeNodeService.generateSchemaStr(tree4));
        StructTreeNode tree5= nodeMergeService.mergeNodeWithSameKey(tree3,tree4);
        structTreeNodeService.upsertWithChildren(tree5);
        LOG.info(structTreeNodeService.generateSchemaStr(structTreeNodeService.getByPath(tree5.getPath())));
    }

    @Test
    public void mergeListNode() {
        StructTreeNode tree1= structTreeNodeService.generateTreeFromJsonExample("tree","{\"layer1-3\":122.2,\"layer1-2\":[\"test1\",\"test2\"],\"layer1-1\":{\"layer2-2\":\"steam\",\"layer2-1\":\"wegame\"},\"layer1-5\":278.222}");
        StructTreeNode tree2= structTreeNodeService.generateTreeFromJsonExample("tree","{\"layer1-7\":122.2,\"layer1-8\":[\"test1\",\"test2\"],\"layer1-1\":{\"layer2-2\":\"steam\",\"layer2-1\":\"wegame\"},\"layer1-5\":278.222}");
        StructTreeNode tree3= structTreeNodeService.generateTreeFromJsonExample("tree","{\"layer1-9\":122.2,\"layer1-10\":[\"test1\",\"test2\"],\"layer1-1\":{\"layer2-2\":\"steam\",\"layer2-1\":\"wegame\"},\"layer1-5\":278.222}");
        StructTreeNode tree4= structTreeNodeService.generateTreeFromJsonExample("tree","{\"layer1-11\":122.2,\"layer1-12\":[\"test1\",\"test2\"],\"layer1-1\":{\"layer2-2\":\"steam\",\"layer2-1\":\"wegame\"},\"layer1-5\":278.222}");
        List<StructTreeNode> nodeList = new ArrayList<>();
        nodeList.add(tree1);
        nodeList.add(tree2);
        nodeList.add(tree3);
        nodeList.add(tree4);
        StructTreeNode endNode = nodeMergeService.mergeNodesWithSameKey(nodeList);
        LOG.info(structTreeNodeService.generateSchemaStr(endNode));
    }

    @Test
    public void testRealData() {
        StructTreeNode tree1= structTreeNodeService.generateTreeFromJsonExample("tree",exampleStr);
        LOG.info(structTreeNodeService.generateSchemaStr(tree1));
    }
}

package org.zhangyan;

import org.zhangyan.dao.StructTreeDO;
import org.zhangyan.dao.StructTreeNodeDao;
import org.zhangyan.data.StructTree;
import org.zhangyan.service.StructTreeConvertor;
import org.zhangyan.service.StructTreeGenerator;
import org.zhangyan.service.StructTreeNodeConvertor;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ConvertorTest {
    public static void main(String args[]) throws JsonProcessingException {
        StructTree tree1= new StructTree("tree","{\"layer1-3\":122.2,\"layer1-2\":[\"test1\",\"test2\"],\"layer1-1\":{\"layer2-2\":\"steam\",\"layer2-1\":\"wegame\"},\"layer1-5\":278.222}");
        StructTree tree2= new StructTree("tree","{\"layer1-4\":278.33,\"layer1-3\":171,\"layer1-2\":[\"tea\",\"tcoco\"],\"layer1-1\":{\"layer2-3\":\"ll\",\"layer2-2\":\"cheat\",\"layer2-4\":\"heat\"}}");
        StructTreeNodeDao structTreeNodeDao = new StructTreeNodeDao();
        StructTreeNodeConvertor structTreeNodeConvertor = new StructTreeNodeConvertor();
        StructTreeConvertor structTreeConvertor = new StructTreeConvertor();
        if (structTreeNodeDao.getByPath(tree1.getStructName()) == null) {
            structTreeConvertor.create(tree1);
        }

        StructTreeDO structTreeDO = structTreeNodeDao.getByPath(tree2.getStructName());
        if (structTreeNodeDao.getByPath(tree2.getStructName()) == null) {
            throw new RuntimeException("can not find tree!");
        }
        StructTree structTree1 = structTreeConvertor.convert(structTreeDO);

        StructTree tree3 = StructTreeGenerator.mergeTree(structTree1, tree2);
        System.out.println(tree3.getSchemaStr());
    }
}

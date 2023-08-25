package org.zhangyan;

import org.zhangyan.converter.StructTreeConverter;
import org.zhangyan.data.StructTree;

class StructTreeConverterTest {

    @org.junit.jupiter.api.Test
    void mergeTree() {

        StructTree tree1= new StructTree("tree","{\"layer1-3\":122.2,\"layer1-2\":[\"test1\",\"test2\"],\"layer1-1\":{\"layer2-2\":\"steam\",\"layer2-1\":\"wegame\"},\"layer1-5\":278.222}");
        StructTree tree2= new StructTree("tree","{\"layer1-4\":278.33,\"layer1-3\":171,\"layer1-2\":[\"tea\",\"tcoco\"],\"layer1-1\":{\"layer2-3\":\"ll\",\"layer2-2\":\"cheat\",\"layer2-4\":\"heat\"}}");
        System.out.println(tree1.getSchemaStr());
        System.out.println(tree2.getSchemaStr());
        StructTree tree3 = StructTreeConverter.mergeTree(tree1, tree2);
        System.out.println(tree3.getSchemaStr());
    }
}
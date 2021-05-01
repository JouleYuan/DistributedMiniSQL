package cn.edu.zju.minisql.distributed.server.master.service.impl;

import org.apache.curator.framework.recipes.cache.ChildData;

import java.nio.charset.StandardCharsets;

public class ZookeeperServiceImpl {

    public static void onCreation(ChildData node) {
        if(node.getPath().equals("/")) return;
        System.out.println("Node created");
        System.out.println(node.getPath().substring(1));
        //System.out.println(new String(node.getData(), StandardCharsets.UTF_8));
    }

    public static void onDeletion(ChildData oldNode) {
        System.out.println("Node deleted");
        System.out.println(oldNode.getPath().substring(1));
        //System.out.println(new String(oldNode.getData(), StandardCharsets.UTF_8));
    }
}

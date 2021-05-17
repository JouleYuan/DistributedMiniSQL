package cn.edu.zju.minisql.distributed.server.master.service.impl;

import cn.edu.zju.minisql.distributed.server.master.Config;
import cn.edu.zju.minisql.distributed.server.master.region.RegionManager;
import cn.edu.zju.minisql.distributed.server.master.table.TableManager;
import org.apache.curator.framework.recipes.cache.ChildData;

import java.util.List;

public class ZookeeperServiceImpl {

    public static void onCreation(ChildData node) {
        if(node.getPath().equals("/")) return;
        String address = node.getPath().substring(1);
        String path = new String(node.getData());
        System.out.println("Node created: " + address + " (data path: " + path + ")");

        RegionManager.addRegionServer(address, path);
        checkStatus();
    }

    public static void onDeletion(ChildData oldNode) {
        String address = oldNode.getPath().substring(1);
        String path = new String(oldNode.getData());
        System.out.println("Node deleted: " + address + " (data path: " + path + ")");

        List<String> tables = RegionManager.getRegionServer(address).getTables();

        RegionManager.removeRegionServer(address);
        checkStatus();

        for(String table: tables) TableManager.replaceRegion(table, address);
    }

    private static void checkStatus() {
        int regionNum = RegionManager.getRegionNum();
        System.out.println("current number of region servers: " + regionNum);
        if(regionNum < Config.minRegionSize) {
            System.out.println("Too few region servers are working! At least " +
                    Config.minRegionSize + " region servers are needed!");
        }
    }
}

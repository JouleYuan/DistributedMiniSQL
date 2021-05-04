package cn.edu.zju.minisql.distributed.server.master.service.impl;

import cn.edu.zju.minisql.distributed.server.master.Config;
import cn.edu.zju.minisql.distributed.server.master.region.RegionManager;
import cn.edu.zju.minisql.distributed.server.master.table.TableManager;
import org.apache.curator.framework.recipes.cache.ChildData;

import java.util.List;

public class ZookeeperServiceImpl {

    public static void onCreation(ChildData node) {
        if(node.getPath().equals("/")) return;
        String info = node.getPath().substring(1);
        System.out.println("Node created: " + info);

        RegionManager.addRegionServer(info);
        checkStatus();
    }

    public static void onDeletion(ChildData oldNode) {
        String info = oldNode.getPath().substring(1);
        System.out.println("Node deleted: " + info);

        String[] str = info.split("&", 2);

        List<String> tables = RegionManager.getRegionServer(str[0]).getTables();

        RegionManager.removeRegionServer(str[0]);
        checkStatus();

        for(String table: tables) TableManager.replaceRegion(table, str[0]);
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

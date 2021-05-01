package cn.edu.zju.minisql.distributed.server.master;

import cn.edu.zju.minisql.distributed.server.master.service.ThriftService;
import cn.edu.zju.minisql.distributed.server.master.service.ZookeeperService;

public class MasterServer {
    public static void main(String[] args) {
        System.out.println("Starting the master server...");
        ZookeeperService.listenRegionServer();
        ThriftService.exposeInterface();
    }
}

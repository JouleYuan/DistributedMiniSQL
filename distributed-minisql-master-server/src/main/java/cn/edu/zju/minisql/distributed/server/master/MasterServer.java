package cn.edu.zju.minisql.distributed.server.master;

import cn.edu.zju.minisql.distributed.server.master.service.ThriftService;
import cn.edu.zju.minisql.distributed.server.master.service.ZookeeperService;

public class MasterServer {
    public static void main(String[] args) {
        Config.init();
        System.out.println("Starting the master server...");
        ZookeeperService.listenRegionServer(Config.Zookeeper.ip, Config.Zookeeper.port, Config.Zookeeper.timeout,
                Config.Zookeeper.baseSleepTime, Config.Zookeeper.maxRetries, Config.Zookeeper.namespace);
        ThriftService.exposeInterface(Config.Thrift.port);
    }
}

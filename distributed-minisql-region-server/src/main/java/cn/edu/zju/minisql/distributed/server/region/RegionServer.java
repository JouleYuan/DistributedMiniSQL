package cn.edu.zju.minisql.distributed.server.region;

import cn.edu.zju.minisql.distributed.server.region.service.ThriftService;
import cn.edu.zju.minisql.distributed.server.region.service.ZookeeperService;

public class RegionServer {
    public static void main(String[] args) {
        Config.init();
        System.out.println("Starting the region server...");
        ZookeeperService.register(Config.Zookeeper.ip, Config.Zookeeper.port, Config.Zookeeper.timeout,
                Config.Zookeeper.baseSleepTime, Config.Zookeeper.maxRetries, Config.Zookeeper.namespace,
                Config.Thrift.port, Config.Minisql.path);
        ThriftService.exposeInterface(Config.Thrift.port);
    }
}

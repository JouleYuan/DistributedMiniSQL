package cn.edu.zju.minisql.distributed.server.region;

import cn.edu.zju.minisql.distributed.server.region.service.ThriftService;
import cn.edu.zju.minisql.distributed.server.region.service.ZookeeperService;

public class RegionServer {

    public static void main(String[] args) {
        Config.init();
        DirectoryManager.init(Config.Minisql.path);
        ZookeeperService.register(Config.Zookeeper.ip, Config.Zookeeper.port, Config.Zookeeper.connectionTimeout,
                Config.Zookeeper.sessionTimeout ,Config.Zookeeper.baseSleepTime, Config.Zookeeper.maxRetries,
                Config.Zookeeper.namespace, Config.Thrift.port, Config.Minisql.path);
        ThriftService.exposeInterface(Config.Thrift.port);
    }
}

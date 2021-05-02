package cn.edu.zju.minisql.distributed.server.region;

import cn.edu.zju.minisql.distributed.server.region.service.ThriftService;
import cn.edu.zju.minisql.distributed.server.region.service.ZookeeperService;

public class RegionServer {
    public static void main(String[] args) {
        System.out.println("Starting the region server...");
        ZookeeperService.register(System.getProperty("user.dir"));
        ThriftService.exposeInterface();
    }
}

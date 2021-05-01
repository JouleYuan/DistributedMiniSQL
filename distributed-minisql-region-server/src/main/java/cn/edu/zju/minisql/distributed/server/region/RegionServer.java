package cn.edu.zju.minisql.distributed.server.region;

import cn.edu.zju.minisql.distributed.server.region.service.ZookeeperService;

public class RegionServer {
    public static void main(String[] args) {
        ZookeeperService.register(0);
    }
}

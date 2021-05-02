package cn.edu.zju.minisql.distributed.server.region.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetAddress;

public class ZookeeperService {
    private static final String ADDRESS = "localhost:2181";
    private static final String NAMESPACE = "region-server";

    public static void register(String path) {
        try {
            CuratorFramework client = CuratorFrameworkFactory.builder()
                    .connectString(ADDRESS)
                    .connectionTimeoutMs(5000)
                    .retryPolicy(new ExponentialBackoffRetry(1000,3))
                    .namespace(NAMESPACE)
                    .build();
            client.start();

            client.create().withMode(CreateMode.EPHEMERAL)
                    .forPath("/" + InetAddress.getLocalHost().getHostAddress() + "&" + path, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
